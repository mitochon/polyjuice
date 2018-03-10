package polyjuice.model

sealed trait CodonBreak {
  def distance: Int
  def offset: Int
  def flip: CodonBreak
}

case class SplitAtFirst(distance: Int) extends CodonBreak {
  override def flip: CodonBreak = SplitAtSecond(distance)
  override val offset: Int = 1
}

case class SplitAtSecond(distance: Int) extends CodonBreak {
  override def flip: CodonBreak = SplitAtFirst(distance)
  override val offset: Int = 2
}
