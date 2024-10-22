package storage.mongo

import cats.MonadThrow
import model.Document
import mongo4cats.collection.MongoCollection
import mongo4cats.operations.Filter
import storage.DocumentRepository

import java.time.Instant

case class MongoDocumentRepository[F[_]: MonadThrow](collection: MongoCollection[F, Document])
  extends DocumentRepository[F] {

  override def getAllByLastUpdatedLessThan(before: Instant): fs2.Stream[F, Document] =
    collection.find(Filter.lt("lastUpdated", before)).stream
}
