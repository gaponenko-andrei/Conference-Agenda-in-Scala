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
    scheduleTracksBasedOn(pairedSessions = morningSessions zip afternoonSessions)
  }

  private def composeSessionsFrom(talks: Talks): (MorningSessions, AfternoonSessions) = {
    val (morningSessions, unusedTalks) = composeMorningSessionsFrom(talks).asPair
    (morningSessions, composeAfternoonSessionsFrom(unusedTalks))
  }

  private def scheduleTracksBasedOn(pairedSessions: Set[(MorningSession, AfternoonSession)]): Tracks =
    pairedSessions.zipWithIndex map { case (pair, i) => newTrack(s"Track ${i + 1}", pair) }

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