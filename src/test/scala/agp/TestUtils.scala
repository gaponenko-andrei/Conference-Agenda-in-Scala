package agp

import java.util.UUID.randomUUID

import agp.scheduling.{ConferenceTrack, Scheduling}
import agp.vo.{AfternoonSession, Event, Lunch, MorningSession, NetworkingEvent, Talk}

object TestUtils {

  def someTalks: Set[Talk] = 2 talks

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  def talksOf(tracks: Set[ConferenceTrack]): Set[Talk] =
    tracks.flatten.collect { case Scheduling(talk: Talk, _) => talk }

  def eventsOf(schedulings: Set[Scheduling]): Set[Event] = schedulings map (_.event)

  def eventsOf(schedulings: List[Scheduling]): List[Event] = schedulings map (_.event)


  // ExtendedConferenceTrack

  implicit final class ExtendedConferenceTrack(track: ConferenceTrack) {

    lazy val events: Set[Event] = eventsOf(track)

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

    def morningSessions: Set[MorningSession] =
      this fillSet MorningSession(uniqueTitle, 2 talks)

    def afternoonSessions: Set[AfternoonSession] =
      this fillSet AfternoonSession(uniqueTitle, 2 talks)

    private def uniqueTitle: String = randomUUID.toString

    private def fillSet[T](value: => T): Set[T] = Iterable.fill(requiredCount)(value).toSet
  }
}
