package agp.composition

import agp.TestUtils
import org.scalactic.Good
import org.scalatest.{GivenWhenThen, WordSpec}

class AfternoonSessionsCompositionImplSpec extends WordSpec with TestUtils with GivenWhenThen {

  /* shorter alias for tested type */
  type Composition = AfternoonSessionsCompositionImpl


  "AfternoonSessionsCompositionImpl" should {

    "return IllegalArgumentException when number of talks < required number of sessions" in {

      Given("required number of sessions = 3")
      val requiredSessionsNumber = 3

      And("afternoon session composition using it")
      val composition = new Composition(requiredSessionsNumber)

      for (talks <- Seq(0 talks, 1 talks, 2 talks)) {

        When(s"composition is applied to ${talks.size} talks")
        val result = composition(talks)

        Then("result should be expected exception")
        assertFailedComposition(result, s"Talks.size is ${talks.size}, but must be >= 3.")
      }
    }

    "result in required number of sessions" in {

      Given("required number of sessions = 3")
      val requiredSessionsNumber = 3

      And("afternoon session composition using it")
      val composition = new Composition(requiredSessionsNumber)

      for (talks <- Seq(3 talks, 7 talks, 10 talks)) {

        When(s"composition is applied to ${talks.size} talks")
        val result = composition(talks)

        Then("result should have expected number of sessions")
        inside(result) { case Good(x) => x.sessions.size shouldBe 3 }
      }
    }

    "result in sessions using all given talks" in {

      Given("afternoon sessions composition")
      val composition = new Composition(requiredSessionsNumber = 4)

      And("talks to compose into sessions")
      val talks = 10 talks

      When("composition is applied")
      val result = composition(talks)

      inside(result) { case Good(x) =>

        Then("sessions should have all talks")
        x.sessions.flatten shouldBe talks

        And("each session should have at least 2 talks")
        x.sessions.foreach(_.size should be >= 2)

        And("unused talks should be empty")
        x.unusedTalks shouldBe empty
      }
    }
  }
}
