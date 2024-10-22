import cats.effect.{Async, IO, IOApp, Resource}
import document.document.{Document => KafkaDocument}
import fs2.kafka.{GenericSerializer, KafkaProducer, ProducerSettings}
import model.{Document, ParsedDocument}
import cats.syntax.all._
import implicits.mongo.documentMongoCodec
import mongo4cats.client.MongoClient
import parser.impl.ParserClientImpl
import service.impl.RefresherServiceImpl
import storage.mongo.MongoDocumentRepository
import tasks.RefresherTask

object RefresherTasksApp extends IOApp.Simple {

  def buildTasks[F[_]: Async]: Resource[F, List[RefresherTask[F]]] = {
    val keySerializer = GenericSerializer[F, Option[String]]
    val valueSerializer = GenericSerializer[F].contramap[KafkaDocument](doc => doc.toByteArray)

    for {
      mongoClient <- MongoClient.fromConnectionString[F]("mongodb://localhost:27017")
      mongoCollection <- Resource.eval(
        mongoClient.getDatabase("test").flatMap(_.getCollectionWithCodec[Document]("documents"))
      )
      documentRepository = MongoDocumentRepository(mongoCollection)
      parserClient = ParserClientImpl[F]()
      kafkaProducer <- KafkaProducer.resource[F, Option[String], KafkaDocument](
        ProducerSettings(keySerializer, valueSerializer).withBootstrapServers("localhost:9092")
      )
      refresherService = RefresherServiceImpl(documentRepository, parserClient, kafkaProducer)
      refresherTask = RefresherTask(refresherService)
    } yield List(refresherTask)
  }

  override def run: IO[Unit] = buildTasks[IO].use(tasks => tasks.map(_.start().compile.drain).parSequence_)
}
