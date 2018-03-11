package polyjuice.potion

package object model {
  type Transcript = String
  type GeneSymbol = String
  type Gene = Map[Transcript, EnsemblGene]

  val emptyGene = Map[String, EnsemblGene]()
}