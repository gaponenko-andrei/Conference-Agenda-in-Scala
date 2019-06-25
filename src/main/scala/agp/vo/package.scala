package agp

import scala.concurrent.duration.Duration._
import scala.concurrent.duration.{Duration, MINUTES}

package object vo {

  /* extension methods */
  implicit class Events[E <: Event](val events: Iterable[E]) {

    def duration: Duration = events.toList.map(_.duration).foldLeft(Zero.asInstanceOf[Duration])(_ + _)

    def except(otherEvents: Set[E]): Set[E] = events.toSet diff otherEvents
  }

  sealed abstract class Event(val title: String, val duration: Duration) {
    require(title.trim.nonEmpty, s"${getClass.getSimpleName} title must have some chars.")
    require(duration.toMillis > 0, s"${getClass.getSimpleName} duration must be positive.")
  }

  /* Talk */

  type TalksCombination = Set[Talk]
  type TalksCombinations = Set[TalksCombination]

  case class Talk(override val title: String, override val duration: Duration) extends Event(title, duration) {
    require(duration.toMinutes >= 5 && duration.toMinutes <= 60, "Talk must have duration 5 <= minutes <= 60.")
  }

  object Talk {
    def apply(title: String, min: Int): Talk = Talk(title, Duration(min, MINUTES))
  }

  /* CompositeEvent */

  sealed abstract class CompositeEvent[E <: Event](override val title: String, val events: List[E])
    extends Event(title, events.duration) with Iterable[E] {

    val iterator: Iterator[E] = events.iterator
  }

  /* MorningSession */

  case class MorningSession[E <: Event](override val title: String, override val events: List[E])
    extends CompositeEvent(title, events)

  object MorningSession {
    def apply[E <: Event](events: Iterable[E]): MorningSession[E] = MorningSession("MorningSession", events.toList)
  }

}
