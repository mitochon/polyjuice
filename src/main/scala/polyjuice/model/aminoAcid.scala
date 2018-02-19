package polyjuice.model

abstract class AminoAcid(
  val name: String,
  val code: String,
  val letter: Char,
  val codons: Seq[String])

case object Ile extends AminoAcid("Isoleucine", "Ile", 'I', Seq("ATT", "ATC", "ATA"))
case object Leu extends AminoAcid("Leucine", "Leu", 'L', Seq("CTT", "CTC", "CTA", "CTG", "TTA", "TTG"))
case object Val extends AminoAcid("Valine", "Val", 'V', Seq("GTT", "GTC", "GTA", "GTG"))
case object Phe extends AminoAcid("Phenylalanine", "Phe", 'F', Seq("TTT", "TTC"))
case object Met extends AminoAcid("Methionine", "Met", 'M', Seq("ATG")) // start codon
case object Cys extends AminoAcid("Cysteine", "Cys", 'C', Seq("TGT", "TGC"))
case object Ala extends AminoAcid("Alanine", "Ala", 'A', Seq("GCT", "GCC", "GCA", "GCG"))
case object Gly extends AminoAcid("Glycine", "Gly", 'G', Seq("GGT", "GGC", "GGA", "GGG"))
case object Pro extends AminoAcid("Proline", "Pro", 'P', Seq("CCT", "CCC", "CCA", "CCG"))
case object Thr extends AminoAcid("Threonine", "Thr", 'T', Seq("ACT", "ACC", "ACA", "ACG"))
case object Ser extends AminoAcid("Serine", "Ser", 'S', Seq("TCT", "TCC", "TCA", "TCG", "AGT", "AGC"))
case object Tyr extends AminoAcid("Tyrosine", "Tyr", 'Y', Seq("TAT", "TAC"))
case object Trp extends AminoAcid("Tryptophan", "Trp", 'W', Seq("TGG"))
case object Gln extends AminoAcid("Glutamine", "Gln", 'Q', Seq("CAA", "CAG"))
case object Asn extends AminoAcid("Asparagine", "Asn", 'N', Seq("AAT", "AAC"))
case object His extends AminoAcid("Histidine", "His", 'H', Seq("CAT", "CAC"))
case object Glu extends AminoAcid("Glutamic acid", "Glu", 'E', Seq("GAA", "GAG"))
case object Asp extends AminoAcid("Aspartic acid", "Asp", 'D', Seq("GAT", "GAC"))
case object Lys extends AminoAcid("Lysine", "Lys", 'K', Seq("AAA", "AAG"))
case object Arg extends AminoAcid("Arginine", "Arg", 'R', Seq("CGT", "CGC", "CGA", "CGG", "AGA", "AGG"))
case object Stop extends AminoAcid("Stop", "*", '*', Seq("TAA", "TAG", "TGA"))
