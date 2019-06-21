package agp.weighting

import org.scalatest.{GivenWhenThen, WordSpec}

class KnapsackSolutionForIntsSpec extends WordSpec with GivenWhenThen {

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
        combinations.foreach(combination =>
          assertThrows[IllegalArgumentException] {

            When(s"solution is applied to $combination")
            new KnapsackSolutionForInts(combination)(10)
          })
      }

      "provided goal <= 0" in {

        Given("some valid ints combination")
        val combination = List(1, 2)

        And("goal <= 0")
        val goal = -1

        Then("exception should be thrown")
        assertThrows[IllegalArgumentException] {

          When("solution is applied")
          new KnapsackSolutionForInts(combination)(goal)
        }

      }
    }

    "return empty result" when {

      "every int in combination > goal" in {

        Given("combination of ints, each > goal")
        val combination = List(13, 13)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be empty")
        assert(combinations.isEmpty)
      }

      "every int in combination barely < goal" in {

        Given("combination of ints, each barely < goal")
        val combination = List(11, 11)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be empty")
        assert(combinations.isEmpty)
      }
    }

    "return one combination" when {

      "ints combination has one int == goal" in {

        Given("combination of one int == goal")
        val combination = List(12)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be one combination of int == goal")
        assert(combinations === once(12))
      }

      "ints combination sums to goal" in {

        Given("combination of several ints sums to goal")
        val combination = List(8, 2, 1, 1)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be one expected combination")
        assert(sorted(combinations) === once(8, 2, 1, 1))
      }
    }

    "return two combinations" when {

      "ints combination has two ints == goal" in {

        Given("combination of two ints == goal")
        val combination = List(12, 12, 9, 8)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be two expected combinations")
        assert(combinations === twice(12))
      }
    }

    "return many combinations" when {

      "goal can be achieved summing different ints" in {

        Given("combination that may produce many answers")
        val combination = List(1, 1, 2, 3, 3, 4, 8, 9)

        When("solution is applied")
        val combinations = new KnapsackSolutionForInts(combination)(12)

        Then("result should be expected")
        assert(sorted(combinations) ===
          (9, 3)          * 2 :::
          (9, 2, 1)       * 2 :::
          (8, 4)          * 1 :::
          (8, 3, 1)       * 4 :::
          (8, 2, 1, 1)    * 1 :::
          (4, 3, 3, 2)    * 1 :::
          (4, 3, 3, 1, 1) * 1
        )
      }
    }
  }

  /* utils */

  implicit final class Combination(product: Product) {
    def *(times: Int): List[List[Any]] = List.fill(times)(product.productIterator.toList)
  }

  private def once(ints: Int*): List[List[Int]] = List.fill(1)(ints.toList)

  private def twice(ints: Int*): List[List[Int]] = List.fill(2)(ints.toList)

  private def sorted(combinations: List[List[Int]]): List[List[Int]] = combinations.map(_.sortWith(_ > _))

}
