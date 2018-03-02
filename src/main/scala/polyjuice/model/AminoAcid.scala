package polyjuice.model

case class AminoAcid(
  val code: String,
  val letter: Char,
  val name: String,
  val codons: Set[Codon])

object AminoAcid {

  object Key extends Enumeration {
    val Ile, Leu, Val, Phe, Met, Cys, Ala, Gly, Pro, Thr, Ser, Tyr, Trp, Gln, Asn, His, Glu, Asp, Lys, Arg = Value
  }

  val All = Map(
    Key.Ile -> AminoAcid("Ile", 'I', "Isoleucine", Set("ATT", "ATC", "ATA").map(Codon(_))),
    Key.Leu -> AminoAcid("Leu", 'L', "Leucine", Set("CTT", "CTC", "CTA", "CTG", "TTA", "TTG").map(Codon(_))),
    Key.Val -> AminoAcid("Val", 'V', "Valine", Set("GTT", "GTC", "GTA", "GTG").map(Codon(_))),
    Key.Phe -> AminoAcid("Phe", 'F', "Phenylalanine", Set("TTT", "TTC").map(Codon(_))),
    Key.Met -> AminoAcid("Met", 'M', "Methionine", Set(Codon.Start)),
    Key.Cys -> AminoAcid("Cys", 'C', "Cysteine", Set("TGT", "TGC").map(Codon(_))),
    Key.Ala -> AminoAcid("Ala", 'A', "Alanine", Set("GCT", "GCC", "GCA", "GCG").map(Codon(_))),
    Key.Gly -> AminoAcid("Gly", 'G', "Glycine", Set("GGT", "GGC", "GGA", "GGG").map(Codon(_))),
    Key.Pro -> AminoAcid("Pro", 'P', "Proline", Set("CCT", "CCC", "CCA", "CCG").map(Codon(_))),
    Key.Thr -> AminoAcid("Thr", 'T', "Threonine", Set("ACT", "ACC", "ACA", "ACG").map(Codon(_))),
    Key.Ser -> AminoAcid("Ser", 'S', "Serine", Set("TCT", "TCC", "TCA", "TCG", "AGT", "AGC").map(Codon(_))),
    Key.Tyr -> AminoAcid("Tyr", 'Y', "Tyrosine", Set("TAT", "TAC").map(Codon(_))),
    Key.Trp -> AminoAcid("Trp", 'W', "Tryptophan", Set("TGG").map(Codon(_))),
    Key.Gln -> AminoAcid("Gln", 'Q', "Glutamine", Set("CAA", "CAG").map(Codon(_))),
    Key.Asn -> AminoAcid("Asn", 'N', "Asparagine", Set("AAT", "AAC").map(Codon(_))),
    Key.His -> AminoAcid("His", 'H', "Histidine", Set("CAT", "CAC").map(Codon(_))),
    Key.Glu -> AminoAcid("Glu", 'E', "Glutamic acid", Set("GAA", "GAG").map(Codon(_))),
    Key.Asp -> AminoAcid("Asp", 'D', "Aspartic acid", Set("GAT", "GAC").map(Codon(_))),
    Key.Lys -> AminoAcid("Lys", 'K', "Lysine", Set("AAA", "AAG").map(Codon(_))),
    Key.Arg -> AminoAcid("Arg", 'R', "Arginine", Set("CGT", "CGC", "CGA", "CGG", "AGA", "AGG").map(Codon(_))))

  val ByCode = All.values.map(aa => (aa.code, aa)).toMap

  val BySingleLetter = All.values.map(aa => (aa.letter, aa)).toMap

  val ByCodon = All.values.flatMap(aa => aa.codons.map((_, aa))).toMap
}