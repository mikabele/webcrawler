package service

trait RefresherService[F[_]] {
  def refresh: F[Unit]
}
