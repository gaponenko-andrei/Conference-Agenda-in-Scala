package agp

import agp.vo.Talk

import scala.io.BufferedSource

object FileParsing extends (BufferedSource => Set[Talk]) {

  override def apply(source: BufferedSource): Set[Talk] =
    source.getLines.map(parseTalk).toSet

  private def parseTalk(talkString: String): Talk = {
    val chunks = talkString.split(" ").toList
    val durationString = chunks.last ensuring (isDuration(_))
    val title = talkString replace(durationString, "")
    if (!durationString.contains("min")) Talk(title, 5)
    else Talk(title, minutes = durationString.replace("min", "").toInt)
  }

  private def isDuration(s: String): Boolean =
    s.contains("min") || s.contains("lightning")
}
