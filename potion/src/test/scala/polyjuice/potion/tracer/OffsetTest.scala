package polyjuice.potion.tracer

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.potion.model._

class OffsetTest extends FunSpec with Matchers {

  describe("offset") {

    it("calculates codon break based on position for forward strands") {
      val e1 = Exon("e1", "123", 1, "1", 1, 10, None, Some(CodonPhase._1))
      val e2 = Exon("e2", "123", 1, "2", 13, 15, Some(CodonPhase._1), None)

      Offset(e1, 1, Strand.Plus, Some(3)).single(Base.A) shouldEqual Single("1", 1, Base.A)
      Offset(e1, 8, Strand.Plus, Some(3)).triple(Codon("ATG")) shouldEqual Triple("1", 8, Codon("ATG"), None)
      Offset(e1, 9, Strand.Plus, Some(3)).triple(Codon("ATG")) shouldEqual Triple("1", 9, Codon("ATG"), Some(SplitAtSecond(3)))
      Offset(e1, 10, Strand.Plus, Some(3)).triple(Codon("ATG")) shouldEqual Triple("1", 10, Codon("ATG"), Some(SplitAtFirst(3)))
    }
  }
}