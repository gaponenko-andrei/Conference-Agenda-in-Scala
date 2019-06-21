package agp.vo

import scala.concurrent.duration._

abstract class Event(_title: String, _duration: Duration) {

  require(_title.trim.nonEmpty)
  val title: String = _title.trim

  require(_duration.toMillis > 0)
  val duration: Duration = _duration


  override def equals(other: Any): Boolean = other match {
    case that: Event =>
      (that canEqual this) &&
        title == that.title &&
        duration == that.duration
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(title, duration)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Event]
}
