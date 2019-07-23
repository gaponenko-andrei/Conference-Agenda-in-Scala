package agp.composition

import agp.composition
import agp.vo.{MorningSession, Talk}
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
  * [[agp.composition.Exception]] is returned with error message.
  */
final class MorningSessionsCompositionImpl(
  val requiredSessionsNumber: Int,
  val knapsackSolution: KnapsackSolution
) extends MorningSessionsComposition {

  private type Talks = Set[Talk]
  private type Result = MorningSessionsCompositionResult

  /** Returns required number of morning sessions if composition was successful.
    * Otherwise returns [[agp.composition.Exception]] with detailed error message
    */
  override def apply(talks: Talks): ExceptionOr[Result] =
    validated(talks) flatMap (compose(_, Queue.empty))

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or [[agp.composition.Exception]] with error message
    */
  private def validated(talks: Talks): ExceptionOr[Talks] =
    if (talks.size >= requiredSessionsNumber) Good(talks)
    else Bad(composition.Exception(
      s"Talks.size is ${talks.size}, " +
      s"but must be >= $requiredSessionsNumber."))

  /** Actual composition; accumulates morning sessions until there is enough of them
    * or returns [[agp.composition.Exception]] if available (unused) talks cannot be
    * composed into morning session using given knapsack solution for some reason
    */
  @tailrec
  private def compose(unused: Talks, sessions: Queue[MorningSession]): ExceptionOr[Result] =
    if (sessions.size == requiredSessionsNumber)
      Good(new Result(sessions.toSet, unused))
    else composeSessionFrom(unused) match {
      case Bad(ex: composition.Exception) => Bad(ex)
      case Good(session: MorningSession) => compose(
        unused -- session.talks, sessions :+ session)
    }

  /** Performs composition of session for given talks, returning it along with unused talks */
  private def composeSessionFrom(talks: Talks): ExceptionOr[MorningSession] = {
    findSuitableCombinationsAmong(talks) match {

      case Bad(ex: IllegalArgumentException) =>
        Bad(newInvalidTalksException(ex))

      case Good(combinations) if combinations.isEmpty =>
        Bad(newZeroCombinationsException)

      case Good(combinations) if combinations.nonEmpty =>
        Good(MorningSession(combinations.head))
    }
  }

  private def newZeroCombinationsException = composition.Exception(
    "Failed to compose morning session. No suitable combinations " +
    "of talks were found for given knapsack solution & talks.")

  private def newInvalidTalksException(ex: IllegalArgumentException) =
    composition.Exception(
      "Failed to compose morning session. Given talks " +
      "didn't meet requirements of knapsack solution.", ex)

  /** An alias for better comprehension of what knapsack solution actually does */
  private def findSuitableCombinationsAmong(talks: Talks) = knapsackSolution(talks)
}
