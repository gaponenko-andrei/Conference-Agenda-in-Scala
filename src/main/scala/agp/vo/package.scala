package agp

import scala.concurrent.duration.Duration
import scala.concurrent.duration.Duration._

package object vo {

  type TalksCombinations = Set[Set[Talk]]

  implicit class Events[E <: EventLike](val events: Iterable[E]) {
    def duration: Duration = events.toList.map(_.duration).foldLeft(Zero.asInstanceOf[Duration])(_ + _)
  }
}
