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
    val zippedSessions = (morningSessions zip afternoonSessions).toVector
    scheduleTracksBasedOn(zippedSessions)
  }

  private def composeSessionsFrom(talks: Talks): (MorningSessions, AfternoonSessions) = {
    val (morningSessions, unusedTalks) = composeMorningSessionsFrom(talks).asPair
    val afternoonSessions = composeAfternoonSessionsFrom(unusedTalks).sessions
    (morningSessions, afternoonSessions)
  }

  private def scheduleTracksBasedOn(sessions: Seq[(MorningSession, AfternoonSession)]): Tracks =
    (for (i <- sessions.indices) yield newTrack(s"Track ${i + 1}", sessions(i))).toSet

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
      case NonFatal(ex) => throw newExceptionCausesBy(ex)
    }

  private def composeAfternoonSessionsFrom: AfternoonSessionsComposition = talks =>
    try {
      afternoonSessionsComposition(talks)
    } catch {
      case NonFatal(ex) => throw newExceptionCausesBy(ex)
    }

  private def newExceptionCausesBy(ex: Throwable) =
    scheduling.Exception("Failed to schedule conference tracks.", ex)
}