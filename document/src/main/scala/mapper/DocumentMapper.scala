package mapper

import document.document.{Document => KafkaDocument}
import io.scalaland.chimney.dsl._
import model.Document
import mongo4cats.bson.ObjectId

import java.time.Instant

object DocumentMapper {

  def toDocument(kafkaDocument: KafkaDocument, now: Instant): Document =
    kafkaDocument.into[Document].withFieldConst(_._id, ObjectId.gen).withFieldConst(_.lastUpdated, now).transform
}
