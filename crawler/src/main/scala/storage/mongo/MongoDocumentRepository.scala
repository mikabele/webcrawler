package storage.mongo

import cats.MonadThrow
import cats.syntax.all._
import model.Document
import model.types.UrlString
import mongo4cats.collection.MongoCollection
import mongo4cats.operations.Filter
import storage.DocumentRepository

case class MongoDocumentRepository[F[_]: MonadThrow](collection: MongoCollection[F, Document])
  extends DocumentRepository[F] {

  override def getManyByUrl(urls: Seq[UrlString]): F[Either[Throwable, Seq[Document]]] =
    collection.find(Filter.in("url", urls)).all.map(_.toSeq).attempt
}
