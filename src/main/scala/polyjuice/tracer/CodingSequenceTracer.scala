package polyjuice.tracer

import scala.util.Try
import scala.util.control.Breaks._

import polyjuice.model._

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
      (transcript, base) <- cds(pos)
      base <- coord(pos, transcript)
    } yield (transcript, base)
  }
}

object CodingSequenceTracer {

  case class Offset(exon: Exon, pos: Int) {
    def single(b: Base): Single = {
      Single(exon.chr, exon.start + pos - 1, b)
    }
  }

  def seek(g: EnsemblGene, pos: Int): Option[Offset] = {
    def isLastExon(e: Exon): Boolean = {
      g.end == e.end
    }

    def isPastUTR3(e: Exon, i: Int): Boolean = {
      isLastExon(e) && e.length - g.utr3.fold(0)(_.length) < i
    }

    var offset = None: Option[Offset]
    var remainder = pos + g.utr5.fold(0)(_.length)

    breakable {
      for (e <- g.exons) {
        if (remainder > e.length) remainder -= e.length
        else {
          if (!isPastUTR3(e, remainder)) {
            offset = Some(Offset(e, remainder))
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
}