package agp.scheduler

import agp.vo.{Talk, TalksCombinations}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.concurrent.duration._

class MorningSessionSchedulerImplSpec extends WordSpec with Matchers with GivenWhenThen {

  "MorningSessionSchedulerImpl" should {

    "throw" when {

      "empty set of talks is given" in {
        an[IllegalArgumentException] should be thrownBy
        MorningSessionSchedulerImpl.using(goal = 2 hours)(Set())
      }

      "it's not possible to create MorningSession with goal duration" in {

        Given("valid scheduling")
        val scheduling = MorningSessionSchedulerImpl
          .using(knapsackSolution = _ => Set.empty)

        And("several talks")
        val talks = Set(Talk("#1", 30), Talk("#2", 30))

        Then("exception should be thrown when scheduling is applied")
        an[SchedulerException] should be thrownBy scheduling(talks)
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
      val result = MorningSessionSchedulerImpl.using(knapsackSolution)(talks)

      Then("result session should consist of first combination")
      result.session should contain only Talk("#2", 30)
    }

    "return Talks unused for creation of MorningSession" in {

      Given("several talks")
      val talks = Set(Talk("#1", 30), Talk("#2", 30))

      And("knapsack solution returning first talk as suitable combination")
      val knapsackSolution = newKnapsackSolutionReturning(Set(Talk("#1", 30)))

      When("scheduling using this knapsack solution is applied")
      val result = MorningSessionSchedulerImpl.using(knapsackSolution)(talks)

      Then("unused talks in result should contain only second talk")
      result.unusedTalks should contain only Talk("#2", 30)
    }
  }

  /* util */

  def newKnapsackSolutionReturning(combinations: Set[Talk]*)
  : Set[Talk] => TalksCombinations = _ => combinations.toSet

}
