package service.impl

import model.types.UrlString
import service.CrawlerService
import storage.DocumentCache

case class CrawlerServiceImpl[F[_]] (docCache : DocumentCache[F]) extends CrawlerService[F] {
  override def getTitles(urls: Seq[UrlString]): F[Either[Throwable, Map[UrlString, String]]] = for {

  } yield ()
}
