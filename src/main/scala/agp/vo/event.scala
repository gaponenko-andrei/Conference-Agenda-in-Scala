package agp.vo

import scala.concurrent.duration._

// Event

sealed abstract class Event extends EventLike {

  final def canEqual(other: Any): Boolean = this.getClass == other.getClass

  // event is a reference object - equality is defined in terms of title
  final override def equals(other: Any): Boolean = other match {
    case that: Event =>
      (that canEqual this) &&
      title == that.title
    case _ => false
  }

  final override def hashCode(): Int = {
    val state = Seq(title)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

// Lunch

object Lunch extends Event {
  override val title: String = "Lunch"
  override val duration: Duration = 1 hour
}

// NetworkingEvent

object NetworkingEvent extends Event {
  override val title: String = "Networking Event"
  override val duration: Duration = 2 hours
}

// Talk

sealed abstract case class Talk private(
  override val title: String,
  override val duration: Duration
) extends Event {

  private val min = duration.toMinutes
  require(title.nonEmpty, "Talk title must have some chars.")
  require(min >= 5 && min <= 60, "Talk duration must be 5 <= minutes <= 60.")
}

object Talk {
  def apply(title: String, minutes: Int): Talk =
    new Talk(title.trim, Duration(minutes, MINUTES)) {}
}