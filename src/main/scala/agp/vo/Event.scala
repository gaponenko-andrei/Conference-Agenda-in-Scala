package agp.vo

import scala.concurrent.duration._

abstract class Event(val title: String, val duration: Duration) {
  require(title.trim.nonEmpty, "Event title must have some chars.")
  require(duration.toMillis > 0, "Event duration must be positive.")
}
