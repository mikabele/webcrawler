import api.Handler
import api.handler.CrawlerHandler
import cats.effect.kernel.Async
import cats.effect.{IO, IOApp, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import config.AppConfig
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.codecs.Codecs
import dev.profunktor.redis4cats.codecs.splits.SplitEpi
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.effect.Log.Stdout.instance
import document.document.{Document => KafkaDocument}
import fs2.kafka.{GenericSerializer, KafkaProducer, ProducerSettings}
import implicits.circe.parsedDocumentCodec
import implicits.config.appConfigReader
import implicits.kafka.kafkaGeneratedMessageSerializer
import implicits.mongo.documentMongoCodec
import io.circe.parser.decode
import io.circe.syntax._
import model.{Document, ParsedDocument}
import mongo4cats.client._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import parser.impl.ParserClientImpl
import pureconfig.ConfigSource
import service.impl.CrawlerServiceImpl
import storage.mongo.MongoDocumentRepository
import storage.redis.RedisDocumentCache
import storage.redis.RedisDocumentCache.RedisDocumentCacheConfig
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.concurrent.duration._

object CrawlerServer extends IOApp.Simple {

  override def run: IO[Unit] = bootstrap[IO].use(_ => IO.never)

  private def bootstrap[F[_]: Async: Log]: Resource[F, Server] = {
    val cacheConfig = RedisDocumentCacheConfig(7.days)
    val parsedDocEpi = SplitEpi[String, ParsedDocument](
      str => decode[ParsedDocument](str).getOrElse(ParsedDocument(None, "")),
      _.asJson.noSpaces
    )
    val redisCodec = Codecs.derive(RedisCodec.Utf8, parsedDocEpi)

    for {
      appConf <- Resource.pure(ConfigSource.default.loadOrThrow[AppConfig])
      mongoClient <- MongoClient.fromConnectionString[F](appConf.mongoConfig.connectionString)
      mongoCollection <- Resource.eval(
        mongoClient.getDatabase(appConf.mongoConfig.database).flatMap(_.getCollectionWithCodec[Document]("documents"))
      )
      documentRepository = MongoDocumentRepository(mongoCollection)
      redis <- Redis[F].simple(appConf.redisConfig.connectionString, redisCodec)
      docCache = RedisDocumentCache(redis, cacheConfig)
      parserClient = ParserClientImpl[F]()
      kafkaProducer <- KafkaProducer.resource[F, Option[String], KafkaDocument](
        ProducerSettings(GenericSerializer[F, Option[String]], GenericSerializer[F, KafkaDocument])
          .withBootstrapServers(appConf.kafkaConfig.bootstrapServers)
      )
      crawlerService = CrawlerServiceImpl(docCache, documentRepository, parserClient, kafkaProducer)
      crawlerHandler = CrawlerHandler(crawlerService)
      handlers = List(crawlerHandler)
      server <- buildServer(handlers)
    } yield server
  }

  private def buildServer[F[_]: Async](handlers: List[Handler[F]]): Resource[F, Server] = {
    val endpoints = handlers.flatMap(_.endpoints)
    val swaggerEndpoints = SwaggerInterpreter().fromServerEndpoints[F](endpoints, "Crawler", "1.0")
    val fullEndpoints = endpoints ++ swaggerEndpoints
    val interpretedRoutes = Http4sServerInterpreter[F].toRoutes(fullEndpoints).orNotFound
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(interpretedRoutes)
      .build
  }
}
