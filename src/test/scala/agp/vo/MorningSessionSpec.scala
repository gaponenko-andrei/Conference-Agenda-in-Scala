package agp.vo

import agp.vo.event.Talk
import agp.vo.session.MorningSession
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.concurrent.duration._

class MorningSessionSpec extends WordSpec with Matchers with GivenWhenThen {

  "MorningSession should have expected duration " when {

    "events have same title, but different duration" in {

      Given("two events with same title & different duration")
      val events = Set(Talk("Title", 30), Talk("Title", 40))

      When("morning session is created from them")
      val compoundEvent = MorningSession(events)

      Then("its duration should be expected")
      compoundEvent.duration shouldBe (70 minutes)
    }

    "events have same title & same duration" in {

      Given("two events with same title & duration")
      val events = Set(Talk("Title", 30), Talk("Title", 30))

      When("morning session is created from them")
      val compoundEvent = MorningSession(events)

      Then("its duration should be expected")
      compoundEvent.duration shouldBe (60 minutes)
    }

    "events have different title & duration" in {

      Given("two events with different title & duration")
      val events = Set(Talk("#1", 30), Talk("#2", 40))

      When("morning session is created from them")
      val compoundEvent = MorningSession(events)

      Then("its duration should be expected")
      compoundEvent.duration shouldBe (70 minutes)
    }

    "events have different title & same duration" in {

      Given("two events with different title & same duration")
      val events = Set(Talk("#1", 30), Talk("#2", 30))

      When("morning session is created from them")
      val compoundEvent = MorningSession(events)

      Then("its duration should be expected")
      compoundEvent.duration shouldBe (60 minutes)
    }
  }

}
