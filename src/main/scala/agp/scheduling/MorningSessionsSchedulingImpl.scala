package agp.scheduling

import agp.vo

import scala.concurrent.duration._


private[scheduling] class MorningSessionsSchedulingImpl
(val morningSessionScheduling: MorningSessionScheduling)
(val requiredSessionsNumber: Int)
  extends MorningSessionsScheduling {


  def apply(talks: Set[vo.Talk]): MorningSessionsSchedulingResult = {
    validateNumberOf(talks)

    val result = morningSessionScheduling(talks)
    morningSessionScheduling(result.unusedTalks)

    null
  }

  private def validateNumberOf(talks: Set[vo.Talk]): Unit =
    require(talks.size >= requiredSessionsNumber, s"Talks.size must be >= $requiredSessionsNumber.")
}

object MorningSessionsSchedulingImpl {

  def apply(requiredSessionsNumber: Int): MorningSessionsSchedulingImpl = {
    val defaultSessionScheduling = MorningSessionSchedulingImpl.using(goal = 3 hours)
    MorningSessionsSchedulingImpl.using(defaultSessionScheduling)(requiredSessionsNumber)
  }

  def using(morningSessionScheduling: MorningSessionScheduling) =
    new MorningSessionsSchedulingImpl(morningSessionScheduling)(_)

}
