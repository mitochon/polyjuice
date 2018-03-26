package polyjuice.phial

import java.nio.file.Paths

import com.typesafe.config.ConfigFactory

object WebServerConfig {

  private[this] val config = ConfigFactory.load()

  val ServicePort = config.getInt("service.port")
  val ServiceHost = config.getString("service.host")
  val GeneList = config.getString("geneList").split(',')
  val EnsemblBuild = config.getString("ensembl.build")
  val EnsemblCdsFastaPath = Paths.get(config.getString("ensembl.cdsFastaPath"))
  val EnsemblFeatureGff3Path = Paths.get(config.getString("ensembl.featureGff3Path"))
}
