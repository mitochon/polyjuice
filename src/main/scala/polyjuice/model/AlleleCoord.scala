package polyjuice.model

case class AlleleCoord(
  contig: String,
  pos: Int,
  ref: String,
  alt: String)