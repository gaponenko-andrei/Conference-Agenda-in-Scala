package agp.weighting

import agp.weighting.KnapsackSolutionForInts.Combinations
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class KnapsackSolutionForIntsSpec extends WordSpec with GivenWhenThen with Matchers {

  "Solution" should {

    "throw" when {

      "invalid ints are provided" in {

        Given("invalid combinations of ints")
        val ints = List(
          List(),
          List(0, 1), // int <= 0
          List(-1, 1) // int <= 0
        )

        Then("exception should be thrown")
        ints foreach (combination => an[IllegalArgumentException] should be thrownBy {

          When(s"solution is applied to $combination")
          applySolution(combination, 10)
        })
      }

      "provided goal <= 0" in {

        Given("some valid ints combination")
        val combination = List(1, 2)

        And("goal <= 0")
        val goal = -1

        Then("exception should be thrown")
        an[IllegalArgumentException] should be thrownBy {

          When("solution is applied")
          applySolution(combination, goal)
        }
      }
    }

    "return empty result" when {

      "every given int > goal" in {

        Given("ints, each > goal")
        val ints = List(13, 13)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be empty")
        combinations shouldBe empty
      }

      "every given int barely < goal" in {

        Given("ints, each barely < goal")
        val ints = List(11, 11)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be empty")
        combinations shouldBe empty
      }
    }

    "return one combination" when {

      "ints have one int == goal" in {

        Given("one int == goal")
        val ints = List(12)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be expected")
        combinations shouldBe List(List(12))
      }

      "ints combination sums to goal" in {

        Given("several ints summing to goal")
        val ints = List(8, 2, 1, 1)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be one expected combination")
        sorted(combinations) shouldBe List(List(8, 2, 1, 1))
      }
    }

    "return two combinations" when {

      "ints have 2 values == goal" in {

        Given("ints with 2 values == goal")
        val ints = List(12, 12, 9, 8)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be 2 expected combinations")
        combinations shouldBe List(List(12), List(12))
      }
    }

    "return many combinations" when {

      "goal can be achieved summing different ints" in {

        Given("ints that may produce many combinations")
        val ints = List(1, 1, 2, 3, 3, 4, 8, 9)

        When("solution is applied")
        val combinations = applySolution(ints, 12)

        Then("result should be expected")
        sorted(combinations) shouldBe {
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

  def applySolution(ints: List[Int], goal: Int): Combinations =
    KnapsackSolutionForInts(goal)(ints)

  private def sorted(combinations: List[List[Int]]): List[List[Int]] =
    combinations.map(_.sortWith(_ > _))

  implicit final class Combination(product: Product) {
    def *(times: Int): List[List[Any]] = List.fill(times)(product.productIterator.toList)
  }
}
