package tasks

import cats.effect.Temporal
import config.AppConfig
import service.RefresherService

case class RefresherTask[F[_]: Temporal](refresherService: RefresherService[F], appConfig: AppConfig) {

  def start(): fs2.Stream[F, Unit] = {
    fs2.Stream.awakeEvery[F](appConfig.awakePeriod) >> fs2.Stream.eval(refresherService.refresh)
  }

}
