package mapper

import io.scalaland.chimney.dsl._
import model.{Document, ParsedDocument}
import mongo4cats.bson.ObjectId
import document.document.{Document => KafkaDocument}

import java.time.Instant

object DocumentMapper {

  def toDocument(kafkaDocument: KafkaDocument, now: Instant): Document =
    kafkaDocument.into[Document].withFieldConst(_._id, ObjectId.gen).withFieldConst(_.lastUpdated, now).transform
}
