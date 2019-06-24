package agp.scheduling

import agp.vo.{MorningSession, Talk, TalksCombination, TalksCombinations}
import agp.weighting.KnapsackSolutionForTalks

import scala.concurrent.duration.Duration

class MorningSessionSchedulingImpl(val knapsackSolution: TalksCombination => TalksCombinations)
  extends Scheduling[Talk, MorningSession[Talk]] {

  /* private aliases */
  private type Result = agp.scheduling.Result[Talk, MorningSession[Talk]]


  def apply(talks: TalksCombination): Result = {
    require(talks.nonEmpty, "At least one talk is required.")

    def findFirstSuitableCombination() =
      knapsackSolution(talks).collectFirst { case i => i }

    def newResultFromGivenTalks(sessionTalks: TalksCombination) =
      new Result(MorningSession(sessionTalks), talks except sessionTalks)

    findFirstSuitableCombination()
      .map(newResultFromGivenTalks)
      .getOrElse(throw newException)
  }

  private def newException = new SchedulingException(
    "Failed to schedule MorningSession for given knapsack solution."
  )
}

object MorningSessionSchedulingImpl {

  def using(goal: Duration): MorningSessionSchedulingImpl = MorningSessionSchedulingImpl
    .using((talks: TalksCombination) => new KnapsackSolutionForTalks(talks)(goal))

  def using(knapsackSolution: TalksCombination => TalksCombinations) =
    new MorningSessionSchedulingImpl(knapsackSolution)
}
