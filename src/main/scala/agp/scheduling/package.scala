package agp

import agp.vo.{MorningSession, Talk}

package object scheduling {

  final class SchedulingException(val msg: String) extends RuntimeException(msg)

  /* Morning Session Scheduling */

  abstract class MorningSessionScheduling extends (Set[Talk] => MorningSessionSchedulingResult)

  final class MorningSessionSchedulingResult(val session: MorningSession, val unusedTalks: Set[Talk])

  /* Morning Sessions Scheduling */

  abstract class MorningSessionsScheduling extends (Set[Talk] => MorningSessionsSchedulingResult)

  final class MorningSessionsSchedulingResult(val sessions: Set[MorningSession], val unusedTalks: Set[Talk])

}
