package implicits

import implicits.circe.documentCodec
import model.Document
import mongo4cats.circe.deriveCirceCodecProvider
import mongo4cats.codecs.MongoCodecProvider

package object mongo {
  implicit lazy val documentMongoCodec: MongoCodecProvider[Document] = deriveCirceCodecProvider[Document]
}
