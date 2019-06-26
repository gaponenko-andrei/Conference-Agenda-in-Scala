package agp.scheduling

import agp.vo._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.util.control.NonFatal


private[scheduling] class MorningSessionsSchedulingImpl
(val morningSessionScheduling: MorningSessionScheduling, val requiredSessionsNumber: Int)
  extends (Set[Talk] => MorningSessionsSchedulingResult) {

  /* shorter alias for result type */
  private type Result = MorningSessionsSchedulingResult


  def apply(talks: Set[Talk]): Result = {
    validateNumberOf(talks)

    val result = new ArrayBuffer[MorningSession]
    var unusedTalks = talks

    for (i <- 1 to requiredSessionsNumber) {
      val sessionSchedulingResult = scheduleSessionFrom(unusedTalks)
      result.append(sessionSchedulingResult.session)
      unusedTalks = sessionSchedulingResult.unusedTalks
    }

    new Result(result.toSet, unusedTalks)
  }

  private def validateNumberOf(talks: Set[Talk]): Unit = {
    require(talks.size >= requiredSessionsNumber,
      s"Talks.size must be >= $requiredSessionsNumber.")
  }

  private def scheduleSessionFrom(talks: Set[Talk]): MorningSessionSchedulingResult = {
    try {
      morningSessionScheduling(talks)
    } catch {
      case NonFatal(ex) => throw new SchedulingException(
        "Failed to schedule required number of morning " +
        "sessions with given MorningSessionScheduling.", ex
      )
    }
  }
}

object MorningSessionsSchedulingImpl {

  def apply(requiredSessionsNumber: Int): MorningSessionsSchedulingImpl = {
    val defaultSessionScheduling = MorningSessionSchedulingImpl.using(goal = 3 hours)
    new MorningSessionsSchedulingImpl(defaultSessionScheduling, requiredSessionsNumber)
  }
}
