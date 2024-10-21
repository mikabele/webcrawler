package implicits

import io.circe.Codec
import io.circe.generic.semiauto._
import model.{Document, ParsedDocument}
import mongo4cats.circe._

package object circe {


  implicit lazy val parsedDocumentCodec: Codec[ParsedDocument] = deriveCodec[ParsedDocument]

  implicit lazy val documentCodec: Codec[Document] = deriveCodec[Document]
}
