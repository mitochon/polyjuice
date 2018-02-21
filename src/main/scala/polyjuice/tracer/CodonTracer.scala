package polyjuice.tracer

import polyjuice.model._

case class CodonTracer(gene: Gene) {
  import CodonTracer._

  def aminoAcid(codonNumber: Int, transcript: Transcript): Option[AminoAcid] = {
    gene.get(transcript).flatMap(lookup(_, codonNumber))
  }

  def aminoAcid(codonNumber: Int): Map[Transcript, AminoAcid] = {
    gene.flatMap {
      case (transcript, g) => lookup(g, codonNumber).map((transcript, _))
    }
  }
}

object CodonTracer {

  def lookup(g: EnsemblGene, codonNumber: Int): Option[AminoAcid] = {
    val codon = g.codingSequence.drop((codonNumber - 1) * 3).take(3)
    AminoAcid.ByCodon.get(codon)
  }
}