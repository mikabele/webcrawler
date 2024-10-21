package storage.redis

import cats.data.EitherT
import cats.syntax.all._
import cats.{Functor, MonadThrow}
import dev.profunktor.redis4cats.RedisCommands
import model.ParsedDocument
import model.types.UrlString
import storage.DocumentCache
import storage.redis.RedisDocumentCache.RedisDocumentCacheConfig

import scala.concurrent.duration.FiniteDuration

case class RedisDocumentCache[F[_]: Functor: MonadThrow](
    redis: RedisCommands[F, UrlString, ParsedDocument],
    config: RedisDocumentCacheConfig)
  extends DocumentCache[F] {
  override def get(key: UrlString): F[Either[Throwable, Option[ParsedDocument]]] = ???

  override def set(key: UrlString, value: ParsedDocument): F[Either[Throwable, Unit]] = ???

  override def remove(key: UrlString): F[Either[Throwable, Unit]] = ???

  override def getMany(keys: Seq[UrlString]): F[Either[Throwable, Map[UrlString, ParsedDocument]]] =
    redis.mGet(keys.toSet).attempt

  override def setMany(parsedDocsMap: Map[UrlString, ParsedDocument]): F[Either[Throwable, Unit]] = (for {
    _ <- EitherT(redis.mSet(parsedDocsMap).attempt)
    _ <- EitherT(expireMany(parsedDocsMap.keys.toSeq))
  } yield ()).value

  private def expireMany(keys: Seq[UrlString]): F[Either[Throwable, Unit]] = {
    keys.traverse(key => redis.expire(key, config.expirationTime)).as(()).attempt
  }
}

object RedisDocumentCache {
  case class RedisDocumentCacheConfig(expirationTime: FiniteDuration)
}
