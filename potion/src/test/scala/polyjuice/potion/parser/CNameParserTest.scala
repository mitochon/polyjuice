package polyjuice.potion.parser

import org.scalatest.{ FunSpec, Matchers }

import polyjuice.potion.model._

class CNameParserTest extends FunSpec with Matchers {

  describe("cname parser") {

    it("parses substitutions") {
      CNameParser.parse("123C>T").get shouldEqual CdsSub(123, Base.C, Base.T)
    }
  }
}