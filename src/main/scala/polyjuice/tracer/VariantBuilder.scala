package polyjuice.tracer

import polyjuice.model._

object VariantBuilder {

  def build(single: Single, to: Base, strand: Strand.Value): Snv = {
    strand match {
      case Strand.Plus  => Snv(single.contig, single.pos, single.base, to)
      case Strand.Minus => Snv(single.contig, single.pos, single.base, to.complement)
    }
  }
}