package agp

import agp.vo.Talk

package object scheduling {
  type ConferenceTracksScheduling = Set[Talk] => Set[ConferenceTrack]
}
