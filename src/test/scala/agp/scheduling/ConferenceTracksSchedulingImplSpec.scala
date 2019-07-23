package agp.scheduling

import java.time.LocalTime

import agp.composition._
import agp.vo.{AfternoonSession, Lunch, MorningSession, NetworkingEvent, Talk}
import agp.{TestUtils, composition, scheduling}
import org.scalactic.{Bad, Good}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with TestUtils with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type TracksScheduling = ConferenceTracksSchedulingImpl


  "ConferenceTracksSchedulingImpl" should {

    "return detailed scheduling exception" when {

      "morning sessions composition returns exception" in {

        Given("morning sessions composition returning exception")
        val msComposition = (_: Set[Talk]) => Bad(composition.Exception("cause"))

        And("afternoon sessions composition returning some sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

        When("scheduling using them is applied to some talks")
        val result = newTracksScheduling(msComposition, asComposition)(someTalks)

        Then("result should be expected exception")
        assertFailedScheduling(result, cause = composition.Exception("cause"))
      }

      "afternoon sessions composition returns exception" in {

        Given("morning sessions composition returning some sessions")
        val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

        And("afternoon sessions composition returning exception")
        val asComposition = (_: Set[Talk]) => Bad(composition.Exception("cause"))

        When("scheduling using them is applied to some talks")
        val result = newTracksScheduling(msComposition, asComposition)(someTalks)

        Then("result should be expected exception")
        assertFailedScheduling(result, cause = composition.Exception("cause"))
      }

      def assertFailedScheduling(result: ExceptionOr[Set[ConferenceTrack]], cause: composition.Exception): Unit = {
        result shouldBe Bad(scheduling.Exception("Failed to schedule conference tracks.", cause))
      }
    }

    "schedule tracks based on composed number of morning & afternoon sessions pairs" in {

      List((0, 0), (0, 1), (1, 0), (10, 10)).foreach { case (i, j) =>

        Given(s"morning sessions composition returning $i sessions")
        val msComposition = newMorningSessionsCompositionReturning(i morningSessions)

        And(s"afternoon sessions composition returning $j sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(j afternoonSessions)

        When("scheduling using them is applied to some talks")
        val result = newTracksScheduling(msComposition, asComposition)(someTalks)

        Then(s"number of tracks should be ${i min j}")
        inside(result) { case Good(tracks) => tracks.size shouldBe (i min j) }
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
      val result = newTracksScheduling(msComposition, asComposition)(someTalks)

      Then("tracks should have expected talks")
      inside(result) { case Good(tracks) =>
        val morningTalks = morningSessions.flatten
        val afternoonTalks = afternoonSessions.flatten
        talksOf(tracks) shouldBe (morningTalks ++ afternoonTalks)
      }
    }

    "schedule tracks with Lunch & NetworkingEvent" in {

      Given("morning sessions composition returning 2 sessions")
      val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val result = newTracksScheduling(msComposition, asComposition)(someTalks)

      Then("each track should contain scheduling of Lunch & NetworkingEvent")
      inside(result) { case Good(tracks) =>
        tracks foreach (eventsOf(_) should contain allOf(Lunch, NetworkingEvent))
      }
    }

    "nothing should be scheduled after NetworkingEvent" in {

      Given("morning sessions composition returning 3 sessions")
      val msComposition = newMorningSessionsCompositionReturning(3 morningSessions)

      And("afternoon sessions composition returning 3 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(3 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val result = newTracksScheduling(msComposition, asComposition)(someTalks)

      Then("each track should have scheduling of NetworkingEvent as the last one")
      inside(result) { case Good(tracks) =>
        tracks foreach (_.max.event shouldBe NetworkingEvent)
      }
    }

    "schedule talks of morning sessions before lunch" in {

      Given("morning sessions composition returning 2 sessions")
      val morningSessions = 2 morningSessions
      val msComposition = newMorningSessionsCompositionReturning(morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

      When("scheduling using them is applied to some talks")
      val result = newTracksScheduling(msComposition, asComposition)(someTalks)

      Then("morning session talks should be scheduled before lunch")
      inside(result map (_.toVector)) { case Good(tracks) =>
        morningSessions foreach (_.talks should {
          equal(tracks(0).eventsBeforeLunch) or
          equal(tracks(1).eventsBeforeLunch)
        })
      }
    }

    "schedule talks of afternoon sessions after lunch" in {

      Given("morning sessions composition returning 2 sessions")
      val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

      And("afternoon sessions composition returning 2 sessions")
      val afternoonSessions = 2 afternoonSessions
      val asComposition = newAfternoonSessionsCompositionReturning(afternoonSessions)

      When("scheduling using them is applied to some talks")
      val result = newTracksScheduling(msComposition, asComposition)(someTalks)

      Then("afternoon session talks should be scheduled after lunch")
      inside(result map (_.toVector)) { case Good(tracks) =>
        afternoonSessions foreach (_.talks should {
          equal(tracks(0).talksAfterLunch) or
          equal(tracks(1).talksAfterLunch)
        })
      }
    }
  }

  /* utils */

  def newTracksScheduling = new TracksScheduling(LocalTime.of(9, 0))(_, _)

  def newMorningSessionsCompositionReturning(sessions: Set[MorningSession]): MorningSessionsComposition =
    _ => Good(new MorningSessionsCompositionResult(sessions, unusedTalks = someTalks))

  def newAfternoonSessionsCompositionReturning(sessions: Set[AfternoonSession]): AfternoonSessionsComposition =
    _ => Good(new AfternoonSessionsCompositionResult(sessions, unusedTalks = Set.empty))
}
