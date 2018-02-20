package polyjuice.model

case class AminoAcid(
  val name: String,
  val code: String,
  val letter: Char,
  val codons: Seq[String])

object AminoAcid {

  val All = Seq(
    AminoAcid("Isoleucine", "Ile", 'I', Seq("ATT", "ATC", "ATA")),
    AminoAcid("Leucine", "Leu", 'L', Seq("CTT", "CTC", "CTA", "CTG", "TTA", "TTG")),
    AminoAcid("Valine", "Val", 'V', Seq("GTT", "GTC", "GTA", "GTG")),
    AminoAcid("Phenylalanine", "Phe", 'F', Seq("TTT", "TTC")),
    AminoAcid("Methionine", "Met", 'M', Seq("ATG")), // start codon
    AminoAcid("Cysteine", "Cys", 'C', Seq("TGT", "TGC")),
    AminoAcid("Alanine", "Ala", 'A', Seq("GCT", "GCC", "GCA", "GCG")),
    AminoAcid("Glycine", "Gly", 'G', Seq("GGT", "GGC", "GGA", "GGG")),
    AminoAcid("Proline", "Pro", 'P', Seq("CCT", "CCC", "CCA", "CCG")),
    AminoAcid("Threonine", "Thr", 'T', Seq("ACT", "ACC", "ACA", "ACG")),
    AminoAcid("Serine", "Ser", 'S', Seq("TCT", "TCC", "TCA", "TCG", "AGT", "AGC")),
    AminoAcid("Tyrosine", "Tyr", 'Y', Seq("TAT", "TAC")),
    AminoAcid("Tryptophan", "Trp", 'W', Seq("TGG")),
    AminoAcid("Glutamine", "Gln", 'Q', Seq("CAA", "CAG")),
    AminoAcid("Asparagine", "Asn", 'N', Seq("AAT", "AAC")),
    AminoAcid("Histidine", "His", 'H', Seq("CAT", "CAC")),
    AminoAcid("Glutamic acid", "Glu", 'E', Seq("GAA", "GAG")),
    AminoAcid("Aspartic acid", "Asp", 'D', Seq("GAT", "GAC")),
    AminoAcid("Lysine", "Lys", 'K', Seq("AAA", "AAG")),
    AminoAcid("Arginine", "Arg", 'R', Seq("CGT", "CGC", "CGA", "CGG", "AGA", "AGG")),
    AminoAcid("Stop", "*", '*', Seq("TAA", "TAG", "TGA")))

  val ByCode = All.map(aa => (aa.code, aa)).toMap

  val BySingleLetter = All.map(aa => (aa.letter, aa)).toMap

  val ByCodon = All.flatMap(aa => aa.codons.map((_, aa))).toMap
}