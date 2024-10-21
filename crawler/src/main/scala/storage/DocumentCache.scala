package storage

import model.ParsedDocument
import model.types.UrlString

trait DocumentCache[F[_]] {
  def get(key: UrlString): F[Either[Throwable, Option[ParsedDocument]]]
  def set(key: UrlString, value: ParsedDocument): F[Either[Throwable, Unit]]
  def remove(key: UrlString): F[Either[Throwable, Unit]]
  def getMany(keys: Seq[UrlString]): F[Either[Throwable, Map[UrlString, ParsedDocument]]]

  def setMany(parsedDocsMap: Map[UrlString, ParsedDocument]): F[Either[Throwable, Unit]]
}
