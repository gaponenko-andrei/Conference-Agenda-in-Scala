package agp.composition

import agp.vo.Talk

class AfternoonSessionsCompositionImpl extends (Set[Talk] => AfternoonSessionsCompositionResult) {

  override def apply(v1: Set[Talk]): AfternoonSessionsCompositionResult = ???
}
