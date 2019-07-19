package agp.composition

import agp.Utils._
import agp.composition
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

  private type Result = MorningSessionsCompositionResult
  // todo think about removing type altogether
  private type SessionCompositionResult = agp.composition.SessionCompositionResult[MorningSession]

  /** Returns required number of morning sessions if composition was successful.
    * Otherwise returns [[agp.composition.Exception]] with detailed error message
    */
  override def apply(talks: Set[Talk]): ExceptionOr[Result] =
    validated(talks) flatMap (compose(_, Queue.empty))

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or [[agp.composition.Exception]] with error message
    */
  private def validated(talks: Set[Talk]): ExceptionOr[Set[Talk]] =
    if (talks.size >= requiredSessionsNumber) Good(talks)
    else Bad(composition.Exception(
      s"Talks.size is ${talks.size}, " +
      s"but must be >= $requiredSessionsNumber."))

  /** Actual composition; accumulates morning sessions until there is enough of them
    * or returns [[agp.composition.Exception]] if available (unused) talks cannot be
    * composed into morning session using given knapsack solution for some reason
    */
  @tailrec
  private def compose(unused: Set[Talk], sessions: Queue[MorningSession]): ExceptionOr[Result] =
    if (sessions.size == requiredSessionsNumber)
      Good(new Result(sessions.toSet, unused))
    else composeSessionFrom(unused) match {
      case Bad(e: composition.Exception) => Bad(e)
      case Good(i: SessionCompositionResult) =>
        compose(i.unusedTalks, sessions :+ i.session)
    }

  /** Performs composition of session for given talks, returning it along with unused talks
    */
  private def composeSessionFrom(talks: Set[Talk]): ExceptionOr[SessionCompositionResult] = {
    findSuitableCombinationsAmong(talks) match {

      case Good(combinations) if combinations.isEmpty =>
        Bad(newZeroCombinationsException)

      case Good(combinations) if combinations.nonEmpty =>
        Good(newSessionCompositionResult(talks, combinations.head))

      case Bad(ex: IllegalArgumentException) =>
        Bad(newInvalidTalksException(ex))
    }
  }

  private def newZeroCombinationsException = composition.Exception(
    "Failed to compose morning session. No suitable combinations " +
    "of talks were found for given knapsack solution & talks.")

  private def newInvalidTalksException(ex: IllegalArgumentException) =
    composition.Exception(
      "Failed to compose morning session. Given talks " +
      "didn't meet requirements of knapsack solution.", ex)

  private def newSessionCompositionResult(allTalks: Set[Talk], sessionTalks: Set[Talk]) =
    new MorningSessionCompositionResult(
      session = MorningSession(sessionTalks),
      unusedTalks = allTalks -- sessionTalks)

  /** An alias for better comprehension of what knapsack solution actually does */
  private def findSuitableCombinationsAmong(talks: Set[Talk]) = knapsackSolution(talks)
}
