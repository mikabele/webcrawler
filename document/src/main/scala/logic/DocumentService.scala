package logic

import document.document.Document

trait DocumentService[F[_]] {
  def processDocument(doc: Document): F[Either[Throwable, Unit]]
}
