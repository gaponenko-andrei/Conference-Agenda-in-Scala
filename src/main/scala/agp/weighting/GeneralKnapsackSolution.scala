package agp.weighting

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

final class GeneralKnapsackSolution[T <: Ordered[T]](weighables: WeighablesCombination[T]) {

  /* type aliases */
  private type Weighable = agp.weighting.Weighable[T]
  private type Combination = WeighablesCombination[T]
  private type Combinations = List[WeighablesCombination[T]]


  def apply(goal: Weighable): Combinations = {
    require(goal.isPositive)
    require(weighables.nonEmpty)
    require(weighables.forall(_.isPositive))
    findCombinationsFor(goal, weighables.sorted(Ordering[Weighable].reverse))
  }

  private def findCombinationsFor(goal: Weighable, allWeighables: Combination): Combinations = {

    @tailrec
    def iteration(unprocessedWeighables: Combination, result: ListBuffer[Combination]): Combinations = {
      if (unprocessedWeighables.isEmpty) {
        result.toList
      } else {
        result ++= combinationsStartingWithHeadFor(unprocessedWeighables)
        iteration(unprocessedWeighables.tail, result)
      }
    }

    def combinationsStartingWithHeadFor(weighables: Combination): Combinations = {
      if (weighables.isEmpty || weighables.head > goal) {
        List.empty
      } else if (weighables.head < goal) {
        /* possible combinations of a head and tail;
        in each combination weight sum of 'head :: tail'
        elements conforms to provided goal weight */
        headAndTailUnionCombinationsFor(weighables)
      } else {
        // a list of one combination with one element
        List(List(weighables.head))
      }
    }

    def headAndTailUnionCombinationsFor(weighables: Combination): Combinations = {
      val tailGoal = goal - weighables.head.weight
      findCombinationsFor(tailGoal, weighables.tail).map(List(weighables.head) :::)
    }

    iteration(allWeighables, new ListBuffer)
  }

}
