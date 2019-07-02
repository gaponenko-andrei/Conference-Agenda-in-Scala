package agp.composition

import agp.vo.{AfternoonSession, Talk}

import scala.annotation.tailrec

private[composition] class AfternoonSessionsCompositionImpl(val requiredSessionsNumber: Int)
  extends (Set[Talk] => AfternoonSessionsCompositionResult) {

  /* shorter alias for result type */
  private type Result = AfternoonSessionsCompositionResult


  override def apply(talks: Set[Talk]): Result = {
    validateNumberOf(talks)
    new Result(sessions = composeSessionsFrom(talks), unusedTalks = Set.empty)
  }

  private def validateNumberOf(talks: Set[Talk]): Unit = {
    require(talks.size >= requiredSessionsNumber,
      s"Talks.size must be >= $requiredSessionsNumber.")
  }

  private def composeSessionsFrom(talks: Set[Talk]): Set[AfternoonSession] = {
    val idealSessionSize = talks.size / requiredSessionsNumber
    type T = Talk

    @tailrec
    def divideEqually(undivided: Seq[T], divided: List[Seq[T]], extra: Int): List[Seq[T]] =
      if (undivided.isEmpty) divided else {
        // take X elements that ideally should be in session,
        // but if there is extra, take +1 and decrement extra
        val (splitIndex, newExtra) = if (extra == 0) (idealSessionSize, 0)
                                     else (idealSessionSize + 1, extra - 1)

        val (newDivided, remaining) = undivided.splitAt(splitIndex)
        divideEqually(remaining, newDivided :: divided, newExtra)
      }

    val dividedTalks: Set[Seq[T]] = divideEqually(
      undivided = talks.toList,
      divided = Nil,
      extra = talks.size % requiredSessionsNumber).toSet

    dividedTalks map (list => AfternoonSession(list.toSet))
  }
}
