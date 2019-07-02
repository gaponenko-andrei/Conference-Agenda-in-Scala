package agp.composition

import agp.vo.{AfternoonSession, Talk}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class AfternoonSessionsCompositionImplSpec extends WordSpec with Matchers with GivenWhenThen {

  /* shorter alias for tested type */
  private type SessionsComposition = AfternoonSessionsCompositionImpl


  "AfternoonSessionsCompositionImpl" should {

    "throw when given number of talks < required number of sessions" in {

      Given("required number of sessions = 3")
      val requiredSessionsNumber = 3

      And("afternoon session composition using it")
      val composition = new SessionsComposition(requiredSessionsNumber)

      Then("exception should be thrown")
      List(1 talks, 2 talks).foreach(talks =>
        an[IllegalArgumentException] should be thrownBy {

          When(s"composition is applied to ${talks.size} talks")
          composition(talks)
        })
    }

    "not throw when given number of talks >= required number of sessions" in {

      Given("required number of sessions = 2")
      val requiredSessionsNumber = 2

      And("afternoon session composition using it")
      val composition = new SessionsComposition(requiredSessionsNumber)

      Then("no exception should be thrown")
      List(2 talks, 3 talks).foreach(talks => noException should be thrownBy {

        When(s"composition is applied to ${talks.size} talks")
        composition(talks)
      })
    }

    "result in required number of sessions" in {

      Given("required number of sessions = 3")
      val requiredSessionsNumber = 3

      And("afternoon session composition using it")
      val composition = new SessionsComposition(requiredSessionsNumber)

      Then("result should have 3 sessions")
      List(3 talks, 7 talks, 10 talks).foreach(talks => {

        When(s"composition is applied to ${talks.size} talks")
        composition(talks).sessions.size shouldBe 3
      })
    }

    "result sessions with all given talks" in {

      Given("afternoon session composition")
      val composition = new SessionsComposition(requiredSessionsNumber = 4)

      And("talks to compose into sessions")
      val talks = 10 talks

      When("composition is applied")
      val result = composition(talks)

      Then("result should sessions with all given talks")
      result.unusedTalks shouldBe empty
      result.sessions.flatten shouldBe talks
    }
  }

  /* to create test entities */

  def session(talks: Talk*) = AfternoonSession(talks: _*)

  implicit class DummiesFactory(requiredCount: Int) {
    def talks: Set[Talk] = (1 to requiredCount).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }
}
