package agp.scheduling

import agp.composition._
import agp.vo.{AfternoonSession, MorningSession, Talk}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen {

  /* shorter alias for tested type */
  private type Scheduling = ConferenceTracksSchedulingImpl

  /* scheduling that always returns some results */
  private val validScheduling = new Scheduling(
    newSuccessfulMorningSessionsComposition,
    newSuccessfulAfternoonSessionsComposition
  )


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

  def newSuccessfulMorningSessionsComposition: MorningSessionsComposition =
    _ => new MorningSessionsCompositionResult(sessions = 2 morningSessions, unusedTalks = 5 hourTalks)

  def newSuccessfulAfternoonSessionsComposition: AfternoonSessionsComposition =
    _ => new AfternoonSessionsCompositionResult(sessions = 2 afternoonSessions, unusedTalks = Set())


  private val someTalks: Set[Talk] = 2 hourTalks

  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {

    def hourTalks: Set[Talk] = (1 to requiredCount)
      .map(i => Talk(s"Title $i", 60))
      .toSet

    def morningSessions: Set[MorningSession] = (1 to requiredCount)
      .map(i => MorningSession(s"Morning Session $i", someTalks))
      .toSet

    def afternoonSessions: Set[AfternoonSession] = (1 to requiredCount)
      .map(i => AfternoonSession(s"Afternoon Session $i", someTalks))
      .toSet
  }
}
