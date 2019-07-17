package agp.scheduling

import java.time.LocalTime

import agp.TestUtils
import agp.composition._
import agp.vo.{AfternoonSession, MorningSession, Talk}
import org.scalactic.{Bad, Good}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, WordSpec}

class ConferenceTracksSchedulingImpl2Spec extends WordSpec with TestUtils with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type TracksScheduling = ConferenceTracksSchedulingImpl2


  "ConferenceTracksSchedulingImpl" should {

    "return detailed scheduling exception" when {

      "morning sessions composition returns exception" in {

        Given("morning sessions composition returning exception")
        val msException = new IllegalArgumentException
        val msComposition = (_: Set[Talk]) => Bad(msException)

        And("afternoon sessions composition returning some sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

        And("scheduling using them")
        val scheduling = newTracksScheduling(msComposition, asComposition)

        When("scheduling is applied")
        val result = scheduling(someTalks)

        Then("result should be expected exception")
        inside(result) {
          case Bad(ex: agp.scheduling.Exception) =>
            ex.cause shouldBe msException
            ex.message shouldBe "Failed to schedule conference tracks."
        }
      }

      "afternoon sessions composition returns exception" in {

        Given("morning sessions composition returning some sessions")
        val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

        And("afternoon sessions composition returning exception")
        val asException = new IllegalArgumentException
        val asComposition = (_: Set[Talk]) => Bad(asException)

        And("scheduling using them")
        val scheduling = newTracksScheduling(msComposition, asComposition)

        When("scheduling is applied")
        val result = scheduling(someTalks)

        Then("application result should be expected")
        inside(result) {
          case Bad(ex: agp.scheduling.Exception) =>
            ex.cause shouldBe asException
            ex.message shouldBe "Failed to schedule conference tracks."
        }
      }
    }

  }

  /* utils */

  def newTracksScheduling =
    new TracksScheduling(LocalTime.of(9, 0))(_, _)

  def newMorningSessionsCompositionReturning(sessions: Set[MorningSession]): MorningSessionsComposition2 =
    _ => Good(new MorningSessionsCompositionResult(sessions, unusedTalks = someTalks))

  def newAfternoonSessionsCompositionReturning(sessions: Set[AfternoonSession]): AfternoonSessionsComposition2 =
    _ => Good(new AfternoonSessionsCompositionResult(sessions, unusedTalks = Set.empty))
}
