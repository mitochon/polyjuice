package polyjuice.potion.tracer


import polyjuice.potion.model._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CodingSequenceTracerTest extends AnyFunSpec with Matchers {

  describe("coding sequence tracer") {

    val utr5 = IndexedSeq(UTR5("123", 1, 4))
    val utr3 = IndexedSeq(UTR3("123", 47, 50))

    val ensemblGene = EnsemblGene("g", "123", "1", 1, 50, Strand.Plus, utr5, utr3, IndexedSeq(), "ACTG")
    val emptyExon = Exon("e1", "123", 1, "1", 0, 0, None, None)

    it("seek produces the right offset") {
      val e1 = emptyExon.copy(start = 1, end = 10)
      val e2 = emptyExon.copy(start = 20, end = 35)
      val e3 = emptyExon.copy(start = 45, end = 50)
      val g = ensemblGene.copy(exons = IndexedSeq(e1, e2, e3))

      CodingSequenceTracer.seek(g, 1) shouldEqual Some(Offset(e1, 5, Strand.Plus, Some(10)))
      CodingSequenceTracer.seek(g, 10) shouldEqual Some(Offset(e2, 4, Strand.Plus, Some(10)))
      CodingSequenceTracer.seek(g, 23) shouldEqual Some(Offset(e3, 1, Strand.Plus))
      CodingSequenceTracer.seek(g, 24) shouldEqual Some(Offset(e3, 2, Strand.Plus))
      CodingSequenceTracer.seek(g, 25) shouldEqual None
    }
  }
}