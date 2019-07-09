package agp.composition

import agp.TestUtils._
import agp.{TestUtils, composition}
import agp.vo.{MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.language.postfixOps


class MorningSessionsCompositionImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

  /* shorter alias for tested type */
  type SessionsComposition = MorningSessionsCompositionImpl

  /* test instance of MorningSession indicating we don't care about it's talks */
  val someSession: MorningSession = session("Morning Session")

  /* morning session composition always throwing exception */
  val throwingSessionComposition: MorningSessionComposition = _ => throw composition.Exception("_")

  /* morning session composition always returning some result */
  val successfulSessionComposition: MorningSessionComposition = _ => someSessionCompositionResult


  "MorningSessionsCompositionImpl" should {

    "throw" when {

      "given number of talks < required number of sessions" in {

        Given("morning session composition always returning result")
        val sessionComposition = successfulSessionComposition

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
        List(throwingSessionComposition,    // always throws CompositionException
             newUniqueSessionComposition(1) // returns result once, then throws
        ).foreach(sessionComposition => {

          And("morning sessions composition using it")
          val composition = new SessionsComposition(
            sessionComposition, requiredSessionsNumber = 2)

          Then("exception should be thrown when composition is applied")
          an[agp.composition.Exception] should be thrownBy composition(2 talks)
        })
      }
    }

    "not throw when given number of talks >= required number of sessions" in {

      Given("morning session composition always returning result")
      val sessionComposition = successfulSessionComposition

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
      val (session1, session2) = (session("#5"), session("#6"))
      val sessionComposition = newSessionCompositionReturning(
        sessionCompositionResult(session1, someTalks),
        sessionCompositionResult(session2, someTalks))

      And("morning sessions composition using them")
      val composition = new SessionsComposition(
        sessionComposition, requiredSessionsNumber = 2)

      When("composition is applied")
      val result = composition(5 talks)

      Then("result should have expected morning sessions")
      result.sessions shouldBe Set(session1, session2)
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

  /* utils */

  def session(title: String) = MorningSession(title, someTalks)

  def someSessionCompositionResult: MorningSessionCompositionResult =
    sessionCompositionResult(someSession, someTalks)

  def sessionCompositionResult(session: MorningSession, unused: Set[Talk]) =
    new MorningSessionCompositionResult(session, unused)

  def newSessionCompositionReturning(results: MorningSessionCompositionResult*): MorningSessionComposition =
    setup(mock[MorningSessionComposition]) { it => results foreach (it.apply _ expects * returning _) }

  def newUniqueSessionComposition(times: Int): MorningSessionComposition = setup(mock[MorningSessionComposition]) {
    it => (1 to times).foreach(i => {
      it.apply _ expects * returning sessionCompositionResult(session("#" + i), someTalks)
    })
  }
}
