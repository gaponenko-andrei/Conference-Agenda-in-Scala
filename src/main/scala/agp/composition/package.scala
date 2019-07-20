package agp

import agp.Utils.OnMetReq
import agp.vo.{AfternoonSession, MorningSession, Session, Talk, TalksCombinations}
import org.scalactic.Or

package object composition {

  type KnapsackSolution = Set[Talk] => OnMetReq[TalksCombinations]
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]
  type MorningSessionsComposition = Set[Talk] => ExceptionOr[MorningSessionsCompositionResult]
  type AfternoonSessionsComposition = Set[Talk] => ExceptionOr[AfternoonSessionsCompositionResult]

  /** Alias for package-internal use only, bound to package-specific exception */
  private[composition] type ExceptionOr[T] = T Or composition.Exception

  /** Auxiliary type to return composed sessions along with talks unused during composition */
  final case class SessionsCompositionResult[S <: Session](sessions: Set[S], unusedTalks: Set[Talk])

  /** Specialized exception that should be thrown by every component of the package in case of failure */
  final case class Exception (message: String, cause: Throwable = null) extends RuntimeException(message, cause)
}