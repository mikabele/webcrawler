package storage.mongo

import cats.MonadThrow
import cats.syntax.all._
import model.Document
import mongo4cats.collection.MongoCollection
import storage.DocumentRepository

case class MongoDocumentRepository[F[_]: MonadThrow](collection: MongoCollection[F, Document])
  extends DocumentRepository[F] {

  override def saveAll(documents: Seq[Document]): F[Either[Throwable, Unit]] =
    collection.insertMany(documents).void.attempt

  override def save(document: Document): F[Either[Throwable, Unit]] =
    collection.insertOne(document).void.attempt
}
