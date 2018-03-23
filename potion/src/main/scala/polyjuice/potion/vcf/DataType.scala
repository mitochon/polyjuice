package polyjuice.potion.vcf

object DataType extends Enumeration {
  val IntegerType, FlagType, StringType, CharacterType, FloatType = Value

  def toString(d: DataType.Value): String = {
    d match {
      case IntegerType   => "Integer"
      case FlagType      => "Flag"
      case FloatType     => "Float"
      case CharacterType => "Character"
      case _             => "String"
    }
  }
}
