package agp.composition

import agp.TestUtils
import agp.vo.{MorningSession, Talk, TalksCombinations, _}
import org.scalactic.{Bad, Good}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, WordSpec}

import scala.language.postfixOps


class MorningSessionsCompositionImpl2Spec extends WordSpec
  with GivenWhenThen with MockFactory with TestUtils {

  /* shorter alias for tested type */
  type Composition = MorningSessionsCompositionImpl2

  /* test instance of MorningSession indicating we don't care about it's talks */
  val someSession: MorningSession = session("Morning Session")

  /* knapsack solution always returning some random talks combinations */
  private val successfulKnapsackSolution = (_: Set[Talk]) => Good(someCombinations)


  "MorningSessionsCompositionImpl2" should {

    "return IllegalArgumentException" when {

      "number of talks < required number of sessions" in {

        Given("required number of sessions is 3")
        val requiredSessionsNumber = 3

        And("knapsack solution returning some result")
        val knapsackSolution = successfulKnapsackSolution

        And("sessions composition using them")
        val composition = new Composition(requiredSessionsNumber, knapsackSolution)

        When("composition is applied")
        val result = composition(2 talks)

        Then("result should be expected exception")
        assertBrokenRequirement(result, "Talks.size is 2, but must be >= 3.")
      }

      "knapsack solution returns IllegalArgumentException" in {

        Given("required number of sessions is 3")
        val requiredSessionsNumber = 3

        And("knapsack solution returning IllegalArgumentException")
        val knapsackSolution = (_: Set[Talk]) => Bad(new IllegalArgumentException)

        And("sessions composition using them")
        val composition = new Composition(requiredSessionsNumber, knapsackSolution)

        When("composition is applied")
        val result = composition(3 talks)

        Then("result should be expected exception")
        assertBrokenRequirement(result,
          "Failed to compose morning session. Given talks " +
          "didn't meet requirements of knapsack solution.")
      }

      "knapsack solution returns zero talks combinations" in {

        Given("required number of sessions is 3")
        val requiredSessionsNumber = 3

        And("knapsack solution returning zero combinations")
        val knapsackSolution = (_: Set[Talk]) => Good(zeroCombinations)

        And("sessions composition using them")
        val composition = new Composition(requiredSessionsNumber, knapsackSolution)

        When("composition is applied")
        val result = composition(3 talks)

        Then("result should be expected exception")
        assertBrokenRequirement(result,
          "Failed to compose morning session. No suitable combinations " +
          "of talks were found for given knapsack solution & talks.")
      }
    }

    "result in required number of sessions" in {

      Given("required number of sessions is 3")
      val requiredSessionsNumber = 3

      And("knapsack solution returning some result")
      val knapsackSolution = successfulKnapsackSolution

      And("sessions composition using them")
      val composition = new Composition(requiredSessionsNumber, knapsackSolution)

      When("composition is applied")
      val result = composition(3 talks)

      Then("result should have 3 sessions")
      inside(result) { case Good(x) => x.sessions.size shouldBe 3 }
    }

    "use the first suitable combination returned by " +
    "knapsack solution to create morning session" in {

      Given("knapsack solution returning specific talks combinations")
      val combination1 = Set(2 talks, 3 talks)
      val combination2 = Set(3 talks, 2 talks)
      val knapsackSolution = newKnapsackSolutionReturning(combination1, combination2)

      And("sessions composition using it")
      val composition = new Composition(requiredSessionsNumber = 2, knapsackSolution)

      When("composition is applied")
      val result = composition(3 talks)

      Then("result should have expected sessions")
      inside(result) {
        case Good(x) => x.sessions shouldBe Set(
          MorningSession(combination1.head),
          MorningSession(combination2.head))
      }
    }

    "result in expected unused talks" in {

      Given("knapsack solution returning specific talks combinations")
      val combination1 = Set(2 talks, 3 talks)
      val combination2 = Set(3 talks, 2 talks)
      val knapsackSolution = newKnapsackSolutionReturning(combination1, combination2)

      And("sessions composition using it")
      val composition = new Composition(requiredSessionsNumber = 2, knapsackSolution)

      When("composition is applied to all talks")
      val talks = (combination1 ++ combination2).flatten
      val result = composition(talks)

      Then("result should have expected unused talks")
      inside(result) { case Good(x) =>
        val used = x.sessions.flatten
        val unused = talks except used
        x.unusedTalks shouldBe unused
      }
    }
  }

  /* utils */

  def zeroCombinations: TalksCombinations = Set.empty[Set[Talk]]

  def someCombinations: TalksCombinations = Set(2 talks, 3 talks)

  def session(title: String) = MorningSession(title, someTalks)

  def newKnapsackSolutionReturning(combinationsSeq: TalksCombinations*): KnapsackSolution =
    setup(mock[KnapsackSolution]) { it =>
      combinationsSeq foreach { combination =>
        it.apply _ expects * returning Good(combination)
      }
    }
}
