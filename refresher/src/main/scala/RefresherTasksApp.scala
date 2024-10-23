import cats.effect.{Async, IO, IOApp, Resource}
import cats.syntax.all._
import config.AppConfig
import document.document.{Document => KafkaDocument}
import fs2.kafka.{GenericSerializer, KafkaProducer, ProducerSettings}
import implicits.config.appConfigReader
import implicits.kafka.kafkaGeneratedMessageSerializer
import implicits.mongo.documentMongoCodec
import model.Document
import mongo4cats.client.MongoClient
import parser.impl.ParserClientImpl
import pureconfig.ConfigSource
import service.impl.RefresherServiceImpl
import storage.mongo.MongoDocumentRepository
import tasks.RefresherTask

object RefresherTasksApp extends IOApp.Simple {

  override def run: IO[Unit] = buildTasks[IO].use(tasks => tasks.map(_.start().compile.drain).parSequence_)

  private def buildTasks[F[_]: Async]: Resource[F, List[RefresherTask[F]]] = {

    for {
      appConf <- Resource.pure(ConfigSource.default.loadOrThrow[AppConfig])
      mongoClient <- MongoClient.fromConnectionString[F](appConf.mongoConfig.connectionString)
      mongoCollection <- Resource.eval(
        mongoClient.getDatabase(appConf.mongoConfig.database).flatMap(_.getCollectionWithCodec[Document]("documents"))
      )
      documentRepository = MongoDocumentRepository(mongoCollection)
      parserClient = ParserClientImpl[F]()
      kafkaProducer <- KafkaProducer.resource[F, Option[String], KafkaDocument](
        ProducerSettings(GenericSerializer[F, Option[String]], GenericSerializer[F, KafkaDocument])
          .withBootstrapServers(appConf.kafkaConfig.bootstrapServers)
      )
      refresherService = RefresherServiceImpl(documentRepository, parserClient, kafkaProducer)
      refresherTask = RefresherTask(refresherService,appConf)
    } yield List(refresherTask)
  }
}
