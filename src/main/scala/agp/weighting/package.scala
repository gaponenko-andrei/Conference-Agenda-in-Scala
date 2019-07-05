package agp

package object weighting {

  // todo think about introducing of abstract type instead of generic type
  trait Weighable[T <: Ordered[T]] extends Ordered[Weighable[T]] {

    def compare(other: Weighable[T]): Int =
      this.weight.compareTo(other.weight)

    def weight: T

    def isPositive: Boolean

    def -(otherWeight: T): Weighable[T]
  }

}
