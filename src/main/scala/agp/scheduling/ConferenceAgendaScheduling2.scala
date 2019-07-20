package agp.scheduling

import java.time.LocalTime

import agp.composition._
import agp.scheduling
import agp.scheduling.ConferenceAgendaScheduling2._
import agp.vo.Talk
import agp.weighting.KnapsackSolutionForTalks2
import org.scalactic.{Bad, Good}

import scala.concurrent.duration._

/** Function to schedule [[agp.scheduling.ConferenceTrack]]s with
  * specific setup of parameters and component implementations, binding
  * it all together, in one place, to perform basic dependency injection.
  */
class ConferenceAgendaScheduling2 extends ConferenceTracksScheduling2 {

  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]

  /** Returns conference tracks scheduled from given talks if scheduling
    * was successful, otherwise returns exception with details on error
    */
  override def apply(talks: Talks): ExceptionOr[Tracks] =
    validated(talks) flatMap scheduleTracks

  /** Returns validated instance of given talks in case they pass preconditions
    * set by this function or [[agp.scheduling.Exception]] with error message
    */
  private def validated(talks: Talks): ExceptionOr[Talks] =
    if (talks.duration >= MinTrackDuration) Good(talks)
    else Bad(scheduling.Exception(
      s"Overall duration of talks must be >= $MinTrackDuration to " +
      "schedule at least one track of morning & afternoon sessions."))

  /** Performs actual scheduling and returns conference tracks if scheduling
    * was successful, otherwise returns cause exception with details on error
    */
  private def scheduleTracks(talks: Talks): ExceptionOr[Tracks] = {
    val requiredTracksNumber = calcRequiredTracksNumberFor(talks)
    new ConferenceTracksSchedulingImpl2(TrackStartTime)(
      newMorningSessionsCompositionFor(requiredTracksNumber),
      newAfternoonSessionsCompositionFor(requiredTracksNumber)
    ).apply(talks)
  }

  /** Calculates number of conference tracks required to put in
    * all given talks according to defined time constraints
    */
  private def calcRequiredTracksNumberFor(talks: Talks): Int = {
    val talksDurationInMinutes = talks.duration.toMinutes
    val maxTrackDurationInMinutes = MaxTrackDuration.toMinutes
    val div = talksDurationInMinutes / maxTrackDurationInMinutes
    val mod = talksDurationInMinutes % maxTrackDurationInMinutes
    (if (mod == 0) div else div + 1).toInt
  }

  private def newMorningSessionsCompositionFor(requiredTracksNumber: Int) =
    new MorningSessionsCompositionImpl2(requiredTracksNumber,
      KnapsackSolutionForTalks2(goal = MorningSessionDuration))

  private def newAfternoonSessionsCompositionFor(requiredTracksNumber: Int) =
    new AfternoonSessionsCompositionImpl2(requiredTracksNumber)
}

object ConferenceAgendaScheduling2 {

  /** Determines when should the first event be scheduled */
  private val TrackStartTime: LocalTime = LocalTime.of(9, 0)

  /** Determines how long [[agp.vo.MorningSession]] MUST be */
  private val MorningSessionDuration: Duration = 3 hours

  /** Determines minimum duration of [[agp.vo.AfternoonSession]]s */
  private val MinAfternoonSessionDuration: Duration = Talk.MinDuration

  /** Determines maximum duration of [[agp.vo.AfternoonSession]]s */
  private val MaxAfternoonSessionDuration: Duration = 4 hours

  /** Determines minimum duration of [[agp.scheduling.ConferenceTrack]] */
  private val MinTrackDuration: Duration = MorningSessionDuration + MinAfternoonSessionDuration

  /** Determines maximum duration of [[agp.scheduling.ConferenceTrack]] */
  private val MaxTrackDuration: Duration = MorningSessionDuration + MaxAfternoonSessionDuration

  def apply(talks: Set[Talk]): Set[ConferenceTrack] = new ConferenceAgendaScheduling()(talks)
}