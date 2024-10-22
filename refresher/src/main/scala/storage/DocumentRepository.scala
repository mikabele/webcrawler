package storage

import model.Document

import java.time.Instant

trait DocumentRepository[F[_]] {
  def getAllByLastUpdatedLessThan(before: Instant): fs2.Stream[F, Document]
}
