package polyjuice.model

case class AminoAcid(
  val code: String,
  val letter: Char,
  val name: String,
  val codons: Seq[Codon])

object AminoAcid {

  val All = Seq(
    AminoAcid("Ile", 'I', "Isoleucine", Seq("ATT", "ATC", "ATA").map(Codon(_))),
    AminoAcid("Leu", 'L', "Leucine", Seq("CTT", "CTC", "CTA", "CTG", "TTA", "TTG").map(Codon(_))),
    AminoAcid("Val", 'V', "Valine", Seq("GTT", "GTC", "GTA", "GTG").map(Codon(_))),
    AminoAcid("Phe", 'F', "Phenylalanine", Seq("TTT", "TTC").map(Codon(_))),
    AminoAcid("Met", 'M', "Methionine", Seq(Codon.Start)),
    AminoAcid("Cys", 'C', "Cysteine", Seq("TGT", "TGC").map(Codon(_))),
    AminoAcid("Ala", 'A', "Alanine", Seq("GCT", "GCC", "GCA", "GCG").map(Codon(_))),
    AminoAcid("Gly", 'G', "Glycine", Seq("GGT", "GGC", "GGA", "GGG").map(Codon(_))),
    AminoAcid("Pro", 'P', "Proline", Seq("CCT", "CCC", "CCA", "CCG").map(Codon(_))),
    AminoAcid("Thr", 'T', "Threonine", Seq("ACT", "ACC", "ACA", "ACG").map(Codon(_))),
    AminoAcid("Ser", 'S', "Serine", Seq("TCT", "TCC", "TCA", "TCG", "AGT", "AGC").map(Codon(_))),
    AminoAcid("Tyr", 'Y', "Tyrosine", Seq("TAT", "TAC").map(Codon(_))),
    AminoAcid("Trp", 'W', "Tryptophan", Seq("TGG").map(Codon(_))),
    AminoAcid("Gln", 'Q', "Glutamine", Seq("CAA", "CAG").map(Codon(_))),
    AminoAcid("Asn", 'N', "Asparagine", Seq("AAT", "AAC").map(Codon(_))),
    AminoAcid("His", 'H', "Histidine", Seq("CAT", "CAC").map(Codon(_))),
    AminoAcid("Glu", 'E', "Glutamic acid", Seq("GAA", "GAG").map(Codon(_))),
    AminoAcid("Asp", 'D', "Aspartic acid", Seq("GAT", "GAC").map(Codon(_))),
    AminoAcid("Lys", 'K', "Lysine", Seq("AAA", "AAG").map(Codon(_))),
    AminoAcid("Arg", 'R', "Arginine", Seq("CGT", "CGC", "CGA", "CGG", "AGA", "AGG").map(Codon(_))))

  val ByCode = All.map(aa => (aa.code, aa)).toMap

  val BySingleLetter = All.map(aa => (aa.letter, aa)).toMap

  val ByCodon = All.flatMap(aa => aa.codons.map((_, aa))).toMap
}