package agp

import scala.annotation.tailrec

object Utils {

  implicit class RichSeq[T](seq: Seq[T]) {
    def equallyDividedInto(partitionsCount: Int): List[Seq[T]] = divideEqually(seq, partitionsCount)
  }

  private def divideEqually[T](seq: Seq[T], n: Int): List[Seq[T]] = {
    val idealChunkSize = seq.size / n

    @tailrec
    def divide(undivided: Seq[T], partitions: List[Seq[T]], extra: Int): List[Seq[T]] =
      if (undivided.isEmpty) partitions else {
        // take X elements that ideally should be in a chunk,
        // but if there is extra, take +1 and decrement extra
        val (chunkSize, newExtra) = if (extra == 0) (idealChunkSize, 0)
                                    else (idealChunkSize + 1, extra - 1)

        val (newPartition, remaining) = undivided splitAt chunkSize
        divide(remaining, partitions = newPartition :: partitions, newExtra)
      }

    divide(undivided = seq,
           partitions = List.empty,
           extra = seq.size % n).reverse
  }

}