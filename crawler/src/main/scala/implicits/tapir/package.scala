package implicits

import model.dto.TitlesResponse
import sttp.tapir.Schema

package object tapir {

  implicit lazy val titlesResponseSchema: Schema[TitlesResponse] = Schema.derived[TitlesResponse]

  implicit lazy val throwableSchema: Schema[Throwable] = Schema.any
}
