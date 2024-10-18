package api
import model.dto.TitlesResponse
import sttp.model.QueryParams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full

trait Handler[F[_]] {

  def endpoint: List[ServerEndpoint[Any, F]]
}
