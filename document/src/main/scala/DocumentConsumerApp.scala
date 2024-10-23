import _root_.consumer.DocumentConsumer
import cats.effect.{Async, IO, IOApp, Resource}
import cats.syntax.all._
import config.AppConfig
import document.document.{Document => KafkaDocument}
import fs2.kafka.Deserializer._
import fs2.kafka._
import implicits.config.appConfigReader
import implicits.kafka.kafkaGeneratedMessageDeserializer
import implicits.mongo.documentMongoCodec
import logic.impl.DocumentServiceImpl
import model.Document
import mongo4cats.client.MongoClient
import pureconfig.ConfigSource
import storage.mongo.MongoDocumentRepository

object DocumentConsumerApp extends IOApp.Simple {

  override def run: IO[Unit] = buildApp[IO].use(cons => cons.consume.compile.drain)

  def buildApp[F[_]: Async]: Resource[F, DocumentConsumer[F]] = for {
    appConf <- Resource.pure(ConfigSource.default.loadOrThrow[AppConfig])
    consumer <- buildConsumer(appConf)
    mongoClient <- MongoClient.fromConnectionString[F](appConf.mongoConfig.connectionString)
    mongoCollection <- Resource.eval(
      mongoClient.getDatabase(appConf.mongoConfig.database).flatMap(_.getCollectionWithCodec[Document]("documents"))
    )
    documentRepository = MongoDocumentRepository(mongoCollection)
    documentService = DocumentServiceImpl[F](documentRepository)
    documentConsumer = DocumentConsumer[F](consumer, documentService)
  } yield documentConsumer

  def buildConsumer[F[_]: Async](appConf: AppConfig): Resource[F, KafkaConsumer[F, Option[String], KafkaDocument]] = {
    val kafkaConsumerSettings =
      ConsumerSettings(GenericDeserializer[F, Option[String]], GenericDeserializer[F, KafkaDocument])
    KafkaConsumer
      .resource(
        kafkaConsumerSettings
          .withBootstrapServers(appConf.kafkaConfig.bootstrapServers)
          .withGroupId("document-service-consumer")
          .withAutoOffsetReset(AutoOffsetReset.Latest)
      )
      .evalTap(_.subscribeTo("documents_topic"))
  }
}
