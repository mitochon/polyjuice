package polyjuice.potion.tracer

import scala.util.Try
import scala.util.control.Breaks._

import polyjuice.potion.model._

case class CodingSequenceTracer(gene: Gene) {
  import CodingSequenceTracer._

  def cds(pos: Int, transcript: Transcript): Option[Base] = {
    gene.get(transcript).flatMap(lookup(_, pos))
  }

  def cds(pos: Int): Map[Transcript, Base] = {
    for {
      (transcript, g) <- gene
      base <- lookup(g, pos)
    } yield (transcript, base)
  }

  def coord(pos: Int, transcript: Transcript): Option[Single] = {
    for {
      b <- cds(pos, transcript)
      g <- gene.get(transcript)
      o <- seek(g, pos)
    } yield o.single(b)
  }

  def coord(pos: Int): Map[Transcript, Single] = {
    for {
      (transcript, _) <- cds(pos)
      base <- coord(pos, transcript)
    } yield (transcript, base)
  }
}

object CodingSequenceTracer {

  def seek(g: EnsemblGene, pos: Int): Option[Offset] = {

    def dist(e: Exon, f: Option[Exon]): Option[Int] = {
      g.strand match {
        case Strand.Plus  => f.map(_.start - e.end)
        case Strand.Minus => f.map(e.start - _.end)
      }
    }

    def isLastExon(e: Exon): Boolean = {
      g.strand match {
        case Strand.Plus  => g.end == e.end
        case Strand.Minus => g.start == e.start
      }
    }

    def isPastUTR3(e: Exon, i: Int): Boolean = {
      isLastExon(e) && e.length - g.utr3Len < i
    }

    val exons = g.exons.sortBy(_.rank)
    var offset = None: Option[Offset]
    var remainder = pos + g.utr5Len

    breakable {
      for (idx <- exons.indices) {
        val e = exons(idx)
        val f = if (isLastExon(e)) None else Some(exons(idx + 1))

        if (remainder > e.length) remainder -= e.length
        else {
          if (!isPastUTR3(e, remainder)) {
            offset = Some(Offset(e, remainder, g.strand, dist(e, f)))
          }
          break
        }
      }
    }
    offset
  }

  def lookup(g: EnsemblGene, pos: Int): Option[Base] = {
    val base = g.codingSequence.drop(pos - 1).take(1)
    if (base.isEmpty) None else Try(Base(base(0))).toOption
  }

  def lookup(g: EnsemblGene, pos: Int, windowSize: Int): Option[Seq[Base]] = {
    val bases = g.codingSequence.drop(pos - 1).take(windowSize)
    if (bases.isEmpty) None else Try(bases.map(Base(_))).toOption
  }
}