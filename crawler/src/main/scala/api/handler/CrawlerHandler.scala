package api.handler

import api.Handler
import api.endpoints.CrawlerEndpoints.getTitlesEndpoint
import cats.Functor
import cats.data.EitherT
import model.dto.TitlesResponse
import model.types.UrlString
import service.CrawlerService
import sttp.model.QueryParams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full

case class CrawlerHandler[F[_]: Functor](crawlerService: CrawlerService[F]) extends Handler[F] {

  private val getTitles: Full[Unit, Unit, QueryParams, Throwable, TitlesResponse, Any, F] =
    getTitlesEndpoint.serverLogic { queryParams =>
      val urls: Seq[UrlString] = queryParams.getMulti("url").toSeq.flatten
      EitherT(crawlerService.getTitles(urls)).map(titlesMap => TitlesResponse(titlesMap)).value
    }

  override def endpoints: List[ServerEndpoint[Any, F]] = List(getTitles)
}
