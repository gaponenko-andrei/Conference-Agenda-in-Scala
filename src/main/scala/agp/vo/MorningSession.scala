package agp.vo

import scala.concurrent.duration.Duration

final case class MorningSession private(
  override val title: String,
  private val events: List[Talk]
) extends Iterable[Talk] with EventLike {

  override def iterator: Iterator[Talk] = events.iterator
  override val duration: Duration = events.duration
}

object MorningSession {

  def apply(events: Talk*): MorningSession =
    new MorningSession("MorningSession", events.toList)

  def apply(events: Iterable[Talk]): MorningSession =
    new MorningSession("MorningSession", events.toList)

  def apply(title: String, events: Iterable[Talk]): MorningSession =
    new MorningSession(title, events.toList)
}