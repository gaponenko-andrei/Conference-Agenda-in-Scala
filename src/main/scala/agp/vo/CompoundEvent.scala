package agp.vo

abstract class CompoundEvent[E <: Event](override val title: String, val events: Iterable[E])
  extends Event(title, events.duration) {

  require(events.nonEmpty, "EventSequence must have at least one event.")
}

case class CompoundSetEvent[E <: Event](override val title: String, override val events: Set[E])
  extends CompoundEvent(title, events)

case class CompoundListEvent[E <: Event](override val title: String, override val events: List[E])
  extends CompoundEvent(title, events)