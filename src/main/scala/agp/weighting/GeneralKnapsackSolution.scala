package agp.weighting

import scala.annotation.tailrec

final class GeneralKnapsackSolution[T <: Ordered[T], W <: Weighable[T]] {

  /* private aliases */
  private type Goal = Weighable[T]
  private type Weighables = List[W]
  private type Combination = List[W]
  private type Combinations = List[Combination]


  def apply(goal: Goal)(weighables: Weighables): Combinations = {
    require(goal.isPositive, "Goal must be positive.")
    require(weighables.nonEmpty, "At least one weighable is required.")
    require(weighables.forall(_.isPositive), "All weighables must be positive.")
    findCombinationsFor(goal, weighables.sorted(Ordering[Weighable[T]].reverse))
  }

  private def findCombinationsFor(goal: Goal, allWeighables: Weighables): Combinations = {

    @tailrec
    def process(unprocessed: Weighables, combinations: Combinations = Nil): Combinations =
      if (unprocessed.isEmpty) combinations else {
        val newCombinations = combinationsStartingWithHeadFor(unprocessed)
        process(unprocessed.tail, combinations ::: newCombinations)
      }

    def combinationsStartingWithHeadFor(weighables: Weighables): Combinations =
      if (weighables.isEmpty || weighables.head > goal) {
        List.empty
      } else if (weighables.head < goal) {
        headConsTailCombinationsFor(weighables)
      } else {
        List(List(weighables.head))
      }

    def headConsTailCombinationsFor(weighables: Weighables): Combinations = {
      val tailGoal = goal - weighables.head.weight
      val tailCombinations = findCombinationsFor(tailGoal, weighables.tail)
      for (combination <- tailCombinations) yield weighables.head :: combination
    }

    process(allWeighables)
  }

}
