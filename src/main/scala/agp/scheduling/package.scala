package agp

import agp.Utils.OnMetReq
import agp.vo.Talk

import scala.language.higherKinds

package object scheduling {

  type ConferenceTracksScheduling = Set[Talk] => Set[ConferenceTrack]
  type ConferenceTracksScheduling2 = Set[Talk] => OnMetReq[Set[ConferenceTrack]]

  /* specialized exception */
  final case class Exception private[scheduling](message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
}
