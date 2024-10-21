package mapper

import io.scalaland.chimney.dsl._
import model.types.UrlString
import model.{Document, ParsedDocument}
import org.jsoup.nodes.{Document => JSoupDocument}

object DocumentMapper {

  def toParsedDocumentTuple(doc: Document): (UrlString, ParsedDocument) =
    doc.url -> doc.transformInto[ParsedDocument]

  def toParsedDocument(doc: JSoupDocument): ParsedDocument =
    ParsedDocument(doc.title(), doc.baseUri(), doc.html())
}
