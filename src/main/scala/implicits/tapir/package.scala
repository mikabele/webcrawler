package implicits

import model.dto.TitlesResponse
import sttp.tapir.Schema

import sttp.tapir.Schema

package object tapir {

  implicit lazy val titlesResponseSchema: Schema[TitlesResponse] =
}
