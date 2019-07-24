package agp.weighting

/** A trait defining what methods and properties that should have a
  * class to be used with [[agp.weighting.GeneralKnapsackSolution]].
  *
  * @tparam A type of comparable element
  */
private[weighting] trait Weighable[A <: Ordered[A]] extends Ordered[Weighable[A]] {

  override def compare(other: Weighable[A]): Int = weight.compareTo(other.weight)

  /** Comparable used as a weight */
  def weight: A

  /** Returns 'true' if weight of this instance is considered positive */
  def isPositive: Boolean

  /** Returns new instance of [[agp.weighting.Weighable]] with a weight that should
    * be result of subtraction between weight of this instance and passed value */
  def -(otherWeight: A): Weighable[A]
}