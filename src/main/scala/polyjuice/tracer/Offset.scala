package polyjuice.tracer

import polyjuice.model._

case class Offset(exon: Exon, pos: Int) {
  def single(b: Base): Single = {
    Single(exon.chr, exon.start + pos - 1, b)
  }

  def triple(c: Codon): Triple = {
    Triple(exon.chr, exon.start + pos - 1, c)
  }
}
