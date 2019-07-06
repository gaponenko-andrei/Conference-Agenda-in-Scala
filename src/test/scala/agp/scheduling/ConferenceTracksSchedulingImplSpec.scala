package agp.scheduling

import java.util.UUID._

import agp.composition._
import agp.vo.{AfternoonSession, MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class ConferenceTracksSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type Scheduling = ConferenceTracksSchedulingImpl


  "ConferenceTracksSchedulingImpl" should {

    "throw" when {

      "morning sessions composition throws exception" in {

        Given("morning sessions composition throwing exception")
        val msComposition = (_: Set[Talk]) => throw new RuntimeException("_")

        And("afternoon sessions composition returning some sessions")
        val asComposition = newAfternoonSessionsCompositionReturning(2 afternoonSessions)

        And("scheduling using them")
        val scheduling = new Scheduling(msComposition, asComposition)

        Then("exception should be thrown when scheduling is applied")
        an[agp.scheduling.Exception] should be thrownBy scheduling(5 talks)
      }

      "afternoon sessions composition throws exception" in {

        Given("morning sessions composition returning some sessions")
        val msComposition = newMorningSessionsCompositionReturning(2 morningSessions)

        And("afternoon sessions composition throwing exception")
        val asComposition = (_: Set[Talk]) => throw new RuntimeException("_")

        And("scheduling using them")
        val scheduling = new Scheduling(msComposition, asComposition)

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

        When("scheduling using them is applied")
        val tracks = new Scheduling(msComposition, asComposition)(2 talks)

        Then(s"number of tracks should be ${i min j}")
        tracks.size shouldBe (i min j)
      }
    }

    // todo check tracks are expected
  }

  def newMorningSessionsCompositionReturning(sessions: Set[MorningSession]): MorningSessionsComposition =
    _ => new MorningSessionsCompositionResult(sessions, unusedTalks = 5 talks)

  def newAfternoonSessionsCompositionReturning(sessions: Set[AfternoonSession]): AfternoonSessionsComposition =
    _ => new AfternoonSessionsCompositionResult(sessions, unusedTalks = Set.empty)


  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {

    def talks: Set[Talk] = this fillSet Talk(uniqueTitle, 10)

    def morningSessions: Set[MorningSession] =
      this fillSet MorningSession(uniqueTitle, 2 talks)

    def afternoonSessions: Set[AfternoonSession] =
      this fillSet AfternoonSession(uniqueTitle, 2 talks)

    private def uniqueTitle: String = randomUUID.toString

    private def fillSet[T](value: => T): Set[T] = Iterable.fill(requiredCount)(value).toSet
  }
}
