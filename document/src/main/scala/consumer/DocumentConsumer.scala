package consumer

import cats.Monad
import cats.syntax.all._
import document.document.Document
import fs2.kafka.KafkaConsumer
import logic.DocumentService
import model.ParsedDocument

case class DocumentConsumer[F[_]: Monad](
    kafkaConsumer: KafkaConsumer[F, Option[String], Document],
    documentService: DocumentService[F]) {

  def consume: fs2.Stream[F, Unit] =
    kafkaConsumer.stream.evalMap(recordWithOffset =>
      for {
        res <- documentService.processDocument(recordWithOffset.record.value)
        _ <- res.map(_ => recordWithOffset.offset.commit).sequence
      } yield ()
    )
}
