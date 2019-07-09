package agp.weighting

import agp.weighting.KnapsackSolutionForInts.{Combination, Combinations}


final class KnapsackSolutionForInts {

  /* private aliases */
  private type WCombination = List[WeighableInt]
  private type WCombinations = List[WCombination]


  def apply(goal: Int)(ints: List[Int]): Combinations = simplifySolution {
    val solution = new GeneralKnapsackSolution[OrderedInt, WeighableInt]
    solution(goal = WeighableInt(goal))(weighables = ints map WeighableInt)
  }

  /* Methods to simplify (unwrap) result of general solution into client-known types */

  private def simplifySolution(combinations: WCombinations)
  : Combinations = combinations map simplifyCombination

  private def simplifyCombination(combination: WCombination)
  : Combination = combination map (_.weight.value)

  /* Auxiliary classes */

  private final case class WeighableInt(i: Int) extends agp.weighting.Weighable[OrderedInt] {
    private val ordered = OrderedInt(i)
    def weight: OrderedInt = ordered
    def isPositive: Boolean = ordered.value > 0
    def -(other: OrderedInt): WeighableInt = WeighableInt(ordered.value - other.value)
  }

  private final case class OrderedInt(value: Int) extends Ordered[OrderedInt] {
    def compare(other: OrderedInt): Int = this.value - other.value
  }
}

object KnapsackSolutionForInts {

  /* public aliases */
  type Combination = List[Int]
  type Combinations = List[Combination]

  def apply(goal: Int, ints: List[Int]): Combinations = new KnapsackSolutionForInts()(goal)(ints)
}