package logic

import model.ParsedDocument

trait DocumentService[F[_]] {
  def processDocument(doc: ParsedDocument): F[Either[Throwable, Unit]]
}
