package polyjuice.model

sealed trait CodonBreak {
  def distance: Int
}

case class SplitAtFirst(distance: Int) extends CodonBreak

case class SplitAtSecond(distance: Int) extends CodonBreak
