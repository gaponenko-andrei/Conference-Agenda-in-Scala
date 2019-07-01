package agp

import agp.vo.event.Talk
import agp.vo.session.{AfternoonSession, MorningSession}

package object composition {

  type MorningSessionComposition = Set[Talk] => MorningSessionCompositionResult
  type MorningSessionsComposition = Set[Talk] => MorningSessionsCompositionResult
  type AfternoonSessionComposition = Set[Talk] => AfternoonSessionCompositionResult


  final case class MorningSessionCompositionResult(session: MorningSession, unusedTalks: Set[Talk])

  final case class MorningSessionsCompositionResult(sessions: Set[MorningSession], unusedTalks: Set[Talk])

  final case class AfternoonSessionCompositionResult(session: AfternoonSession, unusedTalks: Set[Talk])

  final case class CompositionException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)
}