package agp.composition

import agp.vo._
import agp.vo.session.MorningSession

import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.util.control.NonFatal


private[composition] class MorningSessionsCompositionImpl(
  val morningSessionComposition: MorningSessionComposition,
  val requiredSessionsNumber: Int
) extends (Set[Talk] => MorningSessionsCompositionResult) {

  /* shorter alias for result type */
  private type Result = MorningSessionsCompositionResult


  def apply(talks: Set[Talk]): Result = {
    validateNumberOf(talks)

    @tailrec
    def compose(unusedTalks: Set[Talk], sessions: Queue[MorningSession] = Queue()): Result = {
      if (sessions.size == requiredSessionsNumber) {
        new Result(sessions.toSet, unusedTalks)
      } else {
        val result = composeSessionFrom(unusedTalks)
        compose(result.unusedTalks, sessions :+ result.session)
      }
    }

    compose(talks)
  }

  private def validateNumberOf(talks: Set[Talk]): Unit = {
    require(talks.size >= requiredSessionsNumber, s"Talks.size must be >= $requiredSessionsNumber.")
  }

  private def composeSessionFrom(talks: Set[Talk]): MorningSessionCompositionResult = {
    try {
      morningSessionComposition(talks)
    } catch {
      case NonFatal(ex) => throw newExceptionCausedBy(ex)
    }
  }

  private def newExceptionCausedBy(ex: Throwable) = CompositionException(
    "Failed to compose required number of morning " +
    "sessions with given MorningSessionComposition.", ex
  )
}
