package implicits

import cats.effect.kernel.Sync
import fs2.kafka.{GenericSerializer, KeyOrValue}
import scalapb.GeneratedMessage

package object kafka {

  implicit def kafkaGeneratedMessageSerializer[F[_]: Sync, A <: GeneratedMessage]: GenericSerializer[KeyOrValue, F, A] =
    GenericSerializer[F].contramap(_.toByteArray)

}
