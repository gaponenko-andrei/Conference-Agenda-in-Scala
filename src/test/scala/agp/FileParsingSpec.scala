package agp

import agp.vo.Talk
import org.scalatest.FunSuite

import scala.io.Source

class FileParsingSpec extends FunSuite {

  test("parsing SampleInput.txt") {
    val source = Source.fromResource("SampleInput.txt")
    assert(FileParsing(source) === Set(
      Talk("Writing Fast Tests Using Selenium", 60),
      Talk("Overdoing it in Java", 45),
      Talk("AngularJS for the Masses", 30),
      Talk("Ruby Errors from Mismatched Gem Versions", 45),
      Talk("Common Hibernate Errors", 45),
      Talk("Rails for Java Developers", 5),
      Talk("Face-to-Face Communication", 60),
      Talk("Domain-Driven Development", 45),
      Talk("What's New With Java 11", 30),
      Talk("A Perfect Sprint Planning", 30),
      Talk("Pair Programming vs Noise", 45),
      Talk("Java Is Not Magic", 60),
      Talk("Ruby on Rails: Why We Should Move On", 60),
      Talk("Clojure Ate Scala (on my project)", 45),
      Talk("Programming in the Boondocks of Seattle", 30),
      Talk("Ant vs. Maven vs. Gradle Build Tool for Back-End Development", 30),
      Talk("Java Legacy App Maintenance", 60),
      Talk("A World Without Clinical Trials", 30),
      Talk("User Interface CSS in AngularJS Apps", 30)
    ))
  }

}
