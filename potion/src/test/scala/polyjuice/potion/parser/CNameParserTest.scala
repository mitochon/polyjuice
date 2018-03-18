package polyjuice.potion.parser

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.potion.model._

class CNameParserTest extends FunSpec with Matchers {

  describe("cname parser") {

    it("parses substitution") {
      CNameParser.parse("123C>T").get shouldEqual CdsSub(123, Base.C, Base.T)
    }

    it("parses deletion") {
      CNameParser.parse("c.123del").get shouldEqual CdsDel(123, None, None)
      CNameParser.parse("c.123delT").get shouldEqual CdsDel(123, None, Some(Seq(Base.T)))
      CNameParser.parse("123_134del").get shouldEqual CdsDel(123, Some(134), None)
      CNameParser.parse("123_134delAC").get shouldEqual CdsDel(123, Some(134), Some(Seq(Base.A, Base.C)))
      CNameParser.parse("c.123_123del") shouldEqual None
    }

    it("parses duplication") {
      CNameParser.parse("c.123dup").get shouldEqual CdsDup(123, None, None)
      CNameParser.parse("c.123dupA").get shouldEqual CdsDup(123, None, Some(Seq(Base.A)))
      CNameParser.parse("123_134dup").get shouldEqual CdsDup(123, Some(134), None)
      CNameParser.parse("123_134dupCG").get shouldEqual CdsDup(123, Some(134), Some(Seq(Base.C, Base.G)))
    }

    it("parses inversion") {
      CNameParser.parse("c.123_124inv").get shouldEqual CdsInv(123, 124)
    }

    it("parses insertion") {
      CNameParser.parse("c.123_125insAC").get shouldEqual CdsIns(123, 125, Seq(Base.A, Base.C))
    }

    it("parses indel") {
      CNameParser.parse("123delinsGC").get shouldEqual CdsDelIns(123, None, Seq(Base.G, Base.C))
    }
  }
}