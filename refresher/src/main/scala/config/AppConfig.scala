package config

import config.AppConfig.{KafkaConfig, MongoConfig}

import scala.concurrent.duration.FiniteDuration

case class AppConfig(awakePeriod: FiniteDuration, mongoConfig: MongoConfig, kafkaConfig: KafkaConfig)

object AppConfig {
  case class MongoConfig(connectionString: String, database: String)

  case class KafkaConfig(bootstrapServers: String)
}
