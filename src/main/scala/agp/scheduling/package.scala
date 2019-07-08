package agp

import agp.vo.{Event, Talk}

import scala.language.higherKinds

package object scheduling {
  type ConferenceTracksScheduling = Set[Talk] => Set[ConferenceTrack]

  /* specialized exception */
  final case class Exception private[scheduling](message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
}
