package agp.composition

import agp.composition
import agp.vo.{MorningSession, Talk, TalksCombinations}

final class MorningSessionCompositionImpl(
  val knapsackSolution: Set[Talk] => TalksCombinations
) extends (Set[Talk] => MorningSessionCompositionResult) {

  /* shorter alias for result type */
  private type Result = MorningSessionCompositionResult


  override def apply(talks: Set[Talk]): Result = {
    require(talks.nonEmpty, "At least one talk is required.")

    findSuitableCombinationsAmong(talks)
      .collectFirst {
        case suitableTalks => new Result(
          session = MorningSession(suitableTalks),
          unusedTalks = talks -- suitableTalks)

      } getOrElse (throw newException)
  }

  /* alias for better comprehension of what knapsack solution is supposed to do */
  def findSuitableCombinationsAmong(talks: Set[Talk]) = knapsackSolution(talks)

  private def newException = composition.Exception(
    "Failed to compose MorningSession with knapsack solution " +
    "for provided talks, because no possible combination " +
    "of talks conforms to required goal duration of event.")
}