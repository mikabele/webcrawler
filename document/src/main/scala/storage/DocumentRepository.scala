package storage

import model.Document
import model.types.UrlString

trait DocumentRepository[F[_]] {
  def getManyByUrl(urls: Seq[UrlString]) : F[Either[Throwable, Seq[Document]]]
}
