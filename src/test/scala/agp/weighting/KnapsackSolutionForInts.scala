package agp.weighting


class KnapsackSolutionForInts(ints: List[Int]) {

  /* public aliases */
  type Combination = List[Int]
  type Combinations = List[Combination]

  /* private aliases */
  private type WCombination = List[WeighableInt]
  private type WCombinations = List[WCombination]


  def apply(goal: Int): Combinations = {
    val solution = new GeneralKnapsackSolution[OrderedInt, WeighableInt]
    val adaptedGoal = adaptForGeneralSolution(goal)
    val adaptedWeighables = adaptForGeneralSolution(ints)
    simplifyCombinations(solution(adaptedGoal)(adaptedWeighables))
  }

  /* Methods to adapt (wrap) arguments for contract of general solution */

  private def adaptForGeneralSolution(ints: Combination): WCombination = ints.map(WeighableInt)

  private def adaptForGeneralSolution(i: Int): WeighableInt = WeighableInt(i)


  /* Methods to simplify (unwrap) result of general solution into client-known types */

  private def simplifyCombinations(combinations: WCombinations)
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
