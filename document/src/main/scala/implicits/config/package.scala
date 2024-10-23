package implicits

import _root_.config.AppConfig
import _root_.config.AppConfig._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto._

package object config {
  implicit lazy val kafkaConfigReader: ConfigReader[KafkaConfig] = deriveReader[KafkaConfig]

  implicit lazy val mongoConfigReader: ConfigReader[MongoConfig] = deriveReader[MongoConfig]

  implicit lazy val appConfigReader: ConfigReader[AppConfig] = deriveReader[AppConfig]
}
