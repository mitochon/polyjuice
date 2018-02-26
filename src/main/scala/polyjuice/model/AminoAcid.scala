package polyjuice.model

case class AminoAcid(
  val code: String,
  val letter: Char,
  val name: String,
  val codons: Set[Codon])

object AminoAcid {

  val All = Seq(
    AminoAcid("Ile", 'I', "Isoleucine", Set("ATT", "ATC", "ATA").map(Codon(_))),
    AminoAcid("Leu", 'L', "Leucine", Set("CTT", "CTC", "CTA", "CTG", "TTA", "TTG").map(Codon(_))),
    AminoAcid("Val", 'V', "Valine", Set("GTT", "GTC", "GTA", "GTG").map(Codon(_))),
    AminoAcid("Phe", 'F', "Phenylalanine", Set("TTT", "TTC").map(Codon(_))),
    AminoAcid("Met", 'M', "Methionine", Set(Codon.Start)),
    AminoAcid("Cys", 'C', "Cysteine", Set("TGT", "TGC").map(Codon(_))),
    AminoAcid("Ala", 'A', "Alanine", Set("GCT", "GCC", "GCA", "GCG").map(Codon(_))),
    AminoAcid("Gly", 'G', "Glycine", Set("GGT", "GGC", "GGA", "GGG").map(Codon(_))),
    AminoAcid("Pro", 'P', "Proline", Set("CCT", "CCC", "CCA", "CCG").map(Codon(_))),
    AminoAcid("Thr", 'T', "Threonine", Set("ACT", "ACC", "ACA", "ACG").map(Codon(_))),
    AminoAcid("Ser", 'S', "Serine", Set("TCT", "TCC", "TCA", "TCG", "AGT", "AGC").map(Codon(_))),
    AminoAcid("Tyr", 'Y', "Tyrosine", Set("TAT", "TAC").map(Codon(_))),
    AminoAcid("Trp", 'W', "Tryptophan", Set("TGG").map(Codon(_))),
    AminoAcid("Gln", 'Q', "Glutamine", Set("CAA", "CAG").map(Codon(_))),
    AminoAcid("Asn", 'N', "Asparagine", Set("AAT", "AAC").map(Codon(_))),
    AminoAcid("His", 'H', "Histidine", Set("CAT", "CAC").map(Codon(_))),
    AminoAcid("Glu", 'E', "Glutamic acid", Set("GAA", "GAG").map(Codon(_))),
    AminoAcid("Asp", 'D', "Aspartic acid", Set("GAT", "GAC").map(Codon(_))),
    AminoAcid("Lys", 'K', "Lysine", Set("AAA", "AAG").map(Codon(_))),
    AminoAcid("Arg", 'R', "Arginine", Set("CGT", "CGC", "CGA", "CGG", "AGA", "AGG").map(Codon(_))))

  val ByCode = All.map(aa => (aa.code, aa)).toMap

  val BySingleLetter = All.map(aa => (aa.letter, aa)).toMap

  val ByCodon = All.flatMap(aa => aa.codons.map((_, aa))).toMap
}