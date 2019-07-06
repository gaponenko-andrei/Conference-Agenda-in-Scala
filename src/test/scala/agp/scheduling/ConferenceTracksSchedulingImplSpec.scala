package agp.scheduling

import java.util.UUID._

import agp.TestUtils._
import agp.composition._
import agp.vo.{AfternoonSession, MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type Scheduling = ConferenceTracksSchedulingImpl

  /* morning sessions composition always returning some result */
  lazy val newSuccessfulMorningSessionsComposition: MorningSessionsComposition =
    newMorningSessionsCompositionReturning(2 morningSessions)

  /* afternoon sessions composition always returning some result */
  lazy val newSuccessfulAfternoonSessionsComposition: AfternoonSessionsComposition =
    newAfternoonSessionsCompositionReturning(2 afternoonSessions)

  /* scheduling that always returns some results */
  val validScheduling = new Scheduling(
    newSuccessfulMorningSessionsComposition,
    newSuccessfulAfternoonSessionsComposition)


  "ConferenceTracksSchedulingImpl" should {

    "throw" when {

      "no talks were provided" in {
        an[IllegalArgumentException] should be thrownBy validScheduling(Set.empty)
      }

      "overall duration of talks < 185 minutes" in {

        Given("talks with overall duration of 184 min")
        val talks = (2 hourTalks) + Talk("#3", 59) + Talk("#4", 5)

        Then("exception should be thrown when scheduling is applied")
        an[IllegalArgumentException] should be thrownBy validScheduling(talks)
      }

      "morning sessions composition throws exception" in {

        Given("morning sessions composition throwing exception")
        val msComposition = throwing(new RuntimeException("_"))

        And("successful afternoon sessions composition")
        val asComposition = newSuccessfulAfternoonSessionsComposition

        And("scheduling using them")
        val scheduling = new Scheduling(msComposition, asComposition)

        Then("exception should be thrown when scheduling is applied")
        an[agp.scheduling.Exception] should be thrownBy scheduling(5 hourTalks)
      }

      "afternoon sessions composition throws exception" in {

        Given("successful morning sessions composition")
        val msComposition = newSuccessfulMorningSessionsComposition

        And("afternoon sessions composition throwing exception")
        val asComposition = throwing(new RuntimeException("_"))

        And("scheduling using them")
        val scheduling = new Scheduling(msComposition, asComposition)

        Then("exception should be thrown when scheduling is applied")
        an[agp.scheduling.Exception] should be thrownBy scheduling(5 hourTalks)
      }
    }

    "not throw" when {

      "overall duration of talks >= 185 minutes" in {

        Given("talks with overall duration of 185 min")
        val talks = (3 hourTalks) + Talk("#4", 5)

        Then("no exception should be thrown when scheduling is applied")
        noException should be thrownBy validScheduling(talks)
      }
    }

    "schedule expected number of tracks" when {

      "talks duration is sufficient for one track" in {

        Given("morning sessions composition returning one session")
        val msComposition = newMorningSessionsCompositionReturning(1 morningSessions)

        And("afternoon sessions composition returning one session")
        val asComposition = newAfternoonSessionsCompositionReturning(1 afternoonSessions)

        And("scheduling using them")
        val scheduling = new Scheduling(msComposition, asComposition)

        When("scheduling is applied")
        val tracks = scheduling(4 hourTalks)

        Then("only one track should be scheduled")
        tracks.size shouldBe 1
      }

      // todo test "how much we need == how much we have"
    }
  }

  /* utils */

  /* morning sessions composition always returning specific result */
  def newMorningSessionsCompositionReturning(sessions: Set[MorningSession]): MorningSessionsComposition =
    returning(new MorningSessionsCompositionResult(sessions, unusedTalks = 5 hourTalks))

  /* afternoon sessions composition always returning specific result */
  def newAfternoonSessionsCompositionReturning(sessions: Set[AfternoonSession]): AfternoonSessionsComposition =
    returning(new AfternoonSessionsCompositionResult(sessions, unusedTalks = Set.empty))


  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {

    def hourTalks: Set[Talk] = (1 to requiredCount)
      .map(i => Talk(uniqueTitle, 60))
      .toSet

    def morningSessions: Set[MorningSession] = (1 to requiredCount)
      .map(i => MorningSession(uniqueTitle, 2 hourTalks))
      .toSet

    def afternoonSessions: Set[AfternoonSession] = (1 to requiredCount)
      .map(i => AfternoonSession(uniqueTitle, 2 hourTalks))
      .toSet

    private def uniqueTitle: String = randomUUID.toString
  }
}
