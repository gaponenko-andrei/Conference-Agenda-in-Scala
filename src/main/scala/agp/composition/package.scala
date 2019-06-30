package agp

import agp.vo.session.{AfternoonSession, MorningSession}
import agp.vo.{ConferenceTrack, Talk}

package object composition {

  type MorningSessionComposition = Set[Talk] => MorningSessionCompositionResult
  type MorningSessionsComposition = Set[Talk] => MorningSessionsCompositionResult

  type AfternoonSessionScheduler = Set[Talk] => AfternoonSession

  type ConferenceTracksScheduler = Set[Talk] => Set[ConferenceTrack]

  final case class MorningSessionCompositionResult(session: MorningSession, unusedTalks: Set[Talk])

  final case class MorningSessionsCompositionResult(sessions: Set[MorningSession], unusedTalks: Set[Talk])

  final case class CompositionException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)
}