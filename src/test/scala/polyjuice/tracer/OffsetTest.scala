package polyjuice.tracer

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.model._

class OffsetTest extends FunSpec with Matchers {

  describe("offset") {

    it("calculates codon break based on position") {
      val e = Exon("e1", "123", 1, "1", 1, 10, None, None)

      Offset(e, 1).single(Base.A) shouldEqual Single("1", 1, Base.A)
      Offset(e, 8).triple(Codon("ATG")) shouldEqual Triple("1", 8, Codon("ATG"), None)
      Offset(e, 9).triple(Codon("ATG")) shouldEqual Triple("1", 9, Codon("ATG"), Some(CodonBreak._2))
      Offset(e, 10).triple(Codon("ATG")) shouldEqual Triple("1", 10, Codon("ATG"), Some(CodonBreak._1))
    }
  }
}