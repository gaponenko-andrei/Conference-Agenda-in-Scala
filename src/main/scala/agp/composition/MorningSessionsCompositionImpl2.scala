package agp.composition

import agp.Utils._
import agp.composition
import agp.composition.MorningSessionsCompositionImpl2.Result
import agp.vo.{MorningSession, Talk, TalksCombinations}
import org.scalactic.{Bad, Good}

import scala.annotation.tailrec
import scala.collection.immutable.Queue

/** Component to compose required number of morning sessions
  * using given [[agp.composition.KnapsackSolution]] for talks.
  *
  * Returns [[agp.composition.MorningSessionsCompositionResult]] if
  * composition was successful, [[IllegalArgumentException]] otherwise.
  */
final class MorningSessionsCompositionImpl2(
  val requiredSessionsNumber: Int,
  val knapsackSolution: (Set[Talk] => OnMetReq[TalksCombinations])
) extends MorningSessionsComposition2 {

  override def apply(talks: Set[Talk]): OnMetReq[Result] =
    validated(talks) flatMap (compose(_))

  private def validated(talks: Set[Talk]): OnMetReq[Set[Talk]] =
    talks given talks.size >= requiredSessionsNumber because
    s"Talks.size is ${talks.size}, but must be >= $requiredSessionsNumber."

  @tailrec
  private def compose(unusedTalks: Set[Talk], sessions: Queue[MorningSession] = Queue()): OnMetReq[Result] =
    if (sessions.size == requiredSessionsNumber) {
      Good(new Result(sessions.toSet, unusedTalks))
    } else {
      composeSessionFrom(unusedTalks) match {
        case Bad(ex: IllegalArgumentException) => Bad(ex)
        case Good(res: MorningSessionCompositionResult) =>
          compose(res.unusedTalks, sessions :+ res.session)
      }
    }

  /* Methods to compose instance of MorningSession */

  private def composeSessionFrom: MorningSessionComposition2 =
    talks => findSuitableCombinationsAmong(talks) match {

      case Bad(ex: IllegalArgumentException) =>
        Bad(newIllegalTalksException(ex))

      case Good(combinations) if combinations.isEmpty =>
        Bad(newCompositionFailureException)

      case Good(combinations) if combinations.nonEmpty =>
        Good(newSessionCompositionResult(talks, combinations.head))
    }

  /* alias for better comprehension of what knapsack solution is supposed to do */
  def findSuitableCombinationsAmong(talks: Set[Talk]) = knapsackSolution(talks)

  private def newIllegalTalksException(ex: IllegalArgumentException) =
    composition.Exception(
      "Failed to compose morning session. Given talks " +
      "didn't meet requirements of knapsack solution.", ex)

  private def newCompositionFailureException = composition.Exception(
    "Failed to compose morning session. No suitable combinations " +
    "of talks were found for given knapsack solution & talks.")

  private def newSessionCompositionResult(allTalks: Set[Talk], sessionTalks: Set[Talk]) =
    new MorningSessionCompositionResult(
      session = MorningSession(sessionTalks),
      unusedTalks = allTalks except sessionTalks)
}

object MorningSessionsCompositionImpl2 {
  type Result = MorningSessionsCompositionResult
}
