package polyjuice.tracer

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.model._

class CodonTracerTest extends FunSpec with Matchers {

  describe("codon tracer") {

    val utr5 = Some(UTR5("123", 1, 4))
    val utr3 = Some(UTR3("123", 47, 50))

    val ensemblGene = EnsemblGene("g", "123", "1", 1, 50, Strand.Plus, utr5, utr3, Seq(), "ACTG")
    val emptyExon = Exon("e1", "123", 1, "1", 0, 0, None, None)

    it("seek produces the right offset") {
      val e1 = emptyExon.copy(start = 1, end = 10)
      val e2 = emptyExon.copy(start = 20, end = 35)
      val e3 = emptyExon.copy(start = 45, end = 50)
      val g = ensemblGene.copy(exons = Seq(e1, e2, e3))

      CodonTracer.seek(g, 1) shouldEqual Some(Offset(e1, 5))
      CodonTracer.seek(g, 2) shouldEqual Some(Offset(e1, 8))
      CodonTracer.seek(g, 3) shouldEqual Some(Offset(e2, 1))
      CodonTracer.seek(g, 8) shouldEqual Some(Offset(e2, 16))
    }
  }
}