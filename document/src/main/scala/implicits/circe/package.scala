package implicits

import io.circe.Codec
import io.circe.generic.semiauto._
import model.{Document, ParsedDocument}
import mongo4cats.circe._

import java.time.Instant

package object circe {

  implicit lazy val instantCodec: Codec[Instant] = Codec.from(instantDecoder, instantEncoder)

  implicit lazy val parsedDocumentCodec: Codec[ParsedDocument] = deriveCodec[ParsedDocument]

  implicit lazy val documentCodec: Codec[Document] = deriveCodec[Document]
}
