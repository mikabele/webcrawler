package api.endpoints

import _root_.model.dto.TitlesResponse
import implicits.circe._
import implicits.tapir._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object CrawlerEndpoints {

  val getTitlesEndpoint = endpoint.get
    .in("titles")
    .in(queryParams)
    .out(jsonBody[TitlesResponse])
    .errorOut(jsonBody[Throwable])
}
