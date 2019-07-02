package agp.vo

import org.scalacheck._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class TalkSpec extends PropSpec with Matchers with GeneratorDrivenPropertyChecks {

  private lazy val titles: Gen[String] = Gen.alphaStr.suchThat(_.trim.nonEmpty)

  private lazy val differentTitles: Gen[(String, String)] = for {
    title1 <- titles
    title2 <- titles
    if title1 != title2
  } yield (title1, title2)


  property("talks with same titles should be equal") {
    forAll(titles, minSuccessful(100)) { title =>
      Talk(title, 30) shouldBe Talk(title, 40)
    }
  }

  property("talks with different titles should not be equal") {
    forAll(differentTitles, minSuccessful(200)) {
      case (title1, title2) =>
        Talk(title1, 30) should not equal Talk(title2, 30)
    }
  }

  property("talk title must have non-space characters") {
    List("", " ").foreach { title =>
      an[IllegalArgumentException] should be thrownBy Talk(title, 30)
    }
  }

  property("talk duration may be 5 <= minutes <= 60") {
    (5 to 60).foreach { minutes =>
      noException should be thrownBy Talk("Title", minutes)
    }
  }

  property("talk duration must be 5 <= minutes <= 60") {
    forAll(minSuccessful(1000)) { minutes: Int =>
      whenever(minutes < 5 || minutes > 60) {
        an[IllegalArgumentException] should be thrownBy Talk("Title", minutes)
      }
    }
  }
}
