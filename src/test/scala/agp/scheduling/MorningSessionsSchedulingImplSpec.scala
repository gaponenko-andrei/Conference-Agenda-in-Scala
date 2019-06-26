package agp.scheduling

import agp.vo.{MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.language.postfixOps


class MorningSessionsSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

   "MorningSessionsSchedulingImpl" should {

    "throw" when {

      "given number of talks < required number of sessions" in {

        Given("morning sessions scheduling with required number of sessions = 3")
        val scheduling = MorningSessionsSchedulingImpl(requiredSessionsNumber = 3)

        Then("exception should be thrown when scheduling is applied to 2 talks")
        an[IllegalArgumentException] should be thrownBy scheduling(2 talks)
      }

      "given morning session scheduling throws exception" in {

        Given("morning session scheduling throwing exception")
        List(newFailingSessionScheduling, // always throws SchedulingException
          newUniqueSessionScheduling(1)   // returns result once, then throws
        ).foreach(sessionScheduling => {

          And("morning sessions scheduling using it")
          val scheduling = new MorningSessionsSchedulingImpl(
            sessionScheduling, requiredSessionsNumber = 2)

          Then("exception should be thrown when scheduling is applied")
          an[SchedulingException] should be thrownBy scheduling(2 talks)
        })
      }
    }

    "not throw" when {

      "given number of talks >= required number of morning sessions" in {

        Given("morning session scheduling always returning result")
        val sessionScheduling = newSuccessSessionScheduling

        And("required number of sessions = 2")
        val requiredSessionsNumber = 2

        And("morning sessions scheduling using them")
        val scheduling = new MorningSessionsSchedulingImpl(
          sessionScheduling, requiredSessionsNumber)

        Then("no exception should be thrown")
        List(2 talks, 3 talks).foreach(talks => noException should be thrownBy {

          When(s"scheduling is applied to ${talks.size} talks")
          scheduling(talks)
        })
      }
    }

    "result in required number of sessions" when {

      "it's possible with given session scheduling" in {

        Given("morning session scheduling always returning result")
        val sessionScheduling = newUniqueSessionScheduling(times = 3)

        And("required number of sessions = 3")
        val requiredSessionsNumber = 3

        And("morning sessions scheduling using them")
        val scheduling = new MorningSessionsSchedulingImpl(
          sessionScheduling, requiredSessionsNumber)

        When("scheduling is applied to at least 3 talks")
        val result = scheduling(3 talks)

        Then(s"result should have 3 morning sessions")
        result.sessions.size shouldBe 3
      }
    }

     // todo test result has expected sessions
     // todo result has expected unused talks
  }

  /* to create MorningSessionScheduling mocks */

  def newFailingSessionScheduling: MorningSessionScheduling = setup(mock[MorningSessionScheduling]) {
    it => it.apply _ expects * throwing new SchedulingException("_") anyNumberOfTimes()
  }

  def newSuccessSessionScheduling: MorningSessionScheduling = setup(mock[MorningSessionScheduling]) {
    it => it.apply _ expects * returning someSessionSchedulingResult anyNumberOfTimes()
  }

  def newUniqueSessionScheduling(times: Int): MorningSessionScheduling = setup(mock[MorningSessionScheduling]) {
    it => (1 to times).foreach(i => {
        val morningSession: MorningSession = session(talksNumber = i)
        it.apply _ expects * returning sessionResult(morningSession, unusedTalks = i talks)
      })
  }

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  /* to create test MorningSessionSchedulingResult */

  def someSessionSchedulingResult: MorningSessionSchedulingResult =
    sessionResult(someSession, unusedTalks = 2 talks)

  def sessionResult(session: MorningSession, unusedTalks: Set[Talk]) =
    new MorningSessionSchedulingResult(session, unusedTalks)


  /* to create test MorningSession */

  def someSession: MorningSession = session(2)

  def session(talksNumber: Int) = MorningSession(talksNumber talks)


  /* to create test Talk */

  implicit class DummiesFactory(requiredCount: Int) {
    def talks: Set[Talk] = (1 to requiredCount).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }

}
