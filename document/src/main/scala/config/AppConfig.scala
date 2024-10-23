package config

import config.AppConfig.{KafkaConfig, MongoConfig}

case class AppConfig(mongoConfig: MongoConfig, kafkaConfig: KafkaConfig)

object AppConfig {
  case class MongoConfig(connectionString: String, database: String)

  case class KafkaConfig(bootstrapServers: String)
}
