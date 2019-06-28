package agp

import agp.vo.{MorningSession, Talk}

package object scheduler {

  type MorningSessionScheduler = Set[Talk] => MorningSessionSchedulerResult
  type MorningSessionsScheduler = Set[Talk] => MorningSessionsSchedulerResult

  final class MorningSessionSchedulerResult(val session: MorningSession, val unusedTalks: Set[Talk])
  final class MorningSessionsSchedulerResult(val sessions: Set[MorningSession], val unusedTalks: Set[Talk])
  final class SchedulerException(val msg: String, val cause: Throwable = null) extends RuntimeException(msg, cause)
}