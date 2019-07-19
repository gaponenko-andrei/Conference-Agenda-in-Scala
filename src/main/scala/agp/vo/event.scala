package agp.vo

import agp.vo.Talk.{MaxDuration, MinDuration}

import scala.concurrent.duration._


trait EventLike {
  def title: String
  def duration: Duration
}

// Event

sealed abstract class Event extends EventLike {

  def canEqual(other: Any): Boolean = other.isInstanceOf[Event]

  // event is a reference object - equality is determined by title
  final override def equals(other: Any): Boolean = other match {
    case that: Event => (that canEqual this) && title == that.title
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
  require(duration >= MinDuration && duration <= MaxDuration,
         s"Talk duration must ∈ [$MinDuration, $MaxDuration].")

  override def canEqual(other: Any): Boolean = other.isInstanceOf[Talk]
}

object Talk {

  val MinDuration: Duration = 5 minutes
  val MaxDuration: Duration = 60 minutes

  def apply(title: String, minutes: Int): Talk =
    new Talk(title.trim, Duration(minutes, MINUTES)) {}
}