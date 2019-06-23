package agp.weighting

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

final class GeneralKnapsackSolution[W <: Weighable[T], T <: Ordered[T]](weighables: List[W]) {

  /* type aliases */
  private type Combination = List[W]
  private type Combinations = List[Combination]


  def apply(goal: Weighable[T]): Combinations = {
    require(goal.isPositive)
    require(weighables.nonEmpty)
    require(weighables.forall(_.isPositive))
    findCombinationsFor(goal, weighables.sorted(Ordering[Weighable[T]].reverse))
  }

  private def findCombinationsFor(goal: Weighable[T], allWeighables: Combination): Combinations = {

    @tailrec
    def process(unprocessedWeighables: Combination, result: ListBuffer[Combination]): Combinations = {
      if (unprocessedWeighables.isEmpty) {
        result.toList
      } else {
        result ++= combinationsStartingWithHeadFor(unprocessedWeighables)
        process(unprocessedWeighables.tail, result)
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
      findCombinationsFor(tailGoal, weighables.tail).map(List(weighables.head) :::)
    }

    process(allWeighables, new ListBuffer)
  }

}
