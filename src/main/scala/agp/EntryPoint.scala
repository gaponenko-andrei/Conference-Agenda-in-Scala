package agp

import java.time.format.DateTimeFormatter

import agp.scheduling.{ConferenceAgendaScheduling2, ConferenceTrack, Scheduling}
import agp.vo.{Lunch, NetworkingEvent, Talk}
import org.scalactic.Or

import scala.io.Source

object EntryPoint extends App {

  val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

  // Decide what is file to use

  val source = if (args.nonEmpty) {
    println(s"File ${args(0)} is used as input.")
    Source.fromFile(args(0))
  } else {
    println("SampleInput.txt is used as input.")
    Source.fromResource("SampleInput.txt")
  }

  // Schedule conference tracks

  val tracks: Set[ConferenceTrack] Or scheduling.Exception= {
    val inputTalks: Set[Talk] = TalksParsing(source)
    ConferenceAgendaScheduling2(inputTalks)
  }

  // Output results

  tracks map(printTracks) badMap(println)

  private def printTracks(tracks: Set[ConferenceTrack]): Unit = {
    for (track <- tracks.toList sortBy (_.title)) {
      println()
      println(track.title)
      track.toList.sorted.foreach(printScheduling)
    }
  }

  def printScheduling(scheduling: Scheduling): Unit = {
    val startTime: String = timeFormatter format scheduling.startTime
    val duration: String = buildDurationStringFor(scheduling)
    println(s"$startTime ${scheduling.title} $duration")
  }

  def buildDurationStringFor(scheduling: Scheduling): String = scheduling match {
    case Scheduling(Lunch, _) | Scheduling(NetworkingEvent, _) => ""
    case Scheduling(event, _) if event.duration.toMinutes == 5 => "lightning"
    case Scheduling(event, _) => event.duration.toString
  }
}



