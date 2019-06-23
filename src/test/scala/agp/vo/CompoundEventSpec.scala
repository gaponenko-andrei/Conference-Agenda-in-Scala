package agp.vo

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.concurrent.duration._

class CompoundEventSpec extends WordSpec with Matchers with GivenWhenThen {

  "CompoundSetEvent" should {

    "throw when empty set of events is used" in {
      an[IllegalArgumentException] should be thrownBy CompoundSetEvent("_", Set())
    }

    "have expected duration" when {

      "events have same title, but different duration" in {

        Given("two events with same title & different duration")
        val events = Set(Talk("Title", 30), Talk("Title", 40))

        When("compound set event is created")
        val compoundEvent = CompoundSetEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (70 minutes)
      }

      "events have same title & same duration" in {

        Given("two events with same title & duration")
        val events = Set(Talk("Title", 30), Talk("Title", 30))

        When("compound set event is created")
        val compoundEvent = CompoundSetEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (30 minutes)
      }

      "events have different title & duration" in {

        Given("two events with different title & duration")
        val events = Set(Talk("#1", 30), Talk("#2", 40))

        When("compound set event is created")
        val compoundEvent = CompoundSetEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (70 minutes)
      }

      "events have different title & same duration" in {

        Given("two events with different title & same duration")
        val events = Set(Talk("#1", 30), Talk("#2", 30))

        When("compound set event is created")
        val compoundEvent = CompoundSetEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (60 minutes)
      }
    }
  }

  "CompoundListEvent" should {

    "throw when empty list of events is used" in {
      an[IllegalArgumentException] should be thrownBy CompoundListEvent("_", List())
    }

    "have expected duration" when {

      "events have same title, but different duration" in {

        Given("two events with same title & different duration")
        val events = List(Talk("Title", 30), Talk("Title", 40))

        When("compound list event is created")
        val compoundEvent = CompoundListEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (70 minutes)
      }

      "events have same title & same duration" in {

        Given("two events with same title & duration")
        val events = List(Talk("Title", 30), Talk("Title", 30))

        When("compound list event is created")
        val compoundEvent = CompoundListEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (60 minutes)
      }

      "events have different title & duration" in {

        Given("two events with different title & duration")
        val events = List(Talk("#1", 30), Talk("#2", 40))

        When("compound list event is created")
        val compoundEvent = CompoundListEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (70 minutes)
      }

      "events have different title & same duration" in {

        Given("two events with different title & same duration")
        val events = List(Talk("#1", 30), Talk("#2", 30))

        When("compound list event is created")
        val compoundEvent = CompoundListEvent("_", events)

        Then("its duration should be expected")
        compoundEvent.duration shouldBe (60 minutes)
      }
    }
  }

}
