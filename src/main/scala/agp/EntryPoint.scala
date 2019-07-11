package agp

import java.time.format.DateTimeFormatter

import agp.scheduling.{ConferenceAgendaScheduling, ConferenceTrack, Scheduling}
import agp.vo.{Lunch, NetworkingEvent, Talk}

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

  val talks: Set[Talk] = FileParsing(source)
  val tracks: Set[ConferenceTrack] = ConferenceAgendaScheduling(talks)

  // Output results

  tracks.toList.sortWith(_.title < _.title).foreach { it =>
    println()
    println(it.title)
    it.toList.sorted.foreach(printScheduling)
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



