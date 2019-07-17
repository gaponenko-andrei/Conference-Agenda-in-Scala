package agp.scheduling

import java.time.LocalTime

import agp.Utils.OnMetReq
import agp.composition._
import agp.scheduling
import agp.vo._
import org.scalactic.Or

class ConferenceTracksSchedulingImpl2(val trackStartTime: LocalTime)(
  val morningSessionsComposition: MorningSessionsComposition2,
  val afternoonSessionsComposition: AfternoonSessionsComposition2
) extends ConferenceTracksScheduling2 {

  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]
  private type SessionsPair = (MorningSession, AfternoonSession)
  private type PairedSessions = Set[SessionsPair]


  override def apply(allTalks: Talks): Tracks Or scheduling.Exception =
    schedule(allTalks) badMap detalize2SchedulingException

  private def detalize2SchedulingException(ex: Throwable) =
    scheduling.Exception("Failed to schedule conference tracks.", ex)

  private def schedule(allTalks: Talks): OnMetReq[Tracks] = for {
    i <- morningSessionsComposition(allTalks)
    j <- afternoonSessionsComposition(i.unusedTalks)
  } yield scheduleTracksBasedOn(pairedSessions = i.sessions zip j.sessions)

  private def scheduleTracksBasedOn(pairedSessions: PairedSessions): Tracks =
    pairedSessions.zipWithIndex map {
      case (pair, i) => newTrack(s"Track #${i + 1}", pair)
    }

  private def newTrack(title: String, sessions: SessionsPair): ConferenceTrack =
    new TrackBuilder()
      .schedule(sessions._1.talks)
      .schedule(Lunch)
      .schedule(sessions._2.talks)
      .schedule(NetworkingEvent)
      .buildWith(title)


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