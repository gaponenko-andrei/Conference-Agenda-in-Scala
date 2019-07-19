package agp

import agp.Utils.OnMetReq
import agp.vo.{AfternoonSession, MorningSession, Talk, TalksCombinations}
import org.scalactic.Or

package object composition {

  /* specialized exception that should be thrown by every component of the package in case of failure */
  final case class Exception (message: String, cause: Throwable = null) extends RuntimeException(message, cause)

  private[composition] type ExceptionOr[T] = T Or composition.Exception

  type KnapsackSolution = Set[Talk] => OnMetReq[TalksCombinations]
  type MorningSessionComposition = Set[Talk] => MorningSessionCompositionResult
  type MorningSessionsComposition = Set[Talk] => MorningSessionsCompositionResult
  type AfternoonSessionComposition = Set[Talk] => AfternoonSessionCompositionResult
  type AfternoonSessionsComposition = Set[Talk] => AfternoonSessionsCompositionResult

  type MorningSessionsComposition2 = Set[Talk] => ExceptionOr[MorningSessionsCompositionResult]
  type AfternoonSessionsComposition2 = Set[Talk] => ExceptionOr[AfternoonSessionsCompositionResult]

  type MorningSessionCompositionResult = SessionCompositionResult[MorningSession]
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]
  type AfternoonSessionCompositionResult = SessionCompositionResult[AfternoonSession]
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]
}