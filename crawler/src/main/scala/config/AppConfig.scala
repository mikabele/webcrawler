package config

import config.AppConfig.{KafkaConfig, MongoConfig, RedisConfig}

case class AppConfig(mongoConfig: MongoConfig, kafkaConfig: KafkaConfig, redisConfig: RedisConfig)

object AppConfig {
  case class MongoConfig(connectionString: String, database: String)

  case class KafkaConfig(bootstrapServers: String)

  case class RedisConfig(connectionString: String)
}
