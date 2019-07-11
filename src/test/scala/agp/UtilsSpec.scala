package agp

import agp.Utils.RichSeq
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

//noinspection ScalaUnnecessaryParentheses
class UtilsSpec extends WordSpec with Matchers with GivenWhenThen {

  "Method [equallyDivided] should return expected partitions" when {

    "seq is empty" in {
      Seq.empty equallyDividedInto 3 shouldBe empty
    }

    "seq.length < required partitions number" in {
      1 to 3 equallyDividedInto 5 shouldBe List(Seq(1), Seq(2), Seq(3))
    }

    "seq.length = required partitions number" in {
      1 to 3 equallyDividedInto 3 shouldBe List(Seq(1), Seq(2), Seq(3))
    }

    "seq.length > required partitions number" in {
      1 to 10 equallyDividedInto 3 shouldBe List((1 to 4), (5 to 7), (8 to 10))
    }
  }
}
