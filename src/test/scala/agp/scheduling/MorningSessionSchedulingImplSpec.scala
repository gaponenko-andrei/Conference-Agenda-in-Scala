package agp.scheduling

import agp.vo.{Talk, TalksCombination, TalksCombinations}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.concurrent.duration._

class MorningSessionSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen {

  "MorningSessionSchedulingImpl" should {

    "throw" when {

      "empty set of talks is given" in {
        an[IllegalArgumentException] should be thrownBy
          MorningSessionSchedulingImpl.using(goal = 2 hours)(Set())
      }

      "it's not possible to create MorningSession with goal duration" in {

        Given("valid scheduling")
        val scheduling = MorningSessionSchedulingImpl
          .using(knapsackSolution = _ => Set.empty)

        And("several talks")
        val talks = Set(Talk("#1", 30), Talk("#2", 30))

        Then("exception should be thrown when scheduling is applied")
        an[SchedulingException] should be thrownBy scheduling(talks)
      }
    }

    "create MorningSession based on first suitable combination" in {

      Given("several talks")
      val talks = Set(Talk("#1", 30), Talk("#2", 30))

      And("knapsack solution returning each talk as suitable combination")
      val knapsackSolution = newKnapsackSolutionReturning(
        Set(Talk("#2", 30)), // first suitable combination
        Set(Talk("#1", 30))  // second suitable combination
      )

      When("scheduling using this knapsack solution is applied")
      val result = MorningSessionSchedulingImpl.using(knapsackSolution)(talks)

      Then("result MorningSession should consist of first combination")
      result.event.toList should contain only Talk("#2", 30)
    }

    "return Talks unused for creation of MorningSession" in {

      Given("several talks")
      val talks = Set(Talk("#1", 30), Talk("#2", 30))

      And("knapsack solution returning first talk as suitable combination")
      val knapsackSolution = newKnapsackSolutionReturning(Set(Talk("#1", 30)))

      When("scheduling using this knapsack solution is applied")
      val result = MorningSessionSchedulingImpl.using(knapsackSolution)(talks)

      Then("unused talks in result should contain only second talk")
      result.unusedEvents.toList should contain only Talk("#2", 30)
    }
  }

  /* util */

  def newKnapsackSolutionReturning(combinations: TalksCombination*)
  : TalksCombination => TalksCombinations = _ => combinations.toSet

}
