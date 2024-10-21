import _root_.consumer.DocumentConsumer
import cats.effect.{Async, IO, IOApp, Resource}
import cats.syntax.all._
import fs2.kafka.Deserializer._
import fs2.kafka._
import implicits.circe.parsedDocumentCodec
import implicits.mongo.documentMongoCodec
import io.circe.parser.decode
import logic.impl.DocumentServiceImpl
import model.{Document, ParsedDocument}
import mongo4cats.client.MongoClient
import storage.mongo.MongoDocumentRepository

object DocumentConsumerApp extends IOApp.Simple {

  override def run: IO[Unit] = buildApp[IO].use(cons => cons.consume.compile.drain)

  def buildApp[F[_]: Async]: Resource[F, DocumentConsumer[F]] = for {
    consumer <- buildConsumer
    mongoClient <- MongoClient.fromConnectionString[F]("mongodb://localhost:27017")
    mongoCollection <- Resource.eval(
      mongoClient.getDatabase("test").flatMap(_.getCollectionWithCodec[Document]("documents"))
    )
    documentRepository = MongoDocumentRepository(mongoCollection)
    documentService = DocumentServiceImpl[F](documentRepository)
    documentConsumer = DocumentConsumer[F](consumer, documentService)
  } yield documentConsumer

  def buildConsumer[F[_]: Async]: Resource[F, KafkaConsumer[F, Option[String], ParsedDocument]] = {
    val keyDeserializer = GenericDeserializer[F, Option[String]]
    val valueDeserializer =
      GenericDeserializer[F].map(barr =>
        decode[ParsedDocument](String.valueOf(barr)).getOrElse(ParsedDocument("", "", ""))
      )
    val kafkaConsumerSettings = ConsumerSettings(keyDeserializer, valueDeserializer)
    KafkaConsumer
      .resource(
        kafkaConsumerSettings
          .withBootstrapServers("localhost:9092")
          .withGroupId("document-service-consumer")
          .withAutoOffsetReset(AutoOffsetReset.Latest)
      )
      .evalTap(_.subscribeTo("test"))
  }
}
