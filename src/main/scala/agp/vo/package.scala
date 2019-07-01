package agp

import agp.vo.event.Talk

import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration._

package object vo {

  trait EventLike {
    def title: String
    def duration: Duration
  }

  type TalksCombinations = Set[Set[Talk]]

  implicit class Events[E <: EventLike](val events: Iterable[E]) {
    def except(otherEvents: Set[E]): Set[E] = events.toSet diff otherEvents
    def duration: Duration = events.toList.map(_.duration).foldLeft(Zero.asInstanceOf[Duration])(_ + _)
  }
}
