package agp.weighting

import agp.TestUtils
import agp.Utils.OnMetReq
import agp.weighting.KnapsackSolutionForInts.Combinations
import org.scalactic.{Good, Or}
import org.scalatest._

class KnapsackSolutionForIntsSpec extends WordSpec with GivenWhenThen with TestUtils {

  "KnapsackSolutionForInts" should {

    "return IllegalArgumentException" when {

      "provided goal <= 0" in {

        Given("some valid ints combination")
        val ints = List(1, 2)

        And("goal <= 0")
        val goal = -1

        When("solution is applied")
        val result = applySolution(ints, goal)

        Then("result should be expected exception")
        assertBrokenRequirement(result, "Positive goal is required.")
      }

      "no ints were given" in {

        When("solution is applied to empty ints")
        val result = applySolution(List.empty[Int], 10)

        Then("result should be expected exception")
        assertBrokenRequirement(result, "At least one weighable is required.")
      }

      "provided ints have values <= 0" in {

        Given("some ints combinations with values <= 0")
        val intsCombinations = List(List(0, 1), List(-1, 1))

        When("solution is applied to each ints combination")
        val results = intsCombinations map (applySolution(_, 10))

        Then("each result should be expected exception")
        results foreach (assertBrokenRequirement(_, "All weighables must be positive."))
      }
    }

    "return empty result" when {

      "every given int > goal" in {

        Given("ints, each > goal")
        val ints = List(13, 13)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be empty")
        combinations shouldBe Good(List())
      }

      "every given int barely < goal" in {

        Given("ints, each barely < goal")
        val ints = List(11, 11)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be empty")
        combinations shouldBe Good(List())
      }
    }

    "return one combination" when {

      "ints have one int == goal" in {

        Given("one int == goal")
        val ints = List(12)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be expected")
        combinations shouldBe Good(List(List(12)))
      }

      "ints combination sums to goal" in {

        Given("several ints summing to goal")
        val ints = List(8, 2, 1, 1)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be one expected combination")
        inside(combinations) { case Good(result) =>
          sorted(result) shouldBe List(List(8, 2, 1, 1))
        }
      }
    }

    "return two combinations when ints have 2 values == goal" in {

      Given("ints with 2 values == goal")
      val ints = List(12, 12, 9, 8)

      When("solution is applied")
      val combinations = applySolution(ints, 12)

      Then("result should be 2 expected combinations")
      combinations shouldBe Good(List(List(12), List(12)))
    }

    "return many combinations when goal can be achieved summing different ints" in {

      Given("ints that may produce many combinations")
      val ints = List(1, 1, 2, 3, 3, 4, 8, 9)

      When("solution is applied")
      val combinations = applySolution(ints, 12)

      Then("result should be expected")
      inside(combinations) { case Good(result) =>
        sorted(result) shouldBe {
          (9, 3) * 2 :::
          (9, 2, 1) * 2 :::
          (8, 4) * 1 :::
          (8, 3, 1) * 4 :::
          (8, 2, 1, 1) * 1 :::
          (4, 3, 3, 2) * 1 :::
          (4, 3, 3, 1, 1) * 1
        }
      }
    }
  }

  /* utils */

  def applySolution(ints: List[Int], goal: Int): OnMetReq[Combinations] =
    KnapsackSolutionForInts(goal)(ints)

  private def sorted(combinations: List[List[Int]]): List[List[Int]] =
    combinations.map(_.sortWith(_ > _))

  implicit final class Combination(product: Product) {
    def *(times: Int): List[List[Any]] = List.fill(times)(product.productIterator.toList)
  }
}
