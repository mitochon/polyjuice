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

  @throws[Exception]
  def apply(base: Char): Base = {
    base match {
      case A.letter => A
      case C.letter => C
      case T.letter => T
      case G.letter => G
      case _        => throw new IllegalArgumentException("No matching base for " + base)
    }
  }
}