package polyjuice

package object io {
  type Line[A] = Either[Throwable, A]

  def lineFilter[A](line: Line[A], fn: A => Boolean): Boolean = {
    line.isLeft || line.isRight && line.forall(fn)
  }
}
