package agp.weighting

import agp.vo.Talk

import scala.concurrent.duration.Duration

class KnapsackSolutionForTalks(talks: Set[Talk]) {

  /* public aliases */
  type Combination = Set[Talk]
  type Combinations = Set[Combination]

  /* private aliases for package-private types used in general knapsack solution */
  private type WCombination = List[WeighableTalk]
  private type WCombinations = List[WCombination]


  def apply(goal: Duration): Combinations = simplifyCombinations(
    new GeneralKnapsackSolution[WeighableTalk, Duration]
    (adaptForGeneralSolution(talks)) // weighables
    (adaptForGeneralSolution(goal))  // desired goal
  )

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private def adaptForGeneralSolution(talks: Combination)
  : WCombination = talks.map(new WeighableTalk(_)).toList

  private def adaptForGeneralSolution(duration: Duration)
  : WeighableDuration = new WeighableDuration(duration)


  /* Methods to simplify (unwrap) result of general solution into client-known types */

  private def simplifyCombinations(combinations: WCombinations)
  : Combinations = combinations.map(simplifyCombination).toSet

  private def simplifyCombination(combination: WCombination)
  : Combination = combination.map(_.value).toSet


  /* Auxiliary classes */

  private final class WeighableTalk(val value: Talk) extends Weighable[Duration] {

    def weight: Duration = value.duration

    def isPositive: Boolean = this.weight.toMillis > 0

    def -(otherWeight: Duration): Weighable[Duration] = new WeighableDuration(this.weight - otherWeight)
  }

  private final class WeighableDuration(val weight: Duration) extends Weighable[Duration] {

    def isPositive: Boolean = weight.toMillis > 0

    def -(otherWeight: Duration): Weighable[Duration] = new WeighableDuration(weight - otherWeight)
  }

}
