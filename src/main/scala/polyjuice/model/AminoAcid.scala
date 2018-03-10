package polyjuice.model

case class AminoAcid(
  val code: AminoAcid.Code.Value,
  val letter: Char,
  val name: String,
  val codons: Set[Codon])

object AminoAcid {

  object Code extends Enumeration {
    val Ile, Leu, Val, Phe, Met, Cys, Ala, Gly, Pro, Thr, Ser, Tyr, Trp, Gln, Asn, His, Glu, Asp, Lys, Arg = Value
  }

  val All = Map(
    Code.Ile -> AminoAcid(Code.Ile, 'I', "Isoleucine", Set("ATT", "ATC", "ATA").map(Codon(_))),
    Code.Leu -> AminoAcid(Code.Leu, 'L', "Leucine", Set("CTT", "CTC", "CTA", "CTG", "TTA", "TTG").map(Codon(_))),
    Code.Val -> AminoAcid(Code.Val, 'V', "Valine", Set("GTT", "GTC", "GTA", "GTG").map(Codon(_))),
    Code.Phe -> AminoAcid(Code.Phe, 'F', "Phenylalanine", Set("TTT", "TTC").map(Codon(_))),
    Code.Met -> AminoAcid(Code.Met, 'M', "Methionine", Set(Codon.Start)),
    Code.Cys -> AminoAcid(Code.Cys, 'C', "Cysteine", Set("TGT", "TGC").map(Codon(_))),
    Code.Ala -> AminoAcid(Code.Ala, 'A', "Alanine", Set("GCT", "GCC", "GCA", "GCG").map(Codon(_))),
    Code.Gly -> AminoAcid(Code.Gly, 'G', "Glycine", Set("GGT", "GGC", "GGA", "GGG").map(Codon(_))),
    Code.Pro -> AminoAcid(Code.Pro, 'P', "Proline", Set("CCT", "CCC", "CCA", "CCG").map(Codon(_))),
    Code.Thr -> AminoAcid(Code.Thr, 'T', "Threonine", Set("ACT", "ACC", "ACA", "ACG").map(Codon(_))),
    Code.Ser -> AminoAcid(Code.Ser, 'S', "Serine", Set("TCT", "TCC", "TCA", "TCG", "AGT", "AGC").map(Codon(_))),
    Code.Tyr -> AminoAcid(Code.Tyr, 'Y', "Tyrosine", Set("TAT", "TAC").map(Codon(_))),
    Code.Trp -> AminoAcid(Code.Trp, 'W', "Tryptophan", Set("TGG").map(Codon(_))),
    Code.Gln -> AminoAcid(Code.Gln, 'Q', "Glutamine", Set("CAA", "CAG").map(Codon(_))),
    Code.Asn -> AminoAcid(Code.Asn, 'N', "Asparagine", Set("AAT", "AAC").map(Codon(_))),
    Code.His -> AminoAcid(Code.His, 'H', "Histidine", Set("CAT", "CAC").map(Codon(_))),
    Code.Glu -> AminoAcid(Code.Glu, 'E', "Glutamic acid", Set("GAA", "GAG").map(Codon(_))),
    Code.Asp -> AminoAcid(Code.Asp, 'D', "Aspartic acid", Set("GAT", "GAC").map(Codon(_))),
    Code.Lys -> AminoAcid(Code.Lys, 'K', "Lysine", Set("AAA", "AAG").map(Codon(_))),
    Code.Arg -> AminoAcid(Code.Arg, 'R', "Arginine", Set("CGT", "CGC", "CGA", "CGG", "AGA", "AGG").map(Codon(_))))

  val ByCode = All.values.map(aa => (aa.code, aa)).toMap

  val BySingleLetter = All.values.map(aa => (aa.letter, aa)).toMap

  val ByCodon = All.values.flatMap(aa => aa.codons.map((_, aa))).toMap
}