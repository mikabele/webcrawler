package mapper

import document.document.{Document => KafkaDocument}
import io.scalaland.chimney.dsl._
import model.types.UrlString
import model.{Document, ParsedDocument}
import org.jsoup.nodes.{Document => JSoupDocument}

object DocumentMapper {

  def toParsedDocumentTuple(doc: Document): (UrlString, ParsedDocument) =
    doc.url -> doc.transformInto[ParsedDocument]

  def toParsedDocument(doc: JSoupDocument): ParsedDocument =
    ParsedDocument(Option(doc.title()), doc.html())

  def toKafkaDocument(url: UrlString, doc: ParsedDocument): KafkaDocument =
    KafkaDocument.of(doc.title, url, doc.htmlText)
}
