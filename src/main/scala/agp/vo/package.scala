package agp

import scala.concurrent.duration.Duration

package object vo {

  implicit class Events[E <: Event](val events: Iterable[E]) {
    def duration: Duration =
      if (events.isEmpty) Duration.Zero
      else events.toList.map(_.duration).reduce(_ + _)
  }

}
