package agp

object TestUtils {

  def setup[T](obj: T)(setup: T => Unit): T = {
    setup(obj)
    obj
  }

  def returning[T, U](value: U): T => U = _ => value

  def throwing[T](exception: Throwable): Any => Nothing = _ => throw exception
}
