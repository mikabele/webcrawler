package parser

import model.ParsedDocument
import model.types.UrlString

trait ParserClient[F[_]] {
  def fetchAndParse(url: UrlString): F[Either[Throwable, ParsedDocument]]
}
