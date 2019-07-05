package agp.scheduling

import java.time.LocalTime

import agp.vo.{Event, EventLike}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration

final case class ConferenceTrack private(
  override val title: String,
  private val schedulings: Vector[Scheduling]
) extends Iterable[Scheduling] with EventLike {

  override def duration: Duration = schedulings.duration
  override def iterator: Iterator[Scheduling] = schedulings.iterator
}

object ConferenceTrack {

  def newBuilder: Builder = new Builder

  final class Builder {

    private val startTime = LocalTime.of(9, 0)
    private val schedulings = new ArrayBuffer[Scheduling]

    def schedule(sequence: Event*): Builder = {
      sequence.foreach(schedule)
      this
    }

    def schedule(event: Event): Builder = {
      requireNonScheduled(event)
      if (schedulings.isEmpty) {
        scheduleFirst(event)
      } else {
        scheduleLatest(event)
      }
      this
    }

    def build: ConferenceTrack = build("Track")

    def build(title: String): ConferenceTrack =
      new ConferenceTrack(title, schedulings.toVector)

    private def scheduleFirst(event: Event): Unit =
      schedulings.append(Scheduling(event, startTime))

    private def scheduleLatest(event: Event): Unit =
      schedulings.append(Scheduling(event, schedulings.last.endTime))

    private def requireNonScheduled(event: Event): Unit =
      require(!schedulings.exists(_.event == event), "Event can't be scheduled twice.")
  }
}
