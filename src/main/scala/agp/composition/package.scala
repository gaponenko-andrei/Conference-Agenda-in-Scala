package agp

import agp.Utils.OnMetReq
import agp.vo.{AfternoonSession, MorningSession, Talk, TalksCombinations}
import org.scalactic.Or

package object composition {

  type KnapsackSolution = Set[Talk] => OnMetReq[TalksCombinations]

  /* aliases for compositions */
  type MorningSessionComposition = Set[Talk] => MorningSessionCompositionResult
  type MorningSessionsComposition = Set[Talk] => MorningSessionsCompositionResult
  type AfternoonSessionComposition = Set[Talk] => AfternoonSessionCompositionResult
  type AfternoonSessionsComposition = Set[Talk] => AfternoonSessionsCompositionResult

  type MorningSessionsComposition2 = Set[Talk] => MorningSessionsCompositionResult Or composition.Exception
  type AfternoonSessionsComposition2 = Set[Talk] => OnMetReq[AfternoonSessionsCompositionResult]

  /* aliases for composition results */
  type MorningSessionCompositionResult = SessionCompositionResult[MorningSession]
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]
  type AfternoonSessionCompositionResult = SessionCompositionResult[AfternoonSession]
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]

  /* specialized exception */
  final case class Exception (message: String, cause: Throwable = null)
    extends IllegalArgumentException(message, cause)
}