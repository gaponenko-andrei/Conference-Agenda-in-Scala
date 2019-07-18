package agp.scheduling

import java.time.LocalTime

import agp.Utils.OnMetReq
import agp.composition._
import agp.scheduling
import agp.vo._
import org.scalactic.Or

/** Function to schedule [[agp.scheduling.ConferenceTrack]]s with
  * given start time, morning & afternoon sessions compositions.
  *
  * Specifically, morning & afternoon sessions are composed, zipped
  * and scheduled in a way that each event will have a specific start
  * time and end time, represented by [[agp.scheduling.Scheduling]].
  *
  * For each scheduled conference track, this particular implementation
  * will place [[agp.vo.Lunch]] after morning session talks, as well
  * as [[agp.vo.NetworkingEvent]] following afternoon session talks.
  */
class ConferenceTracksSchedulingImpl2(val trackStartTime: LocalTime)(
  val morningSessionsComposition: MorningSessionsComposition2,
  val afternoonSessionsComposition: AfternoonSessionsComposition2
) extends ConferenceTracksScheduling2 {

  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]
  private type SessionsPair = (MorningSession, AfternoonSession)
  private type PairedSessions = Set[SessionsPair]

  /** Returns conference tracks scheduled from given talks if scheduling
    * was successful, otherwise returns exception with details on error
    */
  override def apply(allTalks: Talks): Tracks Or scheduling.Exception =
    schedule(allTalks) badMap detalize2SchedulingException

  /** Wraps given throwable into specialized (scheduling) exception
    * to provide some additional context of what went wrong and where
    */
  private def detalize2SchedulingException(ex: Throwable) =
    scheduling.Exception("Failed to schedule conference tracks.", ex)

  /** Performs actual scheduling and returns conference tracks if scheduling
    * was successful, otherwise returns cause exception with details on error
    */
  private def schedule(allTalks: Talks): OnMetReq[Tracks] = for {
    i <- morningSessionsComposition(allTalks)
    j <- afternoonSessionsComposition(i.unusedTalks)
  } yield scheduleTracksBasedOn(pairedSessions = i.sessions zip j.sessions)

  /** Creates conference tracks for each pair of morning & afternoon sessions */
  private def scheduleTracksBasedOn(pairedSessions: PairedSessions): Tracks =
    pairedSessions.zipWithIndex map {
      case (pair, i) => newTrack(s"Track #${i + 1}", pair)
    }

  /** Creates track with given title for pair of morning & afternoon sessions */
  private def newTrack(title: String, sessions: SessionsPair): ConferenceTrack =
    new TrackBuilder()
      .schedule(sessions._1.talks)
      .schedule(Lunch)
      .schedule(sessions._2.talks)
      .schedule(NetworkingEvent)
      .buildWith(title)

  /** Helper class to make construction of conference track a bit easier on the eyes
    * and encapsulate logic choosing start time & end time for each specific event
    */
  private final class TrackBuilder {

    private val schedulings = new scala.collection.mutable.Queue[Scheduling]

    def schedule(sequence: Set[Talk]): TrackBuilder = {
      sequence.foreach(schedule)
      this
    }

    def schedule(event: Event): TrackBuilder = {
      schedulings.enqueue(newSchedulingOf(event))
      this
    }

    def buildWith(title: String): ConferenceTrack =
      new ConferenceTrack(title, schedulings.toSet)

    private def newSchedulingOf(event: Event) =
      Scheduling(event, startTime = {
        if (schedulings.isEmpty) trackStartTime
        else schedulings.last.endTime
      })
  }
}