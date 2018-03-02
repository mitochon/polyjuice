package polyjuice.model

sealed trait Base {
  def letter: Char
  def complement: Base
  override def toString: String = s"$letter"
}

object Base {

  case object A extends Base {
    override val letter: Char = 'A'
    override val complement: Base = T
  }

  case object C extends Base {
    override val letter: Char = 'C'
    override val complement: Base = G
  }

  case object T extends Base {
    override val letter: Char = 'T'
    override val complement: Base = A
  }

  case object G extends Base {
    override val letter: Char = 'G'
    override val complement: Base = C
  }

  case object N extends Base {
    override val letter: Char = 'N'
    override val complement: Base = N
  }

  @throws[Exception]
  def apply(base: Char): Base = {
    base match {
      case A.letter | 'a' => A
      case C.letter | 'c' => C
      case T.letter | 't' => T
      case G.letter | 'g' => G
      case N.letter | 'n' => N
      case _              => throw new IllegalArgumentException("No matching base for " + base)
    }
  }
}