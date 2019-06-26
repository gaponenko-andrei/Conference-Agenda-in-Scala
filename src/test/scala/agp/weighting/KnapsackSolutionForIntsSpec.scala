package agp.weighting

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class KnapsackSolutionForIntsSpec extends WordSpec with GivenWhenThen with Matchers {

  "Solution" should {

    "throw" when {

      "invalid ints are provided" in {

        Given("invalid combinations of ints")
        val combinations = List(
          List(),
          List(0, 1), // int <= 0
          List(-1, 1) // int <= 0
        )

        Then("exception should be thrown")
        combinations.foreach(combination => an[IllegalArgumentException] should be thrownBy {

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

      "every int in combination > goal" in {

        Given("combination of ints, each > goal")
        val combination = List(13, 13)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

        Then("result should be empty")
        combinations shouldBe empty
      }

      "every int in combination barely < goal" in {

        Given("combination of ints, each barely < goal")
        val combination = List(11, 11)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

        Then("result should be empty")
        combinations shouldBe empty
      }
    }

    "return one combination" when {

      "ints combination has one int == goal" in {

        Given("combination of one int == goal")
        val combination = List(12)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

        Then("result should be expected")
        combinations shouldBe List(List(12))
      }

      "ints combination sums to goal" in {

        Given("combination of several ints sums to goal")
        val combination = List(8, 2, 1, 1)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

        Then("result should be one expected combination")
        sorted(combinations) shouldBe List(List(8, 2, 1, 1))
      }
    }

    "return two combinations" when {

      "ints combination has two ints == goal" in {

        Given("combination of two ints == goal")
        val combination = List(12, 12, 9, 8)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

        Then("result should be two expected combinations")
        combinations shouldBe List(List(12), List(12))
      }
    }

    "return many combinations" when {

      "goal can be achieved summing different ints" in {

        Given("combination that may produce many answers")
        val combination = List(1, 1, 2, 3, 3, 4, 8, 9)

        When("solution is applied")
        val combinations = applySolution(combination, 12)

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

  def applySolution(ints: List[Int], goal: Int) = new KnapsackSolutionForInts(ints)(goal)

  private def sorted(combinations: List[List[Int]]): List[List[Int]] = combinations.map(_.sortWith(_ > _))

  implicit final class Combination(product: Product) {
    def *(times: Int): List[List[Any]] = List.fill(times)(product.productIterator.toList)
  }

}
