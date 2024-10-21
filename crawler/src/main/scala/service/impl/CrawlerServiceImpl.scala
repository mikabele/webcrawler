package service.impl

import cats.MonadThrow
import cats.data.EitherT
import cats.syntax.all._
import fs2.Chunk
import fs2.kafka.{KafkaProducer, ProducerRecord}
import mapper.DocumentMapper
import model.ParsedDocument
import model.types.UrlString
import parser.ParserClient
import service.CrawlerService
import storage.{DocumentCache, DocumentRepository}

case class CrawlerServiceImpl[F[_]: MonadThrow](
    docCache: DocumentCache[F],
    documentRepository: DocumentRepository[F],
    parserClient: ParserClient[F],
    kafkaProducer: KafkaProducer[F, Option[String], ParsedDocument])
  extends CrawlerService[F] {

  override def getTitles(urls: Seq[UrlString]): F[Either[Throwable, Map[UrlString, String]]] = (for {
    docsFromCache <- getDocsFromCacheAndSave(urls)
    missedFromCacheUrls = urls.diff(docsFromCache.keys.toSeq)
    newDocs <- missedFromCacheUrls.traverse(fetchAndParseDoc)
    _ <- EitherT(sendDocumentsToKafka(newDocs.toMap))
    res = (docsFromCache ++ newDocs).map { case (key, value) => key -> value.title }
  } yield res).value

  private def getDocsFromCacheAndSave(urls: Seq[UrlString]): EitherT[F, Throwable, Map[UrlString, ParsedDocument]] =
    for {
      docsFromCache <- EitherT(docCache.getMany(urls))
      missedFromCacheUrls = urls.diff(docsFromCache.keys.toSeq)
      docsFromDb <- EitherT(documentRepository.getManyByUrl(missedFromCacheUrls))
      parsedDocsMap = docsFromDb.map(DocumentMapper.toParsedDocumentTuple).toMap
      _ <- EitherT(docCache.setMany(parsedDocsMap)).whenA(parsedDocsMap.nonEmpty)
    } yield docsFromCache ++ parsedDocsMap

  private def fetchAndParseDoc(url: UrlString): EitherT[F, Throwable, (UrlString, ParsedDocument)] = {
    EitherT(parserClient.fetchAndParse(url)).map(doc => url -> doc)
  }

  private def sendDocumentsToKafka(newDocs: Map[UrlString, ParsedDocument]): F[Either[Throwable, Unit]] = {
    val records = Chunk.from(newDocs.map { case (_, doc) => ProducerRecord("test", Option.empty[String], doc) })
    kafkaProducer.produce(records).flatten.void.attempt
  }
}
