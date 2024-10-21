package mapper

import io.scalaland.chimney.dsl._
import model.{Document, ParsedDocument}
import mongo4cats.bson.ObjectId

object DocumentMapper {

  def toDocument(parsedDocument: ParsedDocument): Document =
    parsedDocument.into[Document].withFieldConst(_._id, ObjectId.gen).transform
}
