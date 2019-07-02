import scala.annotation.tailrec

def cut[T](items: Seq[T], partitionsCount: Int): List[Seq[T]] = {
  val div = items.size / partitionsCount
  val mod = items.size % partitionsCount

  @tailrec
  def loop(unpartitioned: Seq[T], acc: List[Seq[T]], extra: Int): List[Seq[T]] =
    if (unpartitioned.isEmpty) acc
    else {
      val (splitIndex, newExtra) = if (extra > 0) (div + 1, extra - 1) else (div, extra)
      val (newPartitioned, remaining) = unpartitioned.splitAt(splitIndex)
      loop(remaining, newPartitioned :: acc, newExtra)
    }

  loop(items, List.empty, mod).reverse
}

cut(1 to 5, 3)