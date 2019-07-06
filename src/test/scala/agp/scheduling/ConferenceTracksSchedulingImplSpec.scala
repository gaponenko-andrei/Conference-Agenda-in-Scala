package agp.scheduling

import agp.TestUtils._
import agp.composition._
import agp.vo.{AfternoonSession, MorningSession, Talk}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen {

  /* shorter alias for tested type */
  type Scheduling = ConferenceTracksSchedulingImpl

  /* morning sessions composition always returning some result */
  lazy val newSuccessfulMorningSessionsComposition: MorningSessionsComposition = returning(
    new MorningSessionsCompositionResult(2 morningSessions, unusedTalks = 5 hourTalks))

  /* afternoon sessions composition always returning some result */
  lazy val newSuccessfulAfternoonSessionsComposition: AfternoonSessionsComposition = returning(
    new AfternoonSessionsCompositionResult(2 afternoonSessions, unusedTalks = Set.empty))

  /* scheduling that always returns some results */
  val validScheduling = new Scheduling(
    newSuccessfulMorningSessionsComposition,
    newSuccessfulAfternoonSessionsComposition)


  "ConferenceTracksSchedulingImpl" should {

    "throw" when {

      "no talks were provided" in {
        an[IllegalArgumentException] should be thrownBy validScheduling(Set())
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

  }

  /* utils */

  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {

    def hourTalks: Set[Talk] = (1 to requiredCount)
      .map(i => Talk(s"Title $i", 60))
      .toSet

    def morningSessions: Set[MorningSession] = (1 to requiredCount)
      .map(i => MorningSession(s"Morning Session $i", 2 hourTalks))
      .toSet

    def afternoonSessions: Set[AfternoonSession] = (1 to requiredCount)
      .map(i => AfternoonSession(s"Afternoon Session $i", 2 hourTalks))
      .toSet
  }
}
