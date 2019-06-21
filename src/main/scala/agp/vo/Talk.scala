package agp.vo

import scala.concurrent.duration._

class Talk(override val title: String, override val duration: Duration) extends Event(title, duration) {

  require(duration.toMinutes >= 5 && duration.toMinutes <= 60)


}
