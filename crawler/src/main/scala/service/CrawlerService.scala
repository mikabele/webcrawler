package service

import model.types.UrlString

trait CrawlerService[F[_]] {
  def getTitles(urls: Seq[UrlString]): F[Either[Throwable, Map[UrlString, Option[String]]]]
}
