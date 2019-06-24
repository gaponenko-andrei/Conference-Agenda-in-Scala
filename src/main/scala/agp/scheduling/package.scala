package agp

import agp.vo.{CompositeEvent, Event}

package object scheduling {

  trait Scheduling[E <: Event, C <: CompositeEvent[E]] extends (Set[E] => Result[E, C])

  final class Result[E <: Event, C <: CompositeEvent[E]](val event: C, val unusedEvents: Iterable[E])

  final class SchedulingException(val msg: String) extends RuntimeException(msg)
}
