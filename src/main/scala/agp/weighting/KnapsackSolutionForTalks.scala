package agp.weighting

import agp.vo.{Talk, TalksCombinations}

import scala.concurrent.duration.Duration

object KnapsackSolutionForTalks {

  /* private aliases for package-private types used in general knapsack solution */
  private type WCombination = List[WeighableTalk]
  private type WCombinations = List[WCombination]


  def apply(goal: Duration)(talks: Set[Talk]): TalksCombinations = simplifySolution {
    val solution = new GeneralKnapsackSolution[Duration, WeighableTalk]
    val adaptedGoal = adaptForGeneralSolution(goal)
    val weighables = adaptForGeneralSolution(talks)
    solution(adaptedGoal)(weighables)
  }

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private def adaptForGeneralSolution(talks: Set[Talk])
  : WCombination = talks.map(WeighableTalk).toList

  private def adaptForGeneralSolution(duration: Duration)
  : WeighableDuration = WeighableDuration(duration)


  /* Methods to simplify (unwrap) result of general solution into client-known types */

  private def simplifySolution(combinations: WCombinations)
  : TalksCombinations = combinations.map(simplifyCombination).toSet

  private def simplifyCombination(combination: WCombination)
  : Set[Talk] = combination.map(_.value).toSet


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