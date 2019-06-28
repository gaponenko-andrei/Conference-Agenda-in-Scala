package agp.scheduler

import agp.vo.{MorningSession, Talk, TalksCombinations}
import agp.weighting.KnapsackSolutionForTalks

import scala.concurrent.duration.Duration

private[scheduler] class MorningSessionSchedulerImpl(
  val knapsackSolution: Set[Talk] => TalksCombinations
) extends (Set[Talk] => MorningSessionSchedulerResult) {

  /* shorter alias for result type */
  private type Result = MorningSessionSchedulerResult


  def apply(talks: Set[Talk]): Result = {
    require(talks.nonEmpty, "At least one talk is required.")

    def findFirstSuitableCombination() =
      knapsackSolution(talks).collectFirst { case i => i }

    def newResultFromGivenTalks(sessionTalks: Set[Talk]) =
      new Result(MorningSession(sessionTalks), talks except sessionTalks)

    findFirstSuitableCombination()
      .map(newResultFromGivenTalks)
      .getOrElse(throw newException)
  }

  private def newException = new SchedulerException(
    "Failed to schedule MorningSession with knapsack solution " +
    "for provided talks, because no possible combination " +
    "of talks conforms to required goal duration of event."
  )
}

object MorningSessionSchedulerImpl {

  def using(goal: Duration): MorningSessionSchedulerImpl = MorningSessionSchedulerImpl
    .using((talks: Set[Talk]) => new KnapsackSolutionForTalks(talks)(goal))

  def using(knapsackSolution: Set[Talk] => TalksCombinations): MorningSessionSchedulerImpl =
    new MorningSessionSchedulerImpl(knapsackSolution)
}
