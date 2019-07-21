package agp.weighting

import agp.Utils.OnMetReq
import org.scalactic.Good

import scala.annotation.tailrec

/** A basic implementation of algorithm that finds all combinations
  * of weighables with their combined weight equal to provided goal.
  */
final class GeneralKnapsackSolution[T <: Ordered[T], W <: Weighable[T]] {

  private type Goal = Weighable[T]
  private type Weighables = List[W]
  private type Combination = List[W]
  private type Combinations = List[Combination]

  def apply(goal: Goal)(weighables: Weighables): OnMetReq[Combinations] = for {

    _ <- if (goal.isPositive) Good(goal)
         else new IllegalArgumentException("Positive goal is required.")

    _ <- if (weighables.nonEmpty) Good(weighables)
         else new IllegalArgumentException("At least one weighable is required.")

    _ <- if (weighables forall (_.isPositive)) Good(weighables)
         else new IllegalArgumentException("All weighables must be positive.")

  } yield findCombinationsFor(goal, weighables.sorted(Ordering[Weighable[T]].reverse))

  private def findCombinationsFor(goal: Goal, allWeighables: Weighables): Combinations = {

    @tailrec
    def process(unprocessed: Weighables, res: Combinations = Nil): Combinations =
      if (unprocessed.isEmpty) res
      else process(unprocessed.tail, res ::: combinationsStartingWithHeadOf(unprocessed))

    /** Returns all suitable combinations that start with head of the
      * weighables list and may or may not contain some tail elements
      */
    def combinationsStartingWithHeadOf(weighables: Weighables): Combinations =
      if (weighables.isEmpty || weighables.head > goal) List.empty
      else if (weighables.head < goal) headConsTailCombinationsFor(weighables)
      else List(List(weighables.head))

    /** Returns all suitable combinations that start with head of
      * the weighables list and have some tail elements in them
      */
    def headConsTailCombinationsFor(weighables: Weighables): Combinations = {
      val tailGoal = goal - weighables.head.weight
      val tailCombinations = findCombinationsFor(tailGoal, weighables.tail)
      for (combination <- tailCombinations) yield weighables.head :: combination
    }

    process(allWeighables)
  }
}