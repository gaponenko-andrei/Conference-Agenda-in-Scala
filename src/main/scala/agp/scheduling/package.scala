package agp

import agp.vo.Talk
import org.scalactic.Or

import scala.language.higherKinds

package object scheduling {

  /** Definition of function producing [[agp.scheduling.ConferenceTrack]] from talks */
  type ConferenceTracksScheduling = Set[Talk] => ExceptionOr[Set[ConferenceTrack]]

  /** Alias for package use only, bound to package-specific exception */
  private[scheduling] type ExceptionOr[A] = A Or scheduling.Exception

  /** Specialized exception that should be thrown by
    * every component of the package in case of error
    */
  final case class Exception (message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
}
