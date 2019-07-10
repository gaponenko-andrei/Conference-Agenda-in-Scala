package agp.composition

import agp.Utils.RichSeq
import agp.vo.{AfternoonSession, Talk}

final class AfternoonSessionsCompositionImpl(val requiredSessionsNumber: Int)
  extends (Set[Talk] => AfternoonSessionsCompositionResult) {

  /* shorter alias for result type */
  private type Result = AfternoonSessionsCompositionResult


  override def apply(talks: Set[Talk]): Result = {
    validateNumberOf(talks)
    new Result(sessions = newSessionsFrom(talks), unusedTalks = Set())
  }

  private def validateNumberOf(talks: Set[Talk]): Unit = {
    require(talks.size >= requiredSessionsNumber,
      s"Talks.size must be >= $requiredSessionsNumber.")
  }

  private def newSessionsFrom(talks: Set[Talk]): Set[AfternoonSession] =
    talks.toList.equallyDividedInto(requiredSessionsNumber)
      .map(talksList => AfternoonSession(talksList.toSet))
      .toSet
}