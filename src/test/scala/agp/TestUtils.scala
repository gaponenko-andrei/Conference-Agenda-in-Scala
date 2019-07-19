package agp

import java.util.UUID.randomUUID

import agp.Utils.OnMetReq
import agp.scheduling.{ConferenceTrack, Scheduling}
import agp.vo.{AfternoonSession, Event, Lunch, MorningSession, NetworkingEvent, Session, Talk}
import org.scalactic.{Bad, Or}
import org.scalatest.{Assertion, Inside, Matchers}

trait TestUtils extends Inside with Matchers {

  def uniqueTitle: String = randomUUID.toString

  def someTalks: Set[Talk] = 2 talks

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  def talksOf(tracks: Set[ConferenceTrack]): Set[Talk] =
    tracks.flatten.collect { case Scheduling(talk: Talk, _) => talk }

  def eventsOf(track: ConferenceTrack): Set[Event] = eventsOf(track.schedulings)

  def eventsOf(schedulings: Set[Scheduling]): Set[Event] = schedulings map (_.event)

  def assertBrokenRequirement(obj: OnMetReq[Any], msg: String): Assertion =
    inside(obj) { case Bad(ex: IllegalArgumentException) => ex should have message msg }

  def assertFailedComposition(obj: Any Or composition.Exception, msg: String): Assertion =
    inside(obj) { case Bad(ex: composition.Exception) => ex.message shouldBe msg }

  // ExtendedConferenceTrack

  implicit final class ExtendedConferenceTrack(track: ConferenceTrack) {

    lazy val talksAfterLunch: Set[Talk] = eventsAfterLunch collect { case i: Talk => i }

    lazy val eventsBeforeLunch: Set[Event] = eventsOf(schedulingsBeforeLunch)

    lazy val eventsAfterLunch: Set[Event] = eventsOf(schedulingsAfterLunch)

    lazy val schedulingsBeforeLunch: Set[Scheduling] =
      track.toList.sorted.takeWhile(_ != lunchScheduling).toSet

    lazy val schedulingsAfterLunch: Set[Scheduling] =
      track.toList.sorted.dropWhile(_ != lunchScheduling).tail.toSet

    lazy val lunchScheduling: Scheduling = track find (_.event == Lunch) getOrElse (
      throw new IllegalStateException("Track is supposed to have one scheduling of Lunch event."))

    lazy val networkingEventScheduling: Scheduling = track find (_.event == NetworkingEvent) getOrElse (
      throw new IllegalStateException("Track is supposed to have one scheduling of NetworkingEvent."))
  }

  // DummiesFactory

  implicit class DummiesFactory(requiredCount: Int) {

    def talks: Set[Talk] = this fillSet Talk(uniqueTitle, 10)

    def talksWithDuration(minutes: Int): Set[Talk] =
      this fillSet Talk(uniqueTitle, minutes)

    def morningSessions: Set[MorningSession] =
      this fillSet MorningSession(uniqueTitle, 2 talks)

    def afternoonSessions: Set[AfternoonSession] =
      this fillSet AfternoonSession(uniqueTitle, 2 talks)

    private def fillSet[T](value: => T): Set[T] =
      Iterable.fill(requiredCount)(value).toSet
  }
}
