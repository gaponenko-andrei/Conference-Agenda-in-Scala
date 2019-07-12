package agp.composition

import agp.TestUtils
import org.scalatest.{GivenWhenThen, WordSpec}

class AfternoonSessionsCompositionImplSpec extends WordSpec with TestUtils with GivenWhenThen {

  /* shorter alias for tested type */
  type SessionsComposition = AfternoonSessionsCompositionImpl


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

    "result in sessions with all given talks" in {

      Given("afternoon session composition")
      val composition = new SessionsComposition(requiredSessionsNumber = 4)

      And("talks to compose into sessions")
      val talks = 10 talks

      When("composition is applied")
      val result = composition(talks)

      Then("sessions should have all talks")
      result.sessions.flatten shouldBe talks

      And("each session should have at least 2 talks")
      result.sessions.foreach(_.size should be >= 2)

      And("unused talks should be empty")
      result.unusedTalks shouldBe empty
    }
  }
}
