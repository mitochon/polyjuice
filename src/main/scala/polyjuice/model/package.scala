package polyjuice

package object model {
  type Transcript = String
  type Gene = Map[Transcript, EnsemblGene]

  val emptyGene = Map[String, EnsemblGene]()
}