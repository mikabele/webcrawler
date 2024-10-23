package parser.impl

import cats.effect.Async
import cats.syntax.all._
import com.themillhousegroup.scoup.Scoup
import model.ParsedDocument
import model.types.UrlString
import parser.ParserClient

case class ParserClientImpl[F[_]: Async]() extends ParserClient[F] {

  override def fetchAndParse(url: UrlString): F[Either[Throwable, ParsedDocument]] = {
    Async[F].fromFuture(Scoup.parse(url).pure).map(doc => ParsedDocument(Option(doc.title()), doc.html())).attempt
  }
}
