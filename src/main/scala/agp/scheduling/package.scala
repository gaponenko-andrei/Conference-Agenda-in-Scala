package agp

import agp.vo.Talk
import org.scalactic.Or

import scala.language.higherKinds

package object scheduling {

  /* specialized exception that should be thrown by every component of the package in case of failure */
  final case class Exception (message: String, cause: Throwable = null) extends RuntimeException(message, cause)

  private[scheduling] type ExceptionOr[T] = T Or scheduling.Exception

  type ConferenceTracksScheduling = Set[Talk] => Set[ConferenceTrack]
  type ConferenceTracksScheduling2 = Set[Talk] => ExceptionOr[Set[ConferenceTrack]]
}
