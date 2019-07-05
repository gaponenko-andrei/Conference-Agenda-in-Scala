package agp.vo

import scala.concurrent.duration._
import Talk.{MaxDuration, MinDuration}

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

  require(title.nonEmpty, "Talk title must have some chars.")
  require(
    duration >= MinDuration && duration <= MaxDuration,
    s"Talk duration must ∈ [$MinDuration, $MaxDuration]."
  )
}

object Talk {

  val MinDuration: Duration = 5 minutes
  val MaxDuration: Duration = 60 minutes

  def apply(title: String, minutes: Int): Talk =
    new Talk(title.trim, Duration(minutes, MINUTES)) {}
}