package agp.weighting

import agp.Utils.OnMetReq
import agp.vo.{Talk, TalksCombinations}
import scala.concurrent.duration.Duration

/** Basic implementation of knapsack solution for talks that returns
  * all combinations of given talks with durations summing to goal.
  */
object KnapsackSolutionForTalks {

  private type Talks = Set[Talk]
  private type WCombination = List[WeighableTalk]
  private type WCombinations = List[WCombination]


  /** Returns all combinations of given talks with durations summing to goal */
  def apply(goal: Duration)(talks: Talks): OnMetReq[TalksCombinations] =
    apply(talks, goal) map simplifyResult // if result is 'Good' then simplify it,
                                          // otherwise return exception as it is

  /** Applies [[agp.weighting.GeneralKnapsackSolution2]] to given goal & talks */
  private def apply(talks: Talks, goal: Duration): OnMetReq[WCombinations] = {
    val solution = new GeneralKnapsackSolution2[Duration, WeighableTalk]
    val (adaptedGoal, weighables) = adaptForGeneralSolution(goal, talks)
    solution(adaptedGoal)(weighables)
  }

  /** Wraps given goal & talks into weighables for general knapsack solution */
  private def adaptForGeneralSolution(goal: Duration, talks: Talks) =
    (WeighableDuration(goal), talks.map(WeighableTalk).toList)

  /** Unwraps (simplifies) given combinations of weighables to TalksCombinations */
  private def simplifyResult(x: WCombinations): TalksCombinations = x.map(simplify).toSet

  /** Unwraps (simplifies) given combination of weighables to Set[Talk] */
  private def simplify(x: WCombination): Talks = x.map(_.value).toSet

  /* Auxiliary classes */

  private final case class WeighableTalk(value: Talk) extends Weighable[Duration] {
    def weight: Duration = value.duration
    def isPositive: Boolean = this.weight.toMillis > 0
    def -(otherWeight: Duration): Weighable[Duration] = WeighableDuration(this.weight - otherWeight)
  }

  private final case class WeighableDuration(weight: Duration) extends Weighable[Duration] {
    def isPositive: Boolean = weight.toMillis > 0
    def -(otherWeight: Duration): Weighable[Duration] = WeighableDuration(weight - otherWeight)
  }
}