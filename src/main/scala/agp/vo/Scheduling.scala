package agp.vo

import java.time.LocalTime

import scala.concurrent.duration.Duration

final case class Scheduling(event: Event, startTime: LocalTime) extends EventLike {
  override def title: String = event.title
  override def duration: Duration = event.duration
  val endTime: LocalTime = startTime plusNanos event.duration.toNanos
}