package parser.impl

import cats.effect.Async
import cats.syntax.all._
import com.themillhousegroup.scoup.Scoup
import mapper.DocumentMapper
import model.ParsedDocument
import model.types.UrlString
import parser.ParserClient

case class ParserClientImpl[F[_]: Async]() extends ParserClient[F] {

  override def fetchAndParse(url: UrlString): F[Either[Throwable, ParsedDocument]] = {
    Async[F].fromFuture(Scoup.parse(url).pure).map(DocumentMapper.toParsedDocument).attempt
  }
}
