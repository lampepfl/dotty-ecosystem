package ecosystem.model

import ecosystem.rendering._

case class CheckReport(
  mainBranch: String,
  aheadUpstream: Int,
  behindUpstream: Int,
  ciHash: String,
  originHeadHash: String,
  isCiAhead: Boolean,
  isCiBehind: Boolean,
)
  def ciTrackingStatus: CITrackingStatus =
    import CITrackingStatus._
    if ciHash == originHeadHash then Equal
    else if !(isCiAhead || isCiBehind) then Unrelated
    else if isCiAhead then Ahead
    else Behind

enum CITrackingStatus
  case Ahead, Behind, Unrelated, Equal

  def render = this match
    case Ahead => yellow(">")
    case Behind => yellow("<")
    case Unrelated => red("!=")
    case Equal => green("==")
