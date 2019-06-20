package agp.weighting

import org.scalatest.{FeatureSpec, GivenWhenThen}

class KnapsackSolutionForIntsSpec extends FeatureSpec with GivenWhenThen {


  feature("Solution should throw on invalid input") {

    scenario("Provided ints are invalid") {

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

    scenario("Provided goal <= 0") {

      Given("Some valid ints combination")
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

  feature("Solution should return empty result when it's expected") {

    scenario("Every int in combination > goal") {

      Given("combination of ints, each > goal")
      val combination = List(13, 13)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be empty")
      assert(combinations.isEmpty)
    }

    scenario("Every int in combination barely < goal") {

      Given("combination of ints, each barely < goal")
      val combination = List(11, 11)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be empty")
      assert(combinations.isEmpty)
    }
  }

  feature("Solution should result in expected combinations") {

    scenario("Combination has one int == goal") {

      Given("combination of one int == goal")
      val combination = List(12)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be one combination of int == goal")
      assert(combinations === once(12))
    }

    scenario("Combination has two ints == goal") {

      Given("combination of two ints == goal")
      val combination = List(12, 12, 9, 8)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be two expected combinations")
      assert(combinations === twice(12))
    }

    scenario("Goal is sum of several ints") {

      Given("combination of several ints sums to goal")
      val combination = List(8, 2, 1, 1)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be one expected combination")
      assert(sorted(combinations) === once(8, 2, 1, 1))
    }

    scenario("Goal can be reached many possible ways") {

      Given("combination that may produce many answers")
      val combination = List(1, 1, 2, 3, 3, 4, 8, 9)

      When("solution is applied")
      val combinations = new KnapsackSolutionForInts(combination)(12)

      Then("result should be expected")
      assert(sorted(combinations) ===

        // starting with 9
        twice(9, 3) ::: twice(9, 2, 1) :::

          // starting with 8
          once(8, 4) ::: fourTimes(8, 3, 1) ::: once(8, 2, 1, 1) :::

          // starting with 4
          once(4, 3, 3, 2) ::: once(4, 3, 3, 1, 1)
      )
    }
  }

  /* utils */

  private def once(ints: Int*): List[List[Int]] = List.fill(1)(ints.toList)

  private def twice(ints: Int*): List[List[Int]] = List.fill(2)(ints.toList)

  private def fourTimes(ints: Int*): List[List[Int]] = List.fill(4)(ints.toList)

  private def sorted(combinations: List[List[Int]]): List[List[Int]] = combinations.map(_.sortWith(_ > _))

}
