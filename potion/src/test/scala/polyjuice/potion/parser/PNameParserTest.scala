package polyjuice.potion.parser

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.potion.model._

class PNameParserTest extends FunSpec with Matchers {

  describe("pname parser") {

    it("parses substitutions") {
      PNameParser.parse("p.T790M").get shouldEqual ProteinSub(790, AminoAcid.Code.Thr, AminoAcid.Code.Met)
    }
  }
}