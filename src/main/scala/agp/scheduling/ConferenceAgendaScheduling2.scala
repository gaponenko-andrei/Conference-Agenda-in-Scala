package agp.scheduling

import java.time.LocalTime

import agp.composition._
import agp.scheduling
import agp.scheduling.ConferenceAgendaScheduling2._
import agp.vo.Talk
import agp.weighting.KnapsackSolutionForTalks2
import org.scalactic.{Bad, Good}

import scala.concurrent.duration._

class ConferenceAgendaScheduling2 extends ConferenceTracksScheduling2 {

  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]

  override def apply(talks: Talks): ExceptionOr[Tracks] =
    validated(talks) flatMap scheduleTracks

  private def validated(talks: Talks): ExceptionOr[Talks] =
    if (talks.duration >= MinTalksDuration) Good(talks)
    else Bad(scheduling.Exception(
      s"Overall duration of talks must be >= $MinTalksDuration to " +
      "schedule at least one track of morning & afternoon sessions."))

  private def scheduleTracks(talks: Talks): ExceptionOr[Tracks] = {
    val requiredTracksNumber = calcRequiredTracksNumberFor(talks)
    new ConferenceTracksSchedulingImpl2(TrackStartTime)(
      newMorningSessionsCompositionFor(requiredTracksNumber),
      newAfternoonSessionsCompositionFor(requiredTracksNumber)
    ).apply(talks)
  }

  private def calcRequiredTracksNumberFor(talks: Talks): Int = {
    val talksDurationInMinutes = talks.duration.toMinutes
    val maxTalksDurationInMinutes = MaxTalksDuration.toMinutes
    val div = talksDurationInMinutes / maxTalksDurationInMinutes
    val mod = talksDurationInMinutes % maxTalksDurationInMinutes
    (if (mod == 0) div else div + 1).toInt
  }

  private def newMorningSessionsCompositionFor(requiredTracksNumber: Int) =
    new MorningSessionsCompositionImpl2(requiredTracksNumber,
      KnapsackSolutionForTalks2(goal = MorningSessionDuration))

  private def newAfternoonSessionsCompositionFor(requiredTracksNumber: Int) =
    new AfternoonSessionsCompositionImpl2(requiredTracksNumber)
}

object ConferenceAgendaScheduling2 {

  private val TrackStartTime: LocalTime = LocalTime.of(9, 0)

  private val MorningSessionDuration: Duration = 3 hours

  private val MinAfternoonSessionDuration: Duration = Talk.MinDuration

  private val MaxAfternoonSessionDuration: Duration = 4 hours

  private val MinTalksDuration: Duration = MorningSessionDuration + MinAfternoonSessionDuration

  private val MaxTalksDuration: Duration = MorningSessionDuration + MaxAfternoonSessionDuration

  def apply(talks: Set[Talk]): Set[ConferenceTrack] = new ConferenceAgendaScheduling()(talks)
}