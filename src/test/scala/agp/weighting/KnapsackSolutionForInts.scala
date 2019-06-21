package agp.weighting


class KnapsackSolutionForInts(ints: List[Int]) {

  /* public aliases */
  type Combination = List[Int]
  type Combinations = List[Combination]

  /* private aliases for package-private types used in general knapsack solution */
  private type WCombination = List[WeighableInt]
  private type WCombinations = List[WCombination]


  def apply(goal: Int): Combinations = simplifyCombinations(
    new GeneralKnapsackSolution[WeighableInt, OrderedInt]
    (adaptForGeneralSolution(ints)) // weighables
    (adaptForGeneralSolution(goal)) // desired goal
  )

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private def adaptForGeneralSolution(ints: Combination)
  : WCombination = ints.map(new WeighableInt(_))

  private def adaptForGeneralSolution(i: Int)
  : WeighableInt = new WeighableInt(i)


  /* Methods to simplify (unwrap) result of general solution into client-known types */

  private def simplifyCombinations(combinations: WCombinations)
  : Combinations = combinations.map(simplifyCombination)

  private def simplifyCombination(combination: WCombination)
  : Combination = combination.map(_.weight.value)


  /* Auxiliary classes */

  private final class WeighableInt(i: Int) extends agp.weighting.Weighable[OrderedInt] {
    val ordered = new OrderedInt(i)

    def weight: OrderedInt = ordered

    def isPositive: Boolean = ordered.value > 0

    def -(other: OrderedInt): WeighableInt = new WeighableInt(ordered.value - other.value)
  }

  private final class OrderedInt(val value: Int) extends Ordered[OrderedInt] {
    def compare(other: OrderedInt): Int = this.value - other.value
  }

}
