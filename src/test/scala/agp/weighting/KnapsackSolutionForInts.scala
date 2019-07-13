package agp.weighting

import agp.Utils.OnMetReq
import org.scalactic.Or

/** Basic implementation of knapsack solution for ints that
  * returns all combinations of given ints summing to goal.
  */
object KnapsackSolutionForInts {

  type Combination = List[Int]
  type Combinations = List[Combination]

  private type WCombination = List[WeighableInt]
  private type WCombinations = List[WCombination]


  /** Returns all combinations of given ints summing to goal */
  def apply(goal: Int)(ints: List[Int]): OnMetReq[Combinations] =
    apply(ints, goal) map simplifyResult // if result is 'Good' then simplify it,
                                         // otherwise return exception as it is

  /** Applies [[agp.weighting.GeneralKnapsackSolution2]] to given goal & ints */
  private def apply(ints: List[Int], goal: Int): OnMetReq[WCombinations] = {
    val solution = new GeneralKnapsackSolution2[OrderedInt, WeighableInt]
    val (adaptedGoal, weighables) = adaptForGeneralSolution(goal, ints)
    solution(adaptedGoal)(weighables)
  }

  /** Wraps given goal & ints into weighables for general knapsack solution */
  private def adaptForGeneralSolution(goal: Int, ints: List[Int]) =
    (WeighableInt(goal), ints map WeighableInt)

  /** Unwraps (simplifies) given combinations of weighables to List[Combination] */
  private def simplifyResult(x: WCombinations): Combinations = x map simplify

  /** Unwraps (simplifies) given combination of weighables to List[Int] */
  private def simplify(x: WCombination): Combination = x map (_.weight.value)

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