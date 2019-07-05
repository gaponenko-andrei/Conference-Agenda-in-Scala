package agp.scheduling

import java.time.LocalTime

import agp.vo.{Lunch, NetworkingEvent, Talk}
import org.scalatest.WordSpec

class SchedulingSpec extends WordSpec {

  "Sheduling" should {

    "be equal to other scheduling when they have same event & start time" in {
      Scheduling(Talk("Title", 30), LocalTime.of(10, 0)) ===
      Scheduling(Talk("Title", 30), LocalTime.of(10, 0))
    }

    "not be equal to other scheduling when they have same event & different start time" in {
      Scheduling(Talk("Title", 30), LocalTime.of(10, 0)) !==
      Scheduling(Talk("Title", 30), LocalTime.of(15, 0))
    }

    "not be equal to other scheudling when they have different event & same start time" in {
      Scheduling(Lunch, LocalTime.of(10, 0)) !==
      Scheduling(NetworkingEvent, LocalTime.of(10, 0))
    }
  }
}
