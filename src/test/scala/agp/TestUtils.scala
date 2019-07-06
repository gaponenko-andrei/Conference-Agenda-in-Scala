package agp

object TestUtils {

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }
}
