package agp

import scala.concurrent.duration.{Duration, MINUTES}

package object vo {

  abstract class Event(val title: String, val duration: Duration) {
    require(title.trim.nonEmpty, "Event title must have some chars.")
    require(duration.toMillis > 0, "Event duration must be positive.")
  }

  /* Talk */

  case class Talk(override val title: String, override val duration: Duration) extends Event(title, duration) {
    require(duration.toMinutes >= 5 && duration.toMinutes <= 60, "Talk must have duration 5 <= minutes <= 60.")
  }

  object Talk {
    def apply(title: String, min: Int): Talk = Talk(title, Duration(min, MINUTES))
  }

  /* CompositeEvent */

  case class CompositeEvent[E <: Event](override val title: String, events: List[E]) extends Event(title, events.duration) {
    require(events.nonEmpty, "CompositeEvent must have at least one event.")
  }

  /* implicits */

  implicit class Events[E <: Event](val events: Iterable[E]) {
    def duration: Duration =
      if (events.isEmpty) Duration.Zero
      else events.toList.map(_.duration).reduce(_ + _)
  }

}
