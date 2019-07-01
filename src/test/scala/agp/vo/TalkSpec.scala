package agp.vo

import agp.vo.event.Talk
import org.scalacheck._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

class TalkSpec extends WordSpec with Matchers with GeneratorDrivenPropertyChecks {

  private lazy val titles: Gen[String] = Gen.alphaStr.suchThat(_.trim.nonEmpty)

  private lazy val differentTitles: Gen[(String, String)] = for {
    title1 <- titles
    title2 <- titles
    if title1 != title2
  } yield (title1, title2)


  "Talk" should {

    "equal other Talk only when their titles are equal" in {
      forAll(titles, minSuccessful(100)) { title =>
        Talk(title, 30) shouldBe Talk(title, 40)
      }
      forAll(differentTitles, minSuccessful(200)) {
        case (title1, title2) =>
          Talk(title1, 30) should not equal Talk(title2, 30)
      }
    }

    "have some characters other then spaces" in {
      List("", " ").foreach { title =>
        an[IllegalArgumentException] should be thrownBy Talk(title, 30)
      }
    }

    "have duration 5 <= minutes <= 60" in {
      (5 to 60).foreach { minutes =>
        noException should be thrownBy Talk("Title", minutes)
      }
      forAll(minSuccessful(1000)) { minutes: Int =>
        whenever(minutes < 5 || minutes > 60) {
          an[IllegalArgumentException] should be thrownBy Talk("Title", minutes)
        }
      }
    }
  }
}
