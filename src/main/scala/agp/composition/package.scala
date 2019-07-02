package agp

import agp.vo.{AfternoonSession, MorningSession, Talk}

package object composition {

  /* aliases for composition results */
  type MorningSessionCompositionResult = SessionCompositionResult[MorningSession]
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]
  type AfternoonSessionCompositionResult = SessionCompositionResult[AfternoonSession]
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]

  /* aliases for compositions */
  type MorningSessionComposition = Set[Talk] => MorningSessionCompositionResult
  type MorningSessionsComposition = Set[Talk] => MorningSessionsCompositionResult
  type AfternoonSessionComposition = Set[Talk] => AfternoonSessionCompositionResult
  type AfternoonSessionsComposition = Set[Talk] => AfternoonSessionsCompositionResult

  /* specialized exception */
  final case class CompositionException(
    msg: String, cause: Throwable = null
  ) extends RuntimeException(msg, cause)
}