package agp.vo

import scala.concurrent.duration.{Duration, MINUTES}


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
