package agp.scheduling

import java.time.LocalTime

import agp.vo.{Event, EventLike}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration

final case class ConferenceTrack private(
  override val title: String,
  private val schedulings: Set[Scheduling]
) extends Set[Scheduling] with EventLike {

  override def duration: Duration = schedulings.duration
  override def iterator: Iterator[Scheduling] = schedulings.iterator
  override def contains(elem: Scheduling): Boolean = schedulings.contains(elem)
  override def +(elem: Scheduling): Set[Scheduling] = throw new UnsupportedOperationException
  override def -(elem: Scheduling): Set[Scheduling] = throw new UnsupportedOperationException
}

object ConferenceTrack {

  def newBuilder: Builder = new Builder

  final class Builder {

    private val startTime = LocalTime.of(9, 0)
    private val schedulings = new ArrayBuffer[Scheduling]

    def schedule(sequence: Event*): Builder = schedule(sequence.toIterable)

    def schedule(sequence: Iterable[Event]): Builder = {
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

    def build: ConferenceTrack = buildWithTitle("Track")

    def buildWithTitle(title: String): ConferenceTrack =
      new ConferenceTrack(title, schedulings.toSet)

    private def scheduleFirst(event: Event): Unit =
      schedulings.append(Scheduling(event, startTime))

    private def scheduleLatest(event: Event): Unit =
      schedulings.append(Scheduling(event, schedulings.last.endTime))

    private def requireNonScheduled(event: Event): Unit =
      require(!schedulings.exists(_.event == event), "Event can't be scheduled twice.")
  }
}
