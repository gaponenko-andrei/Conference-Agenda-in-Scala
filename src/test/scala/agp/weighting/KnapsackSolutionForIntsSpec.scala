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

}
