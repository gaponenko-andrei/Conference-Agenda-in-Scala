package agp

import agp.vo.Talk
import org.scalactic.Or

import scala.language.higherKinds

package object scheduling {

  type ConferenceTracksScheduling = Set[Talk] => Set[ConferenceTrack]
  type ConferenceTracksScheduling2 = Set[Talk] => Set[ConferenceTrack] Or scheduling.Exception

  /* specialized exception */
  final case class Exception private[scheduling](message: String, cause: Throwable = null)
    extends IllegalArgumentException(message, cause)
}
