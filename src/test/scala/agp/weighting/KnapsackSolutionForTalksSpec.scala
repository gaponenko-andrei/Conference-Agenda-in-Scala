package agp.weighting

import agp.vo.event.Talk
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.concurrent.duration._

class KnapsackSolutionForTalksSpec extends WordSpec with GivenWhenThen with Matchers {

  "Solution" should {

    "throw" when {

      "goal duration is <= 0" in {

        Given("valid combination of talks")
        val talks = Set(Talk("#1", 20), Talk("#2", 30))

        And("non positive goal durations")
        val nonPositiveGoals = List(0 minutes, -1 minute)

        Then("exception should be thrown")
        nonPositiveGoals.foreach(goal => an[IllegalArgumentException] should be thrownBy {

          When(s"solution is applied to goal $goal")
          applySolution(talks, goal)
        })
      }
    }

    "result in zero combinations" when {

      "given talk is longer then goal duration" in {

        Given("talk of 31 minutes")
        val talks = Set(Talk("Title", 31))

        And("goal of 30 minutes")
        val goal = 30 minutes

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("result should be empty")
        combinations shouldBe empty
      }

      "every talk is barely shorter then goal" in {

        Given("two talks of 29 minutes each")
        val talks = Set(Talk("#1", 29), Talk("#2", 29))

        And("goal of 30 minutes")
        val goal = 30 minutes

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("result should be empty")
        combinations shouldBe empty
      }
    }

    "result in one combination" when {

      "given talk has same duration as goal" in {

        Given("goal of 30 minutes")
        val goal = 30 minutes

        And("talk of 30 minutes")
        val talks = Set(Talk("Title", 30))

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("one combination should be returned")
        combinations shouldBe Set(talks)
      }

      "duration of several talks sums to goal" in {

        Given("goal of 60 minutes")
        val goal = 60 minutes

        And("several talks summing to goal")
        val talks = Set(
          Talk("#1", 15), Talk("#2", 15),
          Talk("#3", 30), Talk("#4", 55)
        )

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("one combination should be returned")
        combinations shouldBe Set(
          Set(Talk("#1", 15), Talk("#2", 15), Talk("#3", 30))
        )
      }
    }

    "result in two combinations" when {

      "two among all talks have goal duration" in {

        Given("goal of 30 minutes")
        val goal = 30 minutes

        And("several talks, two of them 30 minutes long")
        val talks = Set(Talk("#1", 30), Talk("#2", 30), Talk("#3", 29))

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("two combinations should be returned")
        combinations shouldBe Set(
          Set(Talk("#1", 30)),
          Set(Talk("#2", 30))
        )
      }
    }

    "result in expected combinations" when {

      "given tasks can be combined different ways" in {

        Given("goal of 50 minutes")
        val goal = 50 minutes

        And("combination of talks that must result in many answers")
        val talks = Set(
          Talk("#1", 10), Talk("#2", 10), Talk("#3", 20),
          Talk("#4", 30), Talk("#5", 50), Talk("#6", 60)
        )

        When("solution is applied")
        val combinations = applySolution(talks, goal)

        Then("combinations should be expected")
        combinations shouldBe Set(
          Set(Talk("#5", 50)),
          Set(Talk("#4", 30), Talk("#3", 20)),
          Set(Talk("#4", 30), Talk("#1", 10), Talk("#2", 10))
        )
      }
    }
  }

  /* utils */

  def applySolution(talks: Set[Talk], goal: Duration) = new KnapsackSolutionForTalks(talks)(goal)
}
