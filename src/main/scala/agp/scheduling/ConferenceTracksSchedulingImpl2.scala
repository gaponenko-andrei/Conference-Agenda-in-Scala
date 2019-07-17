package agp.scheduling

import agp.Utils.OnMetReq
import agp.composition._
import agp.vo._

class ConferenceTracksSchedulingImpl2(
  val morningSessionsComposition: MorningSessionsComposition2,
  val afternoonSessionsComposition: AfternoonSessionsComposition2
) extends ConferenceTracksScheduling2 {

  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]
  private type SessionsPair = (MorningSession, AfternoonSession)
  private type PairedSessions = Set[SessionsPair]


  override def apply(allTalks: Talks): OnMetReq[Tracks] = for {
    i: MorningSessionsCompositionResult <- morningSessionsComposition(allTalks)
    j: AfternoonSessionsCompositionResult <- afternoonSessionsComposition(i.unusedTalks)
  } yield scheduleTracksBasedOn(pairedSessions = i.sessions zip j.sessions)

  private def scheduleTracksBasedOn(pairedSessions: PairedSessions): Tracks =
    pairedSessions.zipWithIndex map { case (pair, i) =>
      newTrack(s"Track #${i + 1}", pair)
    }

  private def newTrack(title: String, sessions: SessionsPair): ConferenceTrack =
    ConferenceTrack.newBuilder
      .schedule(sessions._1)
      .schedule(Lunch)
      .schedule(sessions._2)
      .schedule(NetworkingEvent)
      .buildWithTitle(title)
}