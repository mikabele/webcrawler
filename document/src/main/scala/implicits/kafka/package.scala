package implicits

import cats.effect.kernel.Sync
import fs2.kafka.{GenericDeserializer, KeyOrValue}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

package object kafka {

  implicit def kafkaGeneratedMessageDeserializer[
      F[_]: Sync,
      A <: GeneratedMessage: GeneratedMessageCompanion]: GenericDeserializer[KeyOrValue, F, A] =
    GenericDeserializer[F].map(implicitly[GeneratedMessageCompanion[A]].parseFrom)

}
