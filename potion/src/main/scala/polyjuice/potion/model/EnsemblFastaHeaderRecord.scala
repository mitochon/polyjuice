package polyjuice.potion.model

case class EnsemblFastaHeaderRecord(
  transcript: Transcript,
  geneSymbol: GeneSymbol,
  ensemblGeneId: String,
  genome: GenomeBuild.Value,
  strand: Strand.Value,
  isChromosome: Boolean,
  seqType: String,
  contig: String,
  start: Int,
  stop: Int)

object EnsemblFastaHeaderRecord {

  val Prefix = ">"
  val ChromosomeStr = "chromosome"
  val ChromosomePattern = """(\w+):(\w+):(\S+):(\d+):(\d+):([-]?[1])"""
  val LineRegex = s"^$Prefix(\\S+) (\\S+) $ChromosomePattern gene:(\\S+).*gene_symbol:(\\S+).*".r

  @throws[Exception]
  def apply(line: String): EnsemblFastaHeaderRecord = {

    def toStrand(s: String) = s match {
      case "1"  => Strand.Plus
      case "-1" => Strand.Minus
      case _    => throw new IllegalArgumentException(s"Unknown strand $s")
    }

    line match {
      case LineRegex(t, seqType, chr, genome, contig, start, stop, strand, geneId, gene) =>
        EnsemblFastaHeaderRecord(t, gene, geneId, GenomeBuild.withName(genome), toStrand(strand),
          chr.equals(ChromosomeStr), seqType, contig, start.toInt, stop.toInt)
      case _ => throw new IllegalArgumentException(s"Unable to parse $line")
    }
  }
}