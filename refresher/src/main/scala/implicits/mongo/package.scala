package implicits

import implicits.circe._
import model.Document
import mongo4cats.circe._
import mongo4cats.codecs.MongoCodecProvider

package object mongo {
  implicit lazy val documentMongoCodec: MongoCodecProvider[Document] = deriveCirceCodecProvider[Document]
}
