package agp.scheduling

import agp.TestUtils._
import agp.composition._
import agp.vo.{AfternoonSession, Lunch, MorningSession, NetworkingEvent, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type TracksScheduling = ConferenceTracksSchedulingImpl


  "ConferenceTracksSchedulingImpl" should {

    "throw" when {

      "morning sessions composition throws exception" in {

        Given("morning sessions composition throwing exception")
        val msComposition = (_: Set[Talk]) => throw new RuntimeException("_")

        And("afternoon sessions composition returning some sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

        And("scheduling using them")
        val scheduling = new TracksScheduling(msComposition, asComposition)

        Then("exception should be thrown when scheduling is applied")
        an[agp.scheduling.Exception] should be thrownBy scheduling(5 talks)
      }

      "afternoon sessions composition throws exception" in {

        Given("morning sessions composition returning some sessions")
        val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

        And("afternoon sessions composition throwing exception")
        val asComposition = (_: Set[Talk]) => throw new RuntimeException("_")

        And("scheduling using them")
        val scheduling = new TracksScheduling(msComposition, asComposition)

        Then("exception should be thrown when scheduling is applied")
        an[agp.scheduling.Exception] should be thrownBy scheduling(5 talks)
      }
    }

    "schedule tracks based on composed number of morning & afternoon sessions pairs" in {

      List((0, 0), (0, 1), (1, 0), (10, 10)).foreach { case (i, j) =>

        Given(s"morning sessions composition returning $i sessions")
        val msComposition = newMorningSessionsCompositionReturning(i morningSessions)

        And(s"afternoon sessions composition returning $j sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(j afternoonSessions)

        When("scheduling using them is applied to some talks")
        val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

        Then(s"number of tracks should be ${i min j}")
        tracks.size shouldBe (i min j)
      }
    }

    "schedule tracks with expected talks" in {

      Given("morning sessions composition returning 2 sessions")
      val morningSessions = 2 morningSessions
      val msComposition = newMorningSessionsCompositionReturning(morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val afternoonSessions = 2 afternoonSessions
      val asComposition = newAfternoonSessionsCompositionReturning(afternoonSessions)

      When("scheduling using them is applied to some talks")
      val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

      Then("tracks should have expected talks")
      val morningTalks = morningSessions.flatten
      val afternoonTalks = afternoonSessions.flatten
      talksOf(tracks) shouldBe (morningTalks ++ afternoonTalks)
    }

    "schedule tracks with Lunch & NetworkingEvent" in {

      Given("morning sessions composition returning 2 sessions")
      val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

      Then("each track should contain scheduling of Lunch & NetworkingEvent")
      tracks.foreach(eventsOf(_) should contain allOf(Lunch, NetworkingEvent))
    }

    "nothing should be scheduled after NetworkingEvent" in {

      Given("morning sessions composition returning 3 sessions")
      val msComposition = newMorningSessionsCompositionReturning(3 morningSessions)

      And("afternoon sessions composition returning 3 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(3 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

      Then("each track should have scheduling of NetworkingEvent as the last one")
      tracks.foreach(_.max.event shouldBe NetworkingEvent)
    }

    "schedule talks of morning sessions before lunch" in {

      Given("morning sessions composition returning 2 sessions")
      val morningSessions = 2 morningSessions
      val msComposition = newMorningSessionsCompositionReturning(morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

      Then("morning session talks should be scheduled before lunch")
      val sessions = morningSessions.toVector
      for (track <- tracks) {
        track.eventsBeforeLunch should {
          equal(sessions(0).talks) or equal(sessions(1).talks)
        }
      }
    }

    "schedule talks talks of afternoon sessions after lunch" in {

      Given("morning sessions composition returning 2 sessions")
      val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val afternoonSessions = 2 afternoonSessions
      val asComposition = newAfternoonSessionsCompositionReturning(afternoonSessions)

      When("scheduling using them is applied to some talks")
      val tracks = new TracksScheduling(msComposition, asComposition)(someTalks)

      Then("afternoon session talks should be scheduled after lunch")
      val sessions = afternoonSessions.toVector
      for (track <- tracks) {
        track.eventsAfterLunch - NetworkingEvent should {
          equal(sessions(0).talks) or equal(sessions(1).talks)
        }
      }
    }
  }

  /* utils */

  def newMorningSessionsCompositionReturning(sessions: Set[MorningSession]): MorningSessionsComposition =
    _ => new MorningSessionsCompositionResult(sessions, unusedTalks = someTalks)

  def newAfternoonSessionsCompositionReturning(sessions: Set[AfternoonSession]): AfternoonSessionsComposition =
    _ => new AfternoonSessionsCompositionResult(sessions, unusedTalks = Set.empty)
}
