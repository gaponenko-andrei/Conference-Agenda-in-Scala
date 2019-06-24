package agp.scheduling

import agp.vo.{MorningSession, Talk, TalksCombination, TalksCombinations}
import agp.weighting.KnapsackSolutionForTalks

import scala.concurrent.duration.Duration

class MorningSessionSchedulingImpl(val knapsackSolution: TalksCombination => TalksCombinations)
  extends Scheduling[Talk, MorningSession[Talk]] {

  /* private aliases */
  private type Result = agp.scheduling.Result[Talk, MorningSession[Talk]]


  def apply(talks: Set[Talk]): Result = {
    require(talks.nonEmpty, "At least one talk is required.")

    def collectFirstSuitableCombination(): Option[TalksCombination] =
      knapsackSolution(talks).collectFirst { case i => i }

    def newResultFromCombination(sessionTalks: TalksCombination) =
      new Result(MorningSession(sessionTalks), talks except sessionTalks)

    def newException = new SchedulingException(
      "Failed to schedule MorningSession for given knapsack solution."
    )

    collectFirstSuitableCombination()
      .map(newResultFromCombination)
      .getOrElse(throw newException)
  }
}

object MorningSessionSchedulingImpl {

  def using(goal: Duration): MorningSessionSchedulingImpl = MorningSessionSchedulingImpl
    .using((talks: TalksCombination) => new KnapsackSolutionForTalks(talks)(goal))

  def using(knapsackSolution: TalksCombination => TalksCombinations) =
    new MorningSessionSchedulingImpl(knapsackSolution)
}
