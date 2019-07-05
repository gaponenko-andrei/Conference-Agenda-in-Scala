package agp.composition

import agp.vo.{MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.language.postfixOps


class MorningSessionsCompositionImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  private type SessionsComposition = MorningSessionsCompositionImpl


  "MorningSessionsCompositionImpl" should {

    "throw" when {

      "given number of talks < required number of sessions" in {

        Given("morning session composition always returning result")
        val sessionComposition = newSuccessSessionComposition

        And("required number of sessions = 3")
        val requiredSessionsNumber = 3

        And("morning sessions composition using them")
        val composition = new SessionsComposition(
          sessionComposition, requiredSessionsNumber)

        Then("exception should be thrown")
        List(1 talks, 2 talks).foreach(talks =>
          an[IllegalArgumentException] should be thrownBy {

            When(s"composition is applied to ${talks.size} talks")
            composition(talks)
          })
      }

      "given morning session composition throws exception" in {

        Given("morning session composition throwing exception")
        List(newFailingSessionComposition,  // always throws CompositionException
             newUniqueSessionComposition(1) // returns result once, then throws
        ).foreach(sessionComposition => {

          And("morning sessions composition using it")
          val composition = new SessionsComposition(
            sessionComposition, requiredSessionsNumber = 2)

          Then("exception should be thrown when composition is applied")
          an[CompositionException] should be thrownBy composition(2 talks)
        })
      }
    }

    "not throw when given number of talks >= required number of sessions" in {

      Given("morning session composition always returning result")
      val sessionComposition = newSuccessSessionComposition

      And("required number of sessions = 2")
      val requiredSessionsNumber = 2

      And("morning sessions composition using them")
      val composition = new SessionsComposition(
        sessionComposition, requiredSessionsNumber)

      Then("no exception should be thrown")
      List(2 talks, 3 talks).foreach(talks => noException should be thrownBy {

        When(s"composition is applied to ${talks.size} talks")
        composition(talks)
      })
    }

    "result in required number of sessions" in {

      Given("morning session composition returning unique results")
      val sessionComposition = newUniqueSessionComposition(times = 3)

      And("required number of sessions = 3")
      val requiredSessionsNumber = 3

      And("morning sessions composition using them")
      val composition = new SessionsComposition(
        sessionComposition, requiredSessionsNumber)

      When("composition is applied to at least 3 talks")
      val result = composition(3 talks)

      Then("result should have 3 sessions")
      result.sessions.size shouldBe 3
    }

    "result in expected sessions" in {

      Given("morning session composition returning unique results")
      val sessionComposition = newSessionCompositionReturning(
        sessionCompositionResult(session("#5"), someTalks),
        sessionCompositionResult(session("#6"), someTalks))

      And("morning sessions composition using them")
      val composition = new SessionsComposition(
        sessionComposition, requiredSessionsNumber = 2)

      When("composition is applied")
      val result = composition(5 talks)

      Then("result should have expected morning sessions")
      result.sessions shouldBe Set(session("#5"), session("#6"))
    }

    "result in expected unused talks" in {

      Given("several unique identifiable talks")
      val uniqueTalks = Set(Talk("#8", 10), Talk("#9", 20))

      And("session composition returning them as unused in last result")
      val sessionComposition = newSessionCompositionReturning(
        someSessionCompositionResult,
        sessionCompositionResult(someSession, uniqueTalks))

      And("morning sessions composition using them")
      val composition = new SessionsComposition(
        sessionComposition, requiredSessionsNumber = 2)

      When("composition is applied")
      val result = composition(5 talks)

      Then("result should have expected unused talks")
      result.unusedTalks shouldBe uniqueTalks
    }
  }

  /* to create MorningSessionComposition mocks */

  def newFailingSessionComposition: MorningSessionComposition = setup(mock[MorningSessionComposition]) {
    it => it.apply _ expects * throwing CompositionException("_") anyNumberOfTimes()
  }

  def newSuccessSessionComposition: MorningSessionComposition = setup(mock[MorningSessionComposition]) {
    it => it.apply _ expects * returning someSessionCompositionResult anyNumberOfTimes()
  }

  def newUniqueSessionComposition(times: Int): MorningSessionComposition = setup(mock[MorningSessionComposition]) {
    it => (1 to times).foreach(i => {
        it.apply _ expects * returning sessionCompositionResult(session("#" + i), someTalks)
      })
  }

  def newSessionCompositionReturning(results: MorningSessionCompositionResult*): MorningSessionComposition =
    setup(mock[MorningSessionComposition]) { it => results foreach (it.apply _ expects * returning _) }

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  /* to create test MorningSessionCompositionResult */

  def someSessionCompositionResult: MorningSessionCompositionResult =
    sessionCompositionResult(someSession, someTalks)

  def sessionCompositionResult(session: MorningSession, unused: Set[Talk]) =
    new MorningSessionCompositionResult(session, unused)


  /* to create test MorningSession */

  def someSession: MorningSession = session("Morning Session")
  def session(title: String) = MorningSession(title, someTalks)


  /* to create test Talk */

  def someTalks: Set[Talk] = 2 talks

  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {
    def talks: Set[Talk] = (1 to requiredCount).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }

}
