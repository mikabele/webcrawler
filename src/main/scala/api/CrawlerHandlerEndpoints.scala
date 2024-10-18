package api

import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import _root_.model.types._
import _root_.model.dto.TitlesResponse
import implicits.circe._
import io.circe.generic.auto._

object CrawlerHandlerEndpoints {
  val getTitles = endpoint
    .get
    .in("titles")
    .in(queryParams)
    .out(jsonBody[TitlesResponse])
}
