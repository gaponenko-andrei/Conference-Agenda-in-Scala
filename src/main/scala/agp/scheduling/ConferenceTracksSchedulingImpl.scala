package agp.scheduling


import agp.composition.{AfternoonSessionsComposition, MorningSessionsComposition}
import agp.scheduling.ConferenceTracksSchedulingImpl.MinimumTrackDuration
import agp.vo.Talk

import scala.concurrent.duration._

class ConferenceTracksSchedulingImpl(
  val morningSessionsComposition: MorningSessionsComposition,
  val afternoonSessionsComposition: AfternoonSessionsComposition
) extends (Set[Talk] => Set[ConferenceTrack]) {


  override def apply(talks: Set[Talk]): Set[ConferenceTrack] = {
    validateDurationOf(talks)
    Set()
  }

  private def validateDurationOf(talks: Set[Talk]): Unit = {
    require(talks.duration >= MinimumTrackDuration,
      s"Overall duration of talks must be >= $MinimumTrackDuration " +
      s"to schedule at least one track of morning & afternoon sessions."
    )
  }
}

object ConferenceTracksSchedulingImpl {

  val MorningSessionDuration: Duration = 3 hours

  val MinimumAfternoonSessionDuration: Duration = Talk.MinDuration

  val MaximumAfternoonSessionDuration: Duration = 4 hours

  val MinimumTrackDuration: Duration = MorningSessionDuration + MinimumAfternoonSessionDuration

  val MaximumTrackDuration: Duration = MorningSessionDuration + MaximumAfternoonSessionDuration
}
