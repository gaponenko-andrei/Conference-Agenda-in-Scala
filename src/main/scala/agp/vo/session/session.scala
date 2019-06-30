package agp.vo.session

import agp.vo.{EventLike, Talk}

import scala.concurrent.duration.Duration

// Session

sealed abstract class Session private[session](
  override val title: String,
  private val events: List[Talk]
) extends Iterable[Talk] with EventLike {

  final override def iterator: Iterator[Talk] = events.iterator
  final override val duration: Duration = events.duration
}

// MorningSession

final case class MorningSession private(
  override val title: String,
  private val events: List[Talk]
) extends Session(title, events)

object MorningSession {

  def apply(events: Talk*): MorningSession =
    new MorningSession("MorningSession", events.toList)

  def apply(events: Iterable[Talk]): MorningSession =
    new MorningSession("MorningSession", events.toList)

  def apply(title: String, events: Iterable[Talk]): MorningSession =
    new MorningSession(title, events.toList)
}

// AfternoonSession

class AfternoonSession private(
  override val title: String,
  private val events: List[Talk]
) extends Session(title, events)