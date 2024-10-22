package logic.impl

import cats.Monad
import cats.syntax.all._
import cats.effect.kernel.Clock
import document.document.{Document => KafkaDocument}
import logic.DocumentService
import mapper.DocumentMapper
import storage.DocumentRepository

import java.time.Instant

case class DocumentServiceImpl[F[_]: Clock: Monad](documentRepository: DocumentRepository[F]) extends DocumentService[F] {

  /* TODO: add check for already saved html pages but on other urls
   */
  override def processDocument(document: KafkaDocument): F[Either[Throwable, Unit]] = for {
    now <- Clock[F].realTime.map(fd => Instant.ofEpochMilli(fd.toMillis))
     doc = DocumentMapper.toDocument(document, now)
    res <- documentRepository.save(doc)
  } yield res
}
