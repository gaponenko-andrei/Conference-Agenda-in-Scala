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
             newUniqueSessionScheduling(1) // returns result once, then throws
        ).foreach(sessionScheduling => {

          And("morning sessions scheduling using it")
          val scheduling = new MorningSessionsSchedulingImpl(
            sessionScheduling, requiredSessionsNumber = 2)

          Then("exception should be thrown when scheduling is applied")
          an[SchedulingException] should be thrownBy scheduling(2 talks)
        })
      }
    }

    "not throw when given number of talks >= required number of sessions" in {

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

    "result in required number of sessions" in {

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

    "result in expected sessions" in {

      Given("morning session scheduling returning unique results")
      val sessionScheduling = newSessionSchedulingReturning(
        sessionSchedulingResult(session("#5"), someTalks),
        sessionSchedulingResult(session("#6"), someTalks))

      And("morning sessions scheduling using them")
      val scheduling = new MorningSessionsSchedulingImpl(
        sessionScheduling, requiredSessionsNumber = 2)

      When("scheduling is applied")
      val result = scheduling(5 talks)

      Then("result should have expected morning sessions")
      result.sessions shouldBe Set(session("#5"), session("#6"))
    }

    "result in expected unused talks" in {

      Given("several unique identifiable talks")
      val uniqueTalks = Set(Talk("#8", 10), Talk("#9", 20))

      And("session scheduling returning them as unused in last result")
      val sessionScheduling = newSessionSchedulingReturning(
        someSessionSchedulingResult,
        sessionSchedulingResult(someSession, uniqueTalks))

      And("morning sessions scheduling using them")
      val scheduling = new MorningSessionsSchedulingImpl(
        sessionScheduling, requiredSessionsNumber = 2)

      When("scheduling is applied")
      val result = scheduling(5 talks)

      Then("result should have expected unused talks")
      result.unusedTalks shouldBe uniqueTalks
    }
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
      it.apply _ expects * returning sessionSchedulingResult(session("#" + i), someTalks)
    })
  }

  def newSessionSchedulingReturning(results: MorningSessionSchedulingResult*): MorningSessionScheduling =
    setup(mock[MorningSessionScheduling]) { it => results foreach (it.apply _ expects * returning _ ) }

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  /* to create test MorningSessionSchedulingResult */

  def someSessionSchedulingResult: MorningSessionSchedulingResult =
    sessionSchedulingResult(someSession, someTalks)

  def sessionSchedulingResult(session: MorningSession, unused: Set[Talk]) =
    new MorningSessionSchedulingResult(session, unused)


  /* to create test MorningSession */

  def someSession: MorningSession = session("Morning Session")

  def session(title: String) = MorningSession(title, someTalks)


  /* to create test Talk */

  def someTalks: Set[Talk] = 2 talks

  implicit class DummiesFactory(requiredCount: Int) {
    def talks: Set[Talk] = (1 to requiredCount).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }

}
