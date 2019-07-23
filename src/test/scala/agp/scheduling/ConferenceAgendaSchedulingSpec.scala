package agp.scheduling

import java.time.LocalTime

import agp.TestUtils
import agp.vo.Talk
import org.scalactic.{Bad, Good}
import org.scalatest.{GivenWhenThen, WordSpec}

import scala.concurrent.duration._

class ConferenceAgendaSchedulingSpec extends WordSpec with TestUtils with GivenWhenThen {

  val scheduling = new ConferenceAgendaScheduling

  "ConferenceAgendaScheduling" should {

    "return expected exception" when {

      "overall duration of talks < 185 min" in {
        0 to 2 foreach { n =>

          Given(s"$n talks, each hour-long")
          val talks = n talksWithDuration 60

          When("scheduling is applied")
          val result = scheduling(talks)

          Then("result should be expected exception")
          assertFailedScheduling(result,
            s"Overall duration of talks must be >= ${185 minutes} to " +
            "schedule at least one track of morning & afternoon sessions.")
        }
      }

      "it's impossible to create agenda for given talks" in {

        Given("talks that are impossible to schedule right")
        val talks = Set(
          Talk("#1", 51), Talk("#2", 52), Talk("#3", 53),
          Talk("#4", 54), Talk("#5", 55), Talk("#6", 56))

        When("scheduling is applied")
        val result = scheduling(talks)

        Then("result should be expected exception")
        assertFailedScheduling(result, "Failed to schedule conference tracks.")
      }

      def assertFailedScheduling(result: ExceptionOr[Set[ConferenceTrack]], message: String): Unit = {
        // exception may have a cause as well, but here we test only message
        inside(result) { case Bad(ex) => ex.message shouldBe message }
      }
    }

    "determine number of tracks based on overall duration of talks" in {
      List((4 -> 1), (7 -> 1), (8 -> 2), (15 -> 3)).foreach { case (i, j) =>

        Given(s"$i one hours talks")
        val talks = i talksWithDuration 60

        When("scheduling is applied")
        val result = scheduling(talks)

        Then(s"result should be $j conference tracks")
        inside(result) { case Good(tracks) => tracks.size shouldBe j }
      }
    }

    "leave no talks unused" in {

      Given("talks with different durations")
      val talks = Set(
        Talk("#1", 29), Talk("#2", 31),
        Talk("#3", 29), Talk("#4", 31),
        Talk("#5", 9), Talk("#6", 51),
        Talk("#7", 9), Talk("#8", 51)
      )

      When("scheduling is applied")
      val result = scheduling(talks)

      Then("result should indicate that all talks were used")
      inside(result) { case Good(tracks) => talksOf(tracks) shouldBe talks }
    }

    "schedule Lunch for 12:00 PM - 1:00 PM" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val result = scheduling(talks)

      Then("each track should have Lunch scheduled for 12:PM - 1:00PM")
      inside(result) {
        case Good(tracks) => tracks foreach { it =>
          it.lunchScheduling.startTime shouldBe LocalTime.of(12, 0)
          it.lunchScheduling.endTime shouldBe LocalTime.of(13, 0)
        }
      }
    }

    "schedule NetworkingEvent for 5:00 PM or earlier" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val result = scheduling(talks)

      Then("each track should have NetworkingEvent scheduled for 5:00 PM or earlier")
      inside(result) {
        case Good(tracks) => tracks foreach { it =>
          it.networkingEventScheduling.startTime should be <= LocalTime.of(17, 0)
        }
      }
    }

    "schedule at least 3 talks for morning session " +
    "and at least one talk for afternoon session" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val result = scheduling(talks)

      inside(result) { case Good(tracks) =>

        Then("each track should have at least 3 talks for morning session")
        tracks foreach (_.eventsBeforeLunch.size should be >= 3)

        And("each track should have at least 1 talk for afternoon session")
        tracks foreach (it => it.talksAfterLunch.size should be >= 1)
      }
    }
  }
}
