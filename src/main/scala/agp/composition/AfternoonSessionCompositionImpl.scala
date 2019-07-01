package agp.composition

import agp.vo.event.Talk
import agp.vo.session.AfternoonSession

class AfternoonSessionCompositionImpl extends (Set[Talk] => AfternoonSessionCompositionResult ){

  override def apply(talks: Set[Talk]): AfternoonSessionCompositionResult = {
    AfternoonSessionCompositionResult(AfternoonSession(talks), unusedTalks = Set())
  }

}
