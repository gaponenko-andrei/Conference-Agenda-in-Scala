package agp.scheduling

import agp.vo.EventLike

import scala.concurrent.duration.Duration

final case class ConferenceTrack private[scheduling](
  override val title: String,
  schedulings: Set[Scheduling]
) extends Iterable[Scheduling] with EventLike {

  override def duration: Duration = schedulings.duration
  override def iterator: Iterator[Scheduling] = schedulings.iterator
}