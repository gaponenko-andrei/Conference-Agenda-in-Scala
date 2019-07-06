package agp.scheduling


import agp.composition.{AfternoonSessionsComposition, MorningSessionsComposition}
import agp.scheduling
import agp.scheduling.ConferenceTracksSchedulingImpl.MinimumTrackDuration
import agp.vo.Talk

import scala.concurrent.duration._
import scala.util.control.NonFatal

class ConferenceTracksSchedulingImpl(
  val morningSessionsComposition: MorningSessionsComposition,
  val afternoonSessionsComposition: AfternoonSessionsComposition
) extends (Set[Talk] => Set[ConferenceTrack]) {


  override def apply(talks: Set[Talk]): Set[ConferenceTrack] = {
    validateDurationOf(talks)

    try {
      val morningSessionsCompositionResult = morningSessionsComposition(talks)
      val morningSessions = morningSessionsCompositionResult.sessions
      val unusedTalks = morningSessionsCompositionResult.unusedTalks

      val afternoonSessionsCompositionResult = afternoonSessionsComposition(unusedTalks)
      val afternoonSessions = afternoonSessionsCompositionResult.sessions

      assume(morningSessions.size == afternoonSessions.size)
      val tuples = (morningSessions zip afternoonSessions).toVector

      val result = for (i <- tuples.indices) yield {
        val (morningSession, afternoonSession) = tuples(i)
        ConferenceTrack.newBuilder
          .schedule(morningSession)
          .schedule(afternoonSession)
          .build(s"Track ${i + 1}")
      }

      result.toSet

    } catch {
      case NonFatal(ex) => throw newExceptionCausedBy(ex)
    }
  }

  private def validateDurationOf(talks: Set[Talk]): Unit = {
    require(talks.duration >= MinimumTrackDuration,
      s"Overall duration of talks must be >= $MinimumTrackDuration " +
      s"to schedule at least one track of morning & afternoon sessions."
    )
  }

  private def newExceptionCausedBy(ex: Throwable) =
    scheduling.Exception("Failed to schedule conference tracks.", ex)
}

object ConferenceTracksSchedulingImpl {

  val MorningSessionDuration: Duration = 3 hours

  val MinimumAfternoonSessionDuration: Duration = Talk.MinDuration

  val MaximumAfternoonSessionDuration: Duration = 4 hours

  val MinimumTrackDuration: Duration = MorningSessionDuration + MinimumAfternoonSessionDuration

  val MaximumTrackDuration: Duration = MorningSessionDuration + MaximumAfternoonSessionDuration
}
