package tasks

import cats.effect.Temporal
import service.RefresherService

import scala.concurrent.duration._

case class RefresherTask[F[_]: Temporal](refresherService: RefresherService[F]) {

  def start(): fs2.Stream[F, Unit] = {
    fs2.Stream.awakeEvery[F](1.minutes) >> fs2.Stream.eval(refresherService.refresh)
  }

}
