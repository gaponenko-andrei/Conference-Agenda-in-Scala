package agp.composition

import agp.vo._

class ConferenceTracksSchedulerImpl(
  val morningSessionsScheduler: MorningSessionsComposition
) extends (Set[Talk] => Set[ConferenceTrack]) {

  override def apply(v1: Set[Talk]): Set[ConferenceTrack] = ???

}
