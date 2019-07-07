package agp.scheduling

import agp.composition._
import agp.scheduling
import agp.vo._

import scala.util.control.NonFatal

class ConferenceTracksSchedulingImpl(
  val morningSessionsComposition: MorningSessionsComposition,
  val afternoonSessionsComposition: AfternoonSessionsComposition
) extends (Set[Talk] => Set[ConferenceTrack]) {

  /* aliases for clarity */
  private type Talks = Set[Talk]
  private type Tracks = Set[ConferenceTrack]
  private type MorningSessions = Set[MorningSession]
  private type AfternoonSessions = Set[AfternoonSession]


  override def apply(talks: Talks): Tracks = {
    val (morningSessions, afternoonSessions) = composeSessionsFrom(talks)
    scheduleTracksBasedOn(morningSessions zip afternoonSessions)
  }

  private def composeSessionsFrom(talks: Talks): (MorningSessions, AfternoonSessions) = {
    val (morningSessions, unusedTalks) = composeMorningSessionsFrom(talks).asPair
    (morningSessions, composeAfternoonSessionsFrom(unusedTalks))
  }

  private def scheduleTracksBasedOn(sessions: Set[(MorningSession, AfternoonSession)]): Tracks = {
    val sessionsSeq = sessions.toVector
    for (i <- sessionsSeq.indices) yield newTrack(s"Track ${i + 1}", sessionsSeq(i))
  }.toSet

  private def newTrack(title: String, sessions: (MorningSession, AfternoonSession)): ConferenceTrack =
    ConferenceTrack.newBuilder
      .schedule(sessions._1)
      .schedule(Lunch)
      .schedule(sessions._2)
      .schedule(NetworkingEvent)
      .buildWithTitle(title)

  /* Exception handling */

  private def composeMorningSessionsFrom: MorningSessionsComposition = talks =>
    try {
      morningSessionsComposition(talks)
    } catch {
      case NonFatal(ex) => throw newExceptionCausedBy(ex)
    }

  private def composeAfternoonSessionsFrom: Talks => AfternoonSessions = talks =>
    try {
      afternoonSessionsComposition(talks).sessions
    } catch {
      case NonFatal(ex) => throw newExceptionCausedBy(ex)
    }

  private def newExceptionCausedBy(ex: Throwable) =
    scheduling.Exception("Failed to schedule conference tracks.", ex)
}