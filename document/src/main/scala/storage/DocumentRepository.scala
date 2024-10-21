package storage

import model.Document

trait DocumentRepository[F[_]] {
  def saveAll(documents: Seq[Document]): F[Either[Throwable, Unit]]
  def save(document: Document): F[Either[Throwable, Unit]]
}
