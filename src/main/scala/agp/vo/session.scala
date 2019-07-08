package agp.vo

import scala.concurrent.duration.Duration

// Session

sealed abstract class Session protected(
  override val title: String,
  val talks: Set[Talk]
) extends Iterable[Talk] with EventLike {

  final override def iterator: Iterator[Talk] = talks.iterator
  final override val duration: Duration = talks.duration
}

// MorningSession

final case class MorningSession private(
  override val title: String,
  override val talks: Set[Talk]
) extends Session(title, talks)

object MorningSession {

  def apply(events: Set[Talk]): MorningSession =
    new MorningSession("MorningSession", events)

  def apply(events: Talk*): MorningSession =
    new MorningSession("MorningSession", events.toSet)
}

// AfternoonSession

final case class AfternoonSession private(
  override val title: String,
  override val talks: Set[Talk]
) extends Session(title, talks)

object AfternoonSession {

  def apply(events: Set[Talk]): AfternoonSession =
    new AfternoonSession("AfternoonSession", events)

  def apply(events: Talk*): AfternoonSession =
    new AfternoonSession("AfternoonSession", events.toSet)
}