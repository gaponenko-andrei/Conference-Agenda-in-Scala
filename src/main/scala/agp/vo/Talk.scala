package agp.vo

import scala.concurrent.duration._

class Talk(override val title: String, override val duration: Duration) extends Event(title, duration) {
  require(duration.toMinutes >= 5 && duration.toMinutes <= 60)
}

object Talk {

  def apply(title: String, min: Int): Talk = Talk(title, Duration(min, MINUTES))

  def apply(title: String, duration: Duration): Talk = new Talk(title, duration)
}
