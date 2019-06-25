package agp.scheduling

import agp.vo.Talk
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}


class MorningSessionsSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen {

  "MorningSessionsSchedulingImpl" should {

    "throw" when {

      "given number of talks is less then required number of morning sessions" in {

        Given("required number of sessions is 2")
        val requiredSessionsNumber = 2

        And("scheduling using this value")
        val scheduling = MorningSessionsSchedulingImpl(requiredSessionsNumber)

        And("singleton set of talk")
        val talks = Set(Talk("Title", 30))

        Then("exception should be thrown when scheduling is applied")
        an[IllegalArgumentException] should be thrownBy scheduling(talks)
      }
    }
  }

}
