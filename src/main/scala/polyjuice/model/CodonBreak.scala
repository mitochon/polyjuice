package polyjuice.model

sealed trait CodonBreak {
  def distance: Int
  def flip: CodonBreak
}

case class SplitAtFirst(distance: Int) extends CodonBreak {
  def flip: CodonBreak = SplitAtSecond(distance)
}

case class SplitAtSecond(distance: Int) extends CodonBreak {
  def flip: CodonBreak = SplitAtFirst(distance)
}
