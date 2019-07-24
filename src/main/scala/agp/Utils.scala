package agp

import org.scalactic.Or

import scala.annotation.tailrec

object Utils {

  /** Alias for "on met requirements" */
  type OnMetReq[A] = A Or IllegalArgumentException

  // RichSeq

  final implicit class RichSeq[A](seq: Seq[A]) {
    def equallyDividedInto(partitionsCount: Int): List[Seq[A]] = divideEqually(seq, partitionsCount)
  }

  private def divideEqually[A](seq: Seq[A], n: Int): List[Seq[A]] = {
    val idealChunkSize = seq.size / n

    @tailrec
    def divide(undivided: Seq[A], partitions: List[Seq[A]], extra: Int): List[Seq[A]] =
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