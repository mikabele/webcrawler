package storage

import model.ParsedDocument
import model.types.UrlString

import scala.concurrent.duration.Duration

trait DocumentCache[F[_]] {
  def get(key: UrlString): F[Either[Throwable, Option[ParsedDocument]]]
  def set(key: UrlString, value: ParsedDocument, expiration: Duration): F[Either[Throwable, Unit]]
  def remove(key: UrlString): F[Either[Throwable, Unit]]
  def getMany(keys: Seq[UrlString]): F[Either[Throwable, Map[UrlString, Option[ParsedDocument]]]]
}
