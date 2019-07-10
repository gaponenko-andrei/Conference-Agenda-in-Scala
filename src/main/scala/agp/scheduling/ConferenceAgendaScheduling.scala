package agp.scheduling

import agp.composition._
import agp.scheduling.ConferenceAgendaScheduling._
import agp.vo.Talk
import agp.weighting.KnapsackSolutionForTalks

import scala.concurrent.duration._

class ConferenceAgendaScheduling extends (Set[Talk] => Set[ConferenceTrack]) {

  private lazy val morningSessionComposition = new MorningSessionCompositionImpl(
    knapsackSolution = new KnapsackSolutionForTalks()(MorningSessionDuration)(_))


  override def apply(talks: Set[Talk]): Set[ConferenceTrack] = {
    validateDurationOf(talks)
    scheduleTracksFor(talks)
  }

  private def validateDurationOf(talks: Set[Talk]): Unit = {
    require(talks.duration >= MinTalksDuration,
      s"Overall duration of talks must be >= $MinTalksDuration to " +
      s"schedule at least one track of morning & afternoon sessions.")
  }

  private def scheduleTracksFor(talks: Set[Talk]): Set[ConferenceTrack] = {
    val requiredTracksNumber = calcRequiredTracksNumberFor(talks)
    new ConferenceTracksSchedulingImpl(
      newMorningSessionsCompositionFor(requiredTracksNumber),
      newAfternoonSessionsCompositionFor(requiredTracksNumber)
    ).apply(talks)
  }

  private def calcRequiredTracksNumberFor(talks: Set[Talk]): Int = {
    val talksDurationInMinutes = talks.duration.toMinutes
    val maxTalksDurationInMinutes = MaxTalksDuration.toMinutes
    val div = talksDurationInMinutes / maxTalksDurationInMinutes
    val mod = talksDurationInMinutes % maxTalksDurationInMinutes
    (if (mod == 0) div else div + 1).toInt
  }

  private def newMorningSessionsCompositionFor(requiredTracksNumber: Int) =
    new MorningSessionsCompositionImpl(morningSessionComposition, requiredTracksNumber)

  private def newAfternoonSessionsCompositionFor(requiredTracksNumber: Int) =
    new AfternoonSessionsCompositionImpl(requiredTracksNumber)
}

object ConferenceAgendaScheduling {

  private val MorningSessionDuration: Duration = 3 hours

  private val MinAfternoonSessionDuration: Duration = Talk.MinDuration

  private val MaxAfternoonSessionDuration: Duration = 4 hours

  private val MinTalksDuration: Duration = MorningSessionDuration + MinAfternoonSessionDuration

  private val MaxTalksDuration: Duration = MorningSessionDuration + MaxAfternoonSessionDuration

  def apply(talks: Set[Talk]): Set[ConferenceTrack] = new ConferenceAgendaScheduling()(talks)
}