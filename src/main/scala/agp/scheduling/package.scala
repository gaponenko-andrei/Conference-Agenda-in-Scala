package agp

import agp.vo.{MorningSession, Talk}

package object scheduling {

  type MorningSessionScheduling = Set[Talk] => MorningSessionSchedulingResult
  type MorningSessionsScheduling = Set[Talk] => MorningSessionsSchedulingResult

  final class MorningSessionSchedulingResult(val session: MorningSession, val unusedTalks: Set[Talk])

  final class MorningSessionsSchedulingResult(val sessions: Set[MorningSession], val unusedTalks: Set[Talk])

  final class SchedulingException(val msg: String, val cause: Throwable = null) extends RuntimeException(msg, cause)
}