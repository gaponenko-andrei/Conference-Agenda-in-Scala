package agp

import agp.Utils.OnMetReq
import agp.vo._
import org.scalactic.Or

package object composition {

  /** Alias for package use only, bound to package-specific exception */
  private[composition] type ExceptionOr[T] = T Or composition.Exception

  /* todo scaladoc */
  type KnapsackSolution = Set[Talk] => OnMetReq[TalksCombinations]

  /* todo scaladoc */
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]

  /* todo scaladoc */
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]

  /* todo scaladoc */
  type MorningSessionsComposition = Set[Talk] => ExceptionOr[MorningSessionsCompositionResult]

  /* todo scaladoc */
  type AfternoonSessionsComposition = Set[Talk] => ExceptionOr[AfternoonSessionsCompositionResult]

  /** Auxiliary type to return composed sessions
    * along with talks unused during composition
    */
  final case class SessionsCompositionResult[S <: Session](sessions: Set[S], unusedTalks: Set[Talk])

  /** Specialized exception that should be thrown by
    * every component of the package in case of failure
    */
  final case class Exception(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
}