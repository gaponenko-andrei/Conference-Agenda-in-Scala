package agp.weighting

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

final class SimplifiedKnapsackSolution[T <: Ordered[T]] {

  /* type aliases */
  private type Weighable = agp.weighting.Weighable[T]
  private type Combination = WeighablesCombination[T]
  private type Combinations = List[WeighablesCombination[T]]


  def apply(weighables: Combination, goal: Weighable): Combinations = {
    require(goal.isPositive)
    require(weighables.nonEmpty)
    require(weighables.forall(_.isPositive))
    findCombinationsFor(goal, weighables.sorted(Ordering[Weighable].reverse))
  }

  private def findCombinationsFor(goal: Weighable, allWeighables: Combination): Combinations = {

    @tailrec
    def iteration(unprocessedWeighables: Combination, result: ListBuffer[Combination]): Combinations = {
      if (unprocessedWeighables.isEmpty) {
        List.empty
      } else {
        result ++= combinationsStartingWithHeadFor(unprocessedWeighables)
        iteration(unprocessedWeighables.tail, result)
      }
    }

    def combinationsStartingWithHeadFor(weighables: Combination): Combinations = {
      if (weighables.isEmpty || weighables.head > goal) {
        List.empty
      } else if (weighables.head < goal) {
        headAndTailUnionCombinationsFor(weighables)
      } else {
        List(List(weighables.head))
      }
    }

    def headAndTailUnionCombinationsFor(weighables: Combination): Combinations = {
      val tailGoal = goal - weighables.head.weight
      findCombinationsFor(tailGoal, weighables.tail)
        .map(List(weighables.head) :::)
    }

    iteration(allWeighables, new ListBuffer)
  }

}
