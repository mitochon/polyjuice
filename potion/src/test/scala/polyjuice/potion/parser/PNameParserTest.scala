package polyjuice.potion.parser


import polyjuice.potion.model._
import polyjuice.potion.model.AminoAcid.Code
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PNameParserTest extends AnyFunSpec with Matchers {

  describe("pname parser") {

    it("parses substitution") {
      PNameParser.parse("Arg54Ser").get shouldEqual ProteinSub(54, Code.Arg, Code.Ser)
      PNameParser.parse("p.T790M") shouldEqual PNameParser.parse("p.Thr790Met")
    }

    it("parses deletion") {
      PNameParser.parse("Ala3del").get shouldEqual ProteinDel(3, Code.Ala, None, None)
      PNameParser.parse("Cys76_Glu79del").get shouldEqual ProteinDel(76, Code.Cys, Some(79), Some(Code.Glu))
      PNameParser.parse("p.Ala3del") shouldEqual PNameParser.parse("A3del")
      PNameParser.parse("Cys76_Glu79del") shouldEqual PNameParser.parse("p.C76_E79del")
    }

    it("parses duplication") {
      PNameParser.parse("Ala3dup").get shouldEqual ProteinDup(3, Code.Ala, None, None)
      PNameParser.parse("Cys76_Glu79dup").get shouldEqual ProteinDup(76, Code.Cys, Some(79), Some(Code.Glu))
      PNameParser.parse("p.Ala3dup") shouldEqual PNameParser.parse("A3dup")
      PNameParser.parse("Cys76_Glu79dup") shouldEqual PNameParser.parse("p.C76_E79dup")
    }

    it("parses insertion") {
      PNameParser.parse("Lys2_Gly3insGlnSerLys").get shouldEqual
        ProteinIns(2, Code.Lys, 3, Code.Gly, Seq(Code.Gln, Code.Ser, Code.Lys))
      PNameParser.parse("Lys2_Gly3insGlnSerLys") shouldEqual PNameParser.parse("p.K2_G3insQSK")

      PNameParser.parse("Met3_His4insGlyTer").get shouldEqual
        ProteinIns(3, Code.Met, 4, Code.His, Seq(Code.Gly, Code.Ter))
      PNameParser.parse("Met3_His4insGlyTer") shouldEqual PNameParser.parse("M3_H4insG*")
    }

    it("parses indel") {
      PNameParser.parse("Arg123delinsSerAsp").get shouldEqual
        ProteinDelIns(123, Code.Arg, None, None, Seq(Code.Ser, Code.Asp))
      PNameParser.parse("Arg123delinsSerAsp") shouldEqual PNameParser.parse("R123delinsSD")

      PNameParser.parse("Arg123_Lys127delinsSerAsp").get shouldEqual
        ProteinDelIns(123, Code.Arg, Some(127), Some(Code.Lys), Seq(Code.Ser, Code.Asp))
      PNameParser.parse("Arg123_Lys127delinsSerAsp") shouldEqual PNameParser.parse("R123_K127delinsSD")
    }

    it("parses frameshift") {
      PNameParser.parse("Arg123fs").get shouldEqual ProteinFrameshift(123, Code.Arg)
      PNameParser.parse("Arg123fs") shouldEqual PNameParser.parse("R123fs")
    }
  }
}