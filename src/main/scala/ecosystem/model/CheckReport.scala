package ecosystem.model

case class CheckReport(
  mainBranch: String,
  aheadUpstream: Int,
  behindUpstream: Int,
  ciHash: String,
  originHeadHash: String
)