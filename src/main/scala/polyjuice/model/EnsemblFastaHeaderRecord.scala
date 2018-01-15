package polyjuice.model

case class EnsemblFastaHeaderRecord(
  transcriptId: String,
  geneSymbol: String,
  ensembGeneId: String,
  genome: GenomeBuild.Value,
  strand: Strand.Value,
  isChromosome: Boolean,
  seqType: String,
  contig: String,
  start: Int,
  end: Int)

object EnsemblFastaHeaderRecord {

  val Prefix = ">"
  val ChromosomeStr = "chromosome"
  val ChromosomeRegex = """(\w+):(\w+):(\S+):(\d+):(\d+):([-]?[1])"""
  val LineRegex = s"^$Prefix(\\S+) (\\S+) $ChromosomeRegex gene:(\\S+).*gene_symbol:(\\S+).*".r

  def apply(line: String): EnsemblFastaHeaderRecord = {

    def toStrand(s: String) = s match {
      case "1"  => Strand.Plus
      case "-1" => Strand.Minus
      case _    => throw new IllegalArgumentException(s"Unknown strand $s")
    }

    line match {
      case LineRegex(trId, seqType, chrom, genome, contig, start, end, strand, geneId, gene) =>
        EnsemblFastaHeaderRecord(trId, gene, geneId, GenomeBuild.withName(genome), toStrand(strand),
          chrom.equals(ChromosomeStr), seqType, contig, start.toInt, end.toInt)
      case _ => throw new IllegalArgumentException(s"Unable to parse $line")
    }
  }
}