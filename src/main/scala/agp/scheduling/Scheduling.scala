package agp.scheduling

import java.time.LocalTime

import agp.vo.{Event, EventLike}

import scala.concurrent.duration.Duration

/** Binding of event to specific start & end time */
final case class Scheduling private[scheduling](event: Event, startTime: LocalTime) extends EventLike {
  override def title: String = event.title
  override def duration: Duration = event.duration
  val endTime: LocalTime = startTime plusNanos event.duration.toNanos
}

object Scheduling {
  /** Default ordering - ascending by start time */
  implicit def orderingByStartTime: Ordering[Scheduling] = Ordering.by(_.startTime)
}