package agp.composition

import agp.vo.{Talk, TalksCombinations}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class MorningSessionCompositionImplSpec extends WordSpec with Matchers with GivenWhenThen {

  /* shorter alias for tested type */
  private type SessionComposition = MorningSessionCompositionImpl


  "MorningSessionCompositionImpl" should {

    "throw" when {

      "empty set of talks is given" in {

        Given("knapsack solution returning some combinations")
        val knapsackSolution = (_: Set[Talk]) => someCombinations

        And("morning session composition using it")
        val composition = new SessionComposition(knapsackSolution)

        Then("exception should be thrown when composition is applied to empty set")
        an[IllegalArgumentException] should be thrownBy composition(Set.empty)
      }

      "knapsack solution is not defined for given talks" in {

        Given("knapsack solution returning zero combinations")
        val knapsackSolution = (_: Set[Talk]) => zeroCombinations

        And("morning session composition using it")
        val composition = new SessionComposition(knapsackSolution)

        And("several talks")
        val talks = Set(Talk("#1", 30), Talk("#2", 30))

        Then("exception should be thrown when composition is applied")
        an[CompositionException] should be thrownBy composition(talks)
      }
    }

    "compose session using first suitable combination of talks" in {

      Given("several talks")
      val talks = Set(Talk("#1", 30), Talk("#2", 30))

      And("knapsack solution returning each talk as suitable combination")
      val knapsackSolution = (_: Set[Talk]) => Set(
        Set(Talk("#2", 30)), // first suitable combination
        Set(Talk("#1", 30)) // second suitable combination
      )

      When("composition using this knapsack solution is applied")
      val result = new SessionComposition(knapsackSolution)(talks)

      Then("result session should consist of first combination")
      result.session should contain only Talk("#2", 30)
    }

    "return talks unused for composition of session" in {

      Given("several talks")
      val talks = Set(Talk("#1", 30), Talk("#2", 30))

      And("knapsack solution returning first talk as suitable combination")
      val knapsackSolution = (_: Set[Talk]) => Set(Set(Talk("#1", 30)))

      When("composition using this knapsack solution is applied")
      val result = new SessionComposition(knapsackSolution)(talks)

      Then("unused talks in result should contain only second talk")
      result.unusedTalks should contain only Talk("#2", 30)
    }
  }

  /* to create test entities */

  def zeroCombinations: TalksCombinations = Set.empty[Set[Talk]]
  def someCombinations: TalksCombinations = Set(2 talks, 3 talks)

  // todo dry
  implicit class DummiesFactory(requiredCount: Int) {
    def talks: Set[Talk] = (1 to requiredCount).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }
}
