package service.impl

import cats.data.EitherT
import cats.effect.kernel.{Clock, Sync}
import cats.syntax.all._
import document.document.{Document => KafkaDocument}
import fs2.kafka.{KafkaProducer, ProducerRecord}
import mapper.DocumentMapper
import model.types.UrlString
import model.{Document, ParsedDocument}
import parser.ParserClient
import service.RefresherService
import storage.DocumentRepository

import java.time.Instant
import scala.concurrent.duration._

case class RefresherServiceImpl[F[_]: Sync](
    documentRepository: DocumentRepository[F],
    parserClient: ParserClient[F],
    kafkaProducer: KafkaProducer[F, Option[String], KafkaDocument])
  extends RefresherService[F] {

  override def refresh: F[Unit] = for {
    nowFD <- Clock[F].realTime
    instantBefore = Instant.ofEpochMilli(nowFD.minus(1.minutes).toMillis)
    _ <- documentRepository
      .getAllByLastUpdatedLessThan(instantBefore)
      .evalMap(fetchAndSaveDocument)
      .compile
      .drain
  } yield ()

  private def fetchAndSaveDocument(document: Document): F[Unit] = {
    (for {
      parsedDoc <- EitherT(parserClient.fetchAndParse(document.url))
      _ <- EitherT(sendDocumentsToKafka(document.url, parsedDoc)).whenA(parsedDoc.title != document.title)
    } yield ()).value.rethrow
  }

  private def sendDocumentsToKafka(url: UrlString, doc: ParsedDocument): F[Either[Throwable, Unit]] = {
    val value = DocumentMapper.toKafkaDocument(url, doc)
    val record = ProducerRecord("documents_topic", None, value)

    kafkaProducer.produceOne(record).flatten.void.attempt
  }
}
