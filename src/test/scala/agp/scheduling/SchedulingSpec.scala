package agp.scheduling

import java.time.LocalTime

import agp.vo.{Lunch, NetworkingEvent, Talk}
import org.scalatest.{Matchers, WordSpec}

class SchedulingSpec extends WordSpec with Matchers {

  "Scheduling" should {

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

    "be sorted using start time by default" in {
      List(
        Scheduling(Talk("#1", 30), LocalTime.of(11, 0)),
        Scheduling(Talk("#2", 30), LocalTime.of(10, 0)),
        Scheduling(Talk("#3", 30), LocalTime.of(12, 0))
      ).sorted shouldBe List(
        Scheduling(Talk("#2", 30), LocalTime.of(10, 0)),
        Scheduling(Talk("#1", 30), LocalTime.of(11, 0)),
        Scheduling(Talk("#3", 30), LocalTime.of(12, 0))
      )
    }
  }

  "Set[Scheduling]" should {

    "produce maximum element based on start time" in {
      Set(
        Scheduling(Talk("#1", 30), LocalTime.of(11, 0)),
        Scheduling(Talk("#2", 30), LocalTime.of(12, 0)),
        Scheduling(Talk("#3", 30), LocalTime.of(10, 0))
      ).max shouldBe Scheduling(Talk("#2", 30), LocalTime.of(12, 0))
    }

    "produce minimum element based on start time" in {
      Set(
        Scheduling(Talk("#1", 30), LocalTime.of(11, 0)),
        Scheduling(Talk("#2", 30), LocalTime.of(12, 0)),
        Scheduling(Talk("#3", 30), LocalTime.of(10, 0))
      ).min shouldBe Scheduling(Talk("#3", 30), LocalTime.of(10, 0))
    }
  }
}
