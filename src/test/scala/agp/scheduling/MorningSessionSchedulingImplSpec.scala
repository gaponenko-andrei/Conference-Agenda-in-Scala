package agp.scheduling

import agp.vo.Talk
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
  }
}
