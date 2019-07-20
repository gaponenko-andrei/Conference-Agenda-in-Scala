package agp.composition

import agp.Utils._
import agp.composition
import agp.vo.{AfternoonSession, Talk}
import org.scalactic.{Bad, Good}

/**
  * Function to compose required number of afternoon sessions.
  *
  * Applying this to talks returns an object with composed
  * sessions & unused talks, if composition was successful,
  * i.e. [[agp.composition.AfternoonSessionsCompositionResult]].
  *
  * If requirements for successful composition were not met, then
  * `IllegalArgumentException` is returned with detailed explanation.
  *
  * Take note that this particular implementation never leaves any
  * talks unused, so corresponding result field will always be an
  * empty set of talks. This is made for generalization purpose.
  */
final class AfternoonSessionsCompositionImpl(val requiredSessionsNumber: Int) extends AfternoonSessionsComposition {

  private type Talks = Set[Talk]
  private type Result = AfternoonSessionsCompositionResult

  /** Returns required number of afternoon sessions if composition was successful.
    * Otherwise returns `IllegalArgumentException` with detailed error message
    */
  override def apply(talks: Talks): ExceptionOr[Result] =
    validated(talks) flatMap compose

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or `IllegalArgumentException` with error message
    */
  private def validated(talks: Talks): ExceptionOr[Talks] =
    if (talks.size >= requiredSessionsNumber) Good(talks)
    else Bad(composition.Exception(
      s"Talks.size is ${talks.size}, " +
      s"but must be >= $requiredSessionsNumber."))

  /** Actual composition; divides given talks into roughly equal
    * sets and creates afternoon session based on each of them,
    * returning object with sessions & empty set of unused talks
    */
  private def compose(talks: Talks): ExceptionOr[Result] = Good {
    new Result(sessions = newSessionsFrom(talks), unusedTalks = Set.empty)
  }

  /** Returns set of afternoon sessions from roughly equally
    * divided set of all given talks, leaving no talks unused
    */
  private def newSessionsFrom(talks: Talks): Set[AfternoonSession] =
    talks.toSeq.equallyDividedInto(requiredSessionsNumber)
      .map((i: Seq[Talk]) => AfternoonSession(i.toSet))
      .toSet
}