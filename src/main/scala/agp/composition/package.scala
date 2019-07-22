package agp

import agp.Utils.OnMetReq
import agp.vo._
import org.scalactic.Or

package object composition {

  /** Definition of knapsack solution for talks that returns all
    * combinations of given talks according to it's implementation
    * or `IllegalArgumentException` if requirements were not met
    */
  type KnapsackSolution = Set[Talk] => OnMetReq[TalksCombinations]

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                          Sessions Compositions                          */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  /** Definition of function producing [[agp.vo.Session]] from talks */
  type SessionsComposition[S <: Session] = Set[Talk] => ExceptionOr[S]

  /** Definition of function producing [[agp.vo.MorningSession]] from talks */
  type MorningSessionsComposition = SessionsComposition[MorningSession]

  /** Definition of function producing [[agp.vo.AfternoonSession]] from talks */
  type AfternoonSessionsComposition = SessionsComposition[AfternoonSession]

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                       Sessions Composition Result                       */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  /** Holder to keep composed [[agp.vo.Session]] along with
    * talks that were provided for it's composition, but were
    * left unused by impl of [[agp.composition.SessionsComposition]]
    */
  final case class SessionsCompositionResult[S <: Session](sessions: Set[S], unusedTalks: Set[Talk])

  /** Holder to keep composed [[agp.vo.MorningSession]] along with
    * talks that were provided for it's composition, but were left
    * unused by impl of [[agp.composition.MorningSessionsComposition]]
    */
  type MorningSessionsCompositionResult = SessionsCompositionResult[MorningSession]

  /** Holder to keep composed [[agp.vo.AfternoonSession]] along with
    * talks that were provided for it's composition, but were left
    * unused by impl of [[agp.composition.AfternoonSessionsComposition]]
    */
  type AfternoonSessionsCompositionResult = SessionsCompositionResult[AfternoonSession]

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                             Error Handling                              */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  /** Alias for package use only, bound to package-specific exception */
  private[composition] type ExceptionOr[T] = T Or composition.Exception

  /** Specialized exception that should be thrown by
    * every component of the package in case of error
    */
  final case class Exception(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
}