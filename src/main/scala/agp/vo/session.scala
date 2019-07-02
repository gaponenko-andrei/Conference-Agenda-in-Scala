package agp.vo

import scala.concurrent.duration.Duration

// Session

sealed abstract class Session protected(
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

  def apply(events: Set[Talk]): MorningSession =
    new MorningSession("MorningSession", events)

  def apply(events: Talk*): MorningSession =
    new MorningSession("MorningSession", events.toSet)
}

// AfternoonSession

final case class AfternoonSession private(
  override val title: String,
  private val events: Set[Talk]
) extends Session(title, events)

object AfternoonSession {

  def apply(events: Set[Talk]): AfternoonSession =
    new AfternoonSession("AfternoonSession", events)

  def apply(events: Talk*): AfternoonSession =
    new AfternoonSession("AfternoonSession", events.toSet)
}