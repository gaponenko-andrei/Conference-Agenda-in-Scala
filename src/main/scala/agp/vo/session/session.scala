package agp.vo.session

import agp.vo.EventLike
import agp.vo.event.Talk

import scala.concurrent.duration.Duration

// Session

sealed abstract class Session private[session](
  override val title: String,
  private val events: Set[Talk]
) extends Iterable[Talk] with EventLike {

  final override def iterator: Iterator[Talk] = events.iterator
  final override val duration: Duration = events.duration
}

// MorningSession

final case class MorningSession private(
  override val title: String,
  private val events: Set[Talk]
) extends Session(title, events)

object MorningSession {

  def apply(events: Talk*): MorningSession =
    new MorningSession("MorningSession", events.toSet)

  def apply(events: Set[Talk]): MorningSession =
    new MorningSession("MorningSession", events)
}

// AfternoonSession

final case class AfternoonSession private(
  override val title: String,
  private val events: Set[Talk]
) extends Session(title, events)

object AfternoonSession {

  def apply(events: Set[Talk]): AfternoonSession =
    new AfternoonSession("AfternoonSession", events)

}