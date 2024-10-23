package mapper

import document.document.{Document => KafkaDocument}
import model.ParsedDocument
import model.types.UrlString

object DocumentMapper {

  def toKafkaDocument(url: UrlString, doc: ParsedDocument): KafkaDocument =
    KafkaDocument.of(doc.title, url, doc.htmlText)
}
