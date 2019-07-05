package agp.scheduling

import java.time.LocalTime

import agp.scheduling.ConferenceTrack.newBuilder
import agp.vo.Talk
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTrackSpec extends WordSpec with GivenWhenThen with Matchers {

  "ConferenceTrack" should {

    "be empty" in {
      newBuilder.build shouldBe empty
    }

    "throw when same event is scheduled twice" in {

      Given("several scheduled events")
      val builder = newBuilder schedule(Talk("#1", 30), Talk("#2", 15))

      Then("exception should be thrown")
      an[IllegalArgumentException] should be thrownBy {

        When("same event is scheduled twice")
        builder schedule Talk("#1", 30)
      }
    }

    "have expected scheduled events" in {

      Given("several events are scheduled")
      val builder = newBuilder schedule(Talk("#1", 30), Talk("#2", 15))

      When("track is built")
      val track = builder.build

      Then("it should have expected events")
      track should contain only(
        Scheduling(Talk("#1", 30), LocalTime.of(9, 0)),
        Scheduling(Talk("#2", 15), LocalTime.of(9, 30))
      )
    }
  }

}
