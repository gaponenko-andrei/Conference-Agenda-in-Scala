package agp.vo

import scala.concurrent.duration._

case class Talk(override val title: String, override val duration: Duration) extends Event(title, duration) {
  require(duration.toMinutes >= 5 && duration.toMinutes <= 60, "Talk must have duration 5 <= minutes <= 60.")
}

object Talk {
  def apply(title: String, min: Int): Talk = Talk(title, Duration(min, MINUTES))
}
