package agp.scheduling

import agp.vo.{MorningSession, Talk}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import scala.language.postfixOps


class MorningSessionsSchedulingImplSpec extends WordSpec with Matchers with GivenWhenThen with MockFactory {

//  "test" in {
//
//    val scheduling = mock[MorningSessionScheduling]
//
//    scheduling.apply _ expects where((_: Set[Talk]).size == 10) returning {
//      print("first stub call")
//      new MorningSessionSchedulingResult(session = session(1), unusedTalks = 5 talks)
//    }
//
//    scheduling.apply _ expects where((_: Set[Talk]).size == 5) returning {
//      print("second stub call")
//      new MorningSessionSchedulingResult(session = session(2), unusedTalks = 2 talks)
//    }
//
//    val requiredSessionsNumber = 3
//
//    MorningSessionsSchedulingImpl
//      .using(scheduling)(requiredSessionsNumber)(10 talks)
//  }

  "MorningSessionsSchedulingImpl" should {

    "throw" when {

      "given number of talks is less then required number of morning sessions" in {

        Given("required number of sessions = 3")
        val requiredSessionsNumber = 3

        And("scheduling using this value")
        val scheduling = MorningSessionsSchedulingImpl(requiredSessionsNumber)

        And("two random talks")
        val talks = 2 talks

        Then("exception should be thrown when scheduling is applied")
        an[IllegalArgumentException] should be thrownBy scheduling(talks)
      }

      "given morning session scheduling throws exception" in {

        Given("required number of sessions = 2")
        val requiredSessionsNumber = 2

        And("morning session scheduling throwing exception")
        val sessionScheduling = newFailingSessionScheduling

        And("morning sessions scheduling using them")
        val scheduling = MorningSessionsSchedulingImpl
          .using(sessionScheduling)(requiredSessionsNumber)

        Then("exception should be thrown when scheduling is applied")
        an[SchedulingException] should be thrownBy scheduling(2 talks)
      }
    }

    "not throw" when {

      "given number of talks is >= required number of morning sessions" in {

        Given("required number of sessions = 2")
        val requiredSessionsNumber = 2

        And("scheduling using this value")
        val scheduling = MorningSessionsSchedulingImpl(requiredSessionsNumber)

        And("two random talks")
        val talks = 2 talks

        Then("no exception should be thrown when scheduling is applied")
        noException should be thrownBy scheduling(talks)
      }
    }
  }

  /* utils */

  def session(i: Int) = MorningSession(2 talks)

  def newFailingSessionScheduling: MorningSessionScheduling = {
    val scheduling = mock[MorningSessionScheduling]
    scheduling.apply _ expects * throwing new SchedulingException("_")
    scheduling
  }

  implicit class RandomTalksFactory(number: Int) {
    def talks: Set[Talk] = (1 to number).map(i => Talk(s"Title ${i + 1}", 5 + i)).toSet
  }

}
