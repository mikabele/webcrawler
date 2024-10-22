package service.impl

import cats.MonadThrow
import cats.data.EitherT
import cats.syntax.all._
import document.document.{Document => KafkaDocument}
import fs2.Chunk
import fs2.kafka.{KafkaProducer, ProducerRecord}
import mapper.DocumentMapper
import model.types.UrlString
import model.{Document, ParsedDocument}
import parser.ParserClient
import service.CrawlerService
import storage.{DocumentCache, DocumentRepository}

case class CrawlerServiceImpl[F[_]: MonadThrow](
    docCache: DocumentCache[F],
    documentRepository: DocumentRepository[F],
    parserClient: ParserClient[F],
    kafkaProducer: KafkaProducer[F, Option[String], KafkaDocument])
  extends CrawlerService[F] {

  override def getTitles(urls: Seq[UrlString]): F[Either[Throwable, Map[UrlString, Option[String]]]] = (for {
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
      docsFromDb <-
        if (missedFromCacheUrls.nonEmpty) getDocsFromDbAndSave(missedFromCacheUrls)
        else EitherT.pure[F, Throwable](Seq.empty)
      parsedDocsMap = docsFromDb.map(DocumentMapper.toParsedDocumentTuple).toMap
    } yield docsFromCache ++ parsedDocsMap

  private def getDocsFromDbAndSave(missedFromCacheUrls: Seq[UrlString]): EitherT[F, Throwable, Seq[Document]] = for {
    docsFromDb <- EitherT(documentRepository.getManyByUrl(missedFromCacheUrls))
    parsedDocsMap = docsFromDb.map(DocumentMapper.toParsedDocumentTuple).toMap
    _ <- EitherT(docCache.setMany(parsedDocsMap)).whenA(parsedDocsMap.nonEmpty)
  } yield docsFromDb

  private def fetchAndParseDoc(url: UrlString): EitherT[F, Throwable, (UrlString, ParsedDocument)] = {
    EitherT(parserClient.fetchAndParse(url)).map(doc => url -> doc)
  }

  private def sendDocumentsToKafka(newDocs: Map[UrlString, ParsedDocument]): F[Either[Throwable, Unit]] = {
    val records = Chunk.from(newDocs.map { case (url, doc) =>
      val value = DocumentMapper.toKafkaDocument(url, doc)
      ProducerRecord("documents_topic", None, value)
    })
    kafkaProducer.produce(records).flatten.void.attempt
  }
}
