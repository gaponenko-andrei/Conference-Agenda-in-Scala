package agp.weighting


class KnapsackSolutionForInts(ints: List[Int]) {

  /* public aliases */
  type Combination = List[Int]
  type Combinations = List[Combination]

  /* private aliases for package-private types used in general knapsack solution */
  private type WeighablesCombination = agp.weighting.WeighablesCombination[OrderedInt]
  private type WeighablesCombinations = List[WeighablesCombination]


  def apply(goal: Int): Combinations = simplify(
    new GeneralKnapsackSolution[OrderedInt]
    (adaptIntsForGeneralSolution(ints)) // weighables
    (adaptGoalForGeneralSolution(goal)) // desired goal
  )

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private def adaptIntsForGeneralSolution(ints: Combination): WeighablesCombination = ints.map(new WeighableInt(_))

  private def adaptGoalForGeneralSolution(i: Int): WeighableInt = new WeighableInt(i)


  /* Methods to simplify (unwrap) general generalSolution result into client-known types */

  private def simplify(combinations: WeighablesCombinations): Combinations = combinations.map(simplify)

  private def simplify(combination: WeighablesCombination): Combination = combination.map(_.weight.value)


  /* Auxiliary classes */

  private class OrderedInt(val value: Int) extends Ordered[OrderedInt] {
    def compare(other: OrderedInt): Int = this.value - other.value
  }

  private class WeighableInt(i: Int) extends agp.weighting.Weighable[OrderedInt] {
    val ordered = new OrderedInt(i)

    def weight: OrderedInt = ordered

    def isPositive: Boolean = ordered.value > 0

    def -(other: OrderedInt): WeighableInt = new WeighableInt(ordered.value - other.value)
  }

}
