package agp.composition

import agp.Utils._
import agp.composition
import agp.composition.MorningSessionsCompositionImpl2.Result
import agp.vo.{MorningSession, Talk, TalksCombinations}
import org.scalactic.{Bad, Good}

import scala.annotation.tailrec
import scala.collection.immutable.Queue

/**
  * Function to compose required number of morning sessions
  * using given instance of [[agp.composition.KnapsackSolution]].
  *
  * Applying this to talks returns an object with composed
  * sessions & unused talks, if composition was successful,
  * i.e. [[agp.composition.MorningSessionsCompositionResult]].
  *
  * If requirements for successful composition were not met, then
  * `IllegalArgumentException` is returned with detailed explanation.
  */
final class MorningSessionsCompositionImpl2(
  val requiredSessionsNumber: Int,
  val knapsackSolution: (Set[Talk] => OnMetReq[TalksCombinations])
) extends MorningSessionsComposition2 {

  /** Returns required number of morning sessions if composition was successful.
    * Otherwise returns `IllegalArgumentException` with detailed error message
    */
  override def apply(talks: Set[Talk]): OnMetReq[Result] =
    validated(talks) flatMap (compose(_))

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or `IllegalArgumentException` with error message
    */
  private def validated(talks: Set[Talk]): OnMetReq[Set[Talk]] =
    talks given talks.size >= requiredSessionsNumber because
    s"Talks.size is ${talks.size}, but must be >= $requiredSessionsNumber."

  /** Actual composition; accumulates morning sessions until there is enough of them
    * or returns `IllegalArgumentException` if available (unused) talks cannot be
    * composed into morning session using given knapsack solution for some reason
    */
  @tailrec
  private def compose(unusedTalks: Set[Talk], sessions: Queue[MorningSession] = Queue()): OnMetReq[Result] =
    if (sessions.size == requiredSessionsNumber) {
      Good(new Result(sessions.toSet, unusedTalks))
    } else {
      composeSessionFrom(unusedTalks) match {
        case Bad(iea: IllegalArgumentException) => Bad(iea)
        case Good(res: MorningSessionCompositionResult) =>
          compose(res.unusedTalks, sessions :+ res.session)
      }
    }

  /** Applies [[agp.composition.MorningSessionComposition2]] to
    * given talks, returning composed session & unused talks
    */
  private def composeSessionFrom: MorningSessionComposition2 =
    talks => findSuitableCombinationsAmong(talks) match {

      case Good(combinations) if combinations.isEmpty =>
        Bad(newCompositionFailureException)

      case Good(combinations) if combinations.nonEmpty =>
        Good(newSessionCompositionResult(talks, combinations.head))

      case Bad(ex: IllegalArgumentException) =>
        Bad(newIllegalTalksException(ex))
    }

  private def newCompositionFailureException = composition.Exception(
    "Failed to compose morning session. No suitable combinations " +
    "of talks were found for given knapsack solution & talks.")

  private def newIllegalTalksException(ex: IllegalArgumentException) =
    composition.Exception(
      "Failed to compose morning session. Given talks " +
      "didn't meet requirements of knapsack solution.", ex)

  private def newSessionCompositionResult(allTalks: Set[Talk], sessionTalks: Set[Talk]) =
    new MorningSessionCompositionResult(
      session = MorningSession(sessionTalks),
      unusedTalks = allTalks except sessionTalks)

  /** An alias for better comprehension of what knapsack solution actually does */
  def findSuitableCombinationsAmong(talks: Set[Talk]) = knapsackSolution(talks)
}

object MorningSessionsCompositionImpl2 {
  type Result = MorningSessionsCompositionResult
}
