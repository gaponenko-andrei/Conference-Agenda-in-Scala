package agp.composition

import agp.Utils._
import agp.composition.AfternoonSessionsCompositionImpl2.Result
import agp.vo.{AfternoonSession, Talk}
import org.scalactic.Good

// todo
final class AfternoonSessionsCompositionImpl2(val requiredSessionsNumber: Int)
  extends AfternoonSessionsComposition2 {

  /** Returns required number of afternoon sessions if composition was successful.
    * Otherwise returns `IllegalArgumentException` with detailed error message
    */
  override def apply(talks: Set[Talk]): OnMetReq[Result] =
    validated(talks) flatMap compose

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or `IllegalArgumentException` with error message
    */
  private def validated(talks: Set[Talk]): OnMetReq[Set[Talk]] =
    talks given talks.size >= requiredSessionsNumber because
    s"Talks.size is ${talks.size}, but must be >= $requiredSessionsNumber."

  /** Actual composition; divides given talks into roughly equal
    * sets and creates afternoon session based on each of them,
    * returning object with sessions & empty set of unused talks
    */
  private def compose(talks: Set[Talk]): OnMetReq[Result] = Good {
    new Result(sessions = newSessionsFrom(talks), unusedTalks = Set.empty)
  }

  /** Returns set of afternoon sessions from roughly equally
    * divided set of all given talks, leaving no talks unused
    */
  private def newSessionsFrom(talks: Set[Talk]): Set[AfternoonSession] =
    talks.toSeq.equallyDividedInto(requiredSessionsNumber)
      .map((i: Seq[Talk]) => AfternoonSession(i.toSet))
      .toSet
}

object AfternoonSessionsCompositionImpl2 {
  type Result = AfternoonSessionsCompositionResult
}