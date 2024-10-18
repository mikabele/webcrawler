package implicits

import io.circe.{Codec, Decoder}
import io.circe.refined.CirceCodecRefined
import io.circe.generic.semiauto._
import model.dto.TitlesResponse
import model.types.UrlString

package object circe extends CirceCodecRefined{

  implicit lazy val urlStringCodec: Decoder[UrlString] = refinedDecoder

  implicit lazy val titlesResponseCodec: Codec[TitlesResponse] = deriveCodec[TitlesResponse]
}
