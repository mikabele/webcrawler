package mapper

import document.document.{Document => KafkaDocument}
import io.scalaland.chimney.dsl._
import model.types.UrlString
import model.{Document, ParsedDocument}

object DocumentMapper {

  def toParsedDocumentTuple(doc: Document): (UrlString, ParsedDocument) =
    doc.url -> doc.transformInto[ParsedDocument]

  def toKafkaDocument(url: UrlString, doc: ParsedDocument): KafkaDocument =
    KafkaDocument.of(doc.title, url, doc.htmlText)
}
