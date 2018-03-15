package polyjuice.potion.tracer

import scala.util.Try

import polyjuice.potion.model._

case class ExonTracer(gene: Gene) {
  import ExonTracer._

  def exon(num: Int, transcript: Transcript): Option[Exon] = {
    gene.get(transcript).flatMap(lookup(_, num))
  }

  def exon(num: Int): Map[Transcript, Exon] = {
    for {
      (transcipt, g) <- gene
      exon <- lookup(g, num)
    } yield (transcipt, exon)
  }

  def coord(num: Int, transcript: Transcript): Option[RegionCoord] = {
    exon(num, transcript).map(e => RegionCoord(e.chr, e.start, e.end))
  }

  def coord(num: Int): Map[Transcript, RegionCoord] = {
    for {
      (transcript, e) <- exon(num)
      r <- coord(num, transcript)
    } yield (transcript, r)
  }
}

object ExonTracer {

  def lookup(g: EnsemblGene, num: Int): Option[Exon] = {
    g.exons.find(_.rank == num)
  }
}