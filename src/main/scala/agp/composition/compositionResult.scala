package agp.composition

import agp.vo.{Session, Talk}


final case class SessionCompositionResult[S <: Session](
  session: S, unusedTalks: Set[Talk]
)

final case class SessionsCompositionResult[S <: Session](
  sessions: Set[S], unusedTalks: Set[Talk]
)