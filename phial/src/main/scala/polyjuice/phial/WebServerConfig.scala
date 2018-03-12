package polyjuice.phial

import java.nio.file.Paths

import com.typesafe.config.ConfigFactory

object WebServerConfig {

  private[this] val config = ConfigFactory.load()

  val GeneList = config.getStringList("hgncGenes")
  val EnsemblCdsFastaPath = Paths.get(config.getString("ensembl.cdsFastaPath"))
  val EnsemblFeatureGff3Path = Paths.get(config.getString("ensembl.featureGff3Path"))
}
