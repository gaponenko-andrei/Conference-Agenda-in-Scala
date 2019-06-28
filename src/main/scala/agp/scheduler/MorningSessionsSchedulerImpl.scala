package agp.scheduler

import agp.vo._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.util.control.NonFatal


private[scheduler] class MorningSessionsSchedulerImpl
(val morningSessionScheduling: MorningSessionScheduler, val requiredSessionsNumber: Int)
  extends (Set[Talk] => MorningSessionsSchedulerResult) {

  /* shorter alias for result type */
  private type Result = MorningSessionsSchedulerResult


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

  private def scheduleSessionFrom(talks: Set[Talk]): MorningSessionSchedulerResult = {
    try {
      morningSessionScheduling(talks)
    } catch {
      case NonFatal(ex) => throw new SchedulerException(
        "Failed to schedule required number of morning " +
        "sessions with given MorningSessionScheduling.", ex
      )
    }
  }
}

object MorningSessionsSchedulerImpl {

  def apply(requiredSessionsNumber: Int): MorningSessionsSchedulerImpl = {
    val defaultSessionScheduling = MorningSessionSchedulerImpl.using(goal = 3 hours)
    new MorningSessionsSchedulerImpl(defaultSessionScheduling, requiredSessionsNumber)
  }
}
