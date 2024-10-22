package implicits

import io.circe.Decoder._
import io.circe.generic.semiauto._
import io.circe.refined.CirceCodecRefined
import io.circe.{Codec, Decoder, Encoder}
import model.dto.TitlesResponse
import model.{Document, ParsedDocument}
import mongo4cats.circe._

import java.time.Instant


package object circe extends CirceCodecRefined {

  implicit lazy val instantCodec: Codec[Instant] = Codec.from(instantDecoder, instantEncoder)

  implicit lazy val titlesResponseCodec: Codec[TitlesResponse] = deriveCodec[TitlesResponse]

  implicit lazy val parsedDocumentCodec: Codec[ParsedDocument] = deriveCodec[ParsedDocument]

  implicit lazy val documentCodec: Codec[Document] = deriveCodec[Document]

  implicit lazy val throwableCodec: Codec[Throwable] =
    Codec.from(Decoder[String].map(str => new Exception(str)), Encoder[String].contramap[Throwable](_.getMessage))
}
