package agp.scheduling

import java.time.LocalTime

import agp.TestUtils
import agp.vo.{NetworkingEvent, Talk}
import org.scalatest.{GivenWhenThen, WordSpec}

class ConferenceAgendaSchedulingSpec extends WordSpec with TestUtils with GivenWhenThen {

  "ConferenceAgendaScheduling" should {

    "throw" when {

      "no talks are provided" in {
        an[IllegalArgumentException] should be thrownBy ConferenceAgendaScheduling(Set.empty)
      }

      "overall duration of talks < 185 min" in {

        List(0, 1, 2).foreach(n => {

          Given("talks with overall duration < 185 min")
          val talks = n talksWithDuration 60

          Then("exception should be thrown when scheduling is applied")
          an[IllegalArgumentException] should be thrownBy ConferenceAgendaScheduling(talks)
        })
      }

      "it's impossible to create agenda for given talks" in {
        an[agp.scheduling.Exception] should be thrownBy
          ConferenceAgendaScheduling(Set(
            Talk("#1", 51), Talk("#2", 52), Talk("#3", 53),
            Talk("#4", 54), Talk("#5", 55), Talk("#6", 56)
          ))
      }
    }

    "determine number of tracks based on overall duration of talks" in {
      List((4 -> 1), (7 -> 1), (8 -> 2), (15 -> 3)).foreach { case (i, j) =>

        Given(s"$i one hours talks")
        val talks = i talksWithDuration 60

        When("scheduling is applied")
        val tracks = ConferenceAgendaScheduling(talks)

        Then(s"number of tracks should be $j")
        tracks.size shouldBe j
      }
    }

    "leave no talks unused" in {

      Given("talks")
      val talks = Set(
        Talk("#1", 29), Talk("#2", 31),
        Talk("#3", 29), Talk("#4", 31),
        Talk("#5", 9), Talk("#6", 51),
        Talk("#7", 9), Talk("#8", 51)
      )

      When("scheduling is applied")
      val tracks = ConferenceAgendaScheduling(talks)

      Then("no talks should be left unused")
      talksOf(tracks) shouldBe talks
    }

    "schedule Lunch for 12:00 PM - 1:00 PM" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val tracks = ConferenceAgendaScheduling(talks)

      Then("each track should have Lunch scheduled for 12:PM - 1:00PM")
      tracks.foreach { it =>
        it.lunchScheduling.startTime shouldBe LocalTime.of(12, 0)
        it.lunchScheduling.endTime shouldBe LocalTime.of(13, 0)
      }
    }

    "schedule NetworkingEvent for 5:00 PM or earlier" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val tracks = ConferenceAgendaScheduling(talks)

      Then("each track should have NetworkingEvent scheduled for 5:00 PM or earlier")
      tracks.foreach(_.networkingEventScheduling.startTime should be <= LocalTime.of(17, 0))
    }

    "schedule at least 3 talks for morning session " +
    "and at least one talk for afternoon session" in {

      Given("talks with duration enough for several tracks")
      val talks = 15 talksWithDuration 60

      When("scheduling is applied")
      val tracks = ConferenceAgendaScheduling(talks)

      Then("each track should have at least 3 talks for morning session")
      tracks.foreach(_.eventsBeforeLunch.size should be >= 3)

      And("each track should have at least 1 talk for afternoon session")
      tracks.foreach(it => (it.eventsAfterLunch - NetworkingEvent).size should be >= 1)
    }
  }
}
