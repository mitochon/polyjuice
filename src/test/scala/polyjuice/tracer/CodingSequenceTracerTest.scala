package polyjuice.tracer

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.model._
import polyjuice.tracer.CodingSequenceTracer.Offset

class CodingSequenceTracerTest extends FunSpec with Matchers {

  describe("coding sequence tracer") {

    val utr5 = Some(UTR(UTRType.five_prime_UTR, "123", 1, 4))
    val utr3 = Some(UTR(UTRType.five_prime_UTR, "123", 47, 50))

    val ensemblGene = EnsemblGene("g", "123", "1", 1, 50, Strand.Plus, utr5, utr3, Seq(), "ACTG")
    val emptyExon = Exon("e1", "123", 1, "1", 0, 0, None, None)

    it("seek produces the right offset") {
      val e1 = emptyExon.copy(start = 1, end = 10)
      val e2 = emptyExon.copy(start = 20, end = 35)
      val e3 = emptyExon.copy(start = 45, end = 50)
      val g = ensemblGene.copy(exons = Seq(e1, e2, e3))

      CodingSequenceTracer.seek(g, 1) shouldEqual Some(Offset(e1, 5))
      CodingSequenceTracer.seek(g, 10) shouldEqual Some(Offset(e2, 4))
      CodingSequenceTracer.seek(g, 23) shouldEqual Some(Offset(e3, 1))
      CodingSequenceTracer.seek(g, 24) shouldEqual Some(Offset(e3, 2))
      CodingSequenceTracer.seek(g, 25) shouldEqual None
    }
  }
}