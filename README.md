# Polyjuice

Polyjuice is a tool for exploring genomic polymorphisms.

The name is also a tribute to the awesome [Polyjuice Potion](http://pottermore.wikia.com/wiki/Polyjuice_Potion) in the Harry Potter series.

Whereas the real potion is brewed with traces of a person, such as hair strands or toenail clippings to create a temporary imperfect copy of that person; this library takes in traces of a mutation, such as HGVS strings, to create imperfect guesses of possible genomic coordinates.


## Overview

Polyjuice has a [library module](potion) and a [web service module](phial). It can answers questions like:
  * For a gene, what are the exons coordinates ?
  * For a gene, what base is in some coding sequence position __P__ across different transcripts ?
  * For a gene, what codon is in position __P__ across different transcripts ?
  * For a gene and an HGVS string, what are possible genomic coordinates ?

And it can generate a VCF file from a set of HGVS strings.

All the *gene* endpoints have a corresponding *transcript* endpoints. The difference is the *gene* endpoint will show results for all matching transcripts.


## Limitations

This tool only handles coding regions for cds' and codons. The following cases are NOT handled:
  * Mutations crossing intronic regions and UTR regions.
  * For [DNA](http://varnomen.hgvs.org/recommendations/DNA/) coding sequence: conversions, copy number variations, allele combinations, complex mutations.
  * For [Protein](http://varnomen.hgvs.org/recommendations/protein/): anything but simple substitutions.


## Related work

Some other tools that deal with transcripts and HGVS notations:

  * [Ensembl REST API](http://rest.ensembl.org)
  * [Mutalyzer](https://www.mutalyzer.nl)
  * [Transvar](https://github.com/zwdzwd/transvar)
  * [Biocommons-HGVS](https://github.com/biocommons/hgvs)
  * [LitVar](https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/LitVar)

## Usage

The tool requires two files from [Ensembl](https://uswest.ensembl.org/info/data/ftp/index.html) - the CDS FASTA file and the GFF3 file.

See the [web service module](phial) for more information on how to use the service endpoints.


### Building manually

  * Download the two files from Ensembl
  * Install [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) and [`sbt`](https://www.scala-sbt.org/download.html)
  * Build an application jar

```
sbt assembly
```

  * Create a properties file with the following properties specified, for example:

```
service.port=8080
service.host=localhost
ensembl.build=GRCh39.build91
geneList=EGFR,BRAF,ERBB2
ensembl.cdsFastaPath=/path/to/cds.fa.gz
ensembl.featureGff3Path=/path/to/gff3.gz
```

  * Run the application

```
java -jar -Xmx128m -Dconfig.file=/path/to/file.properties /path/to/assembly.jar
```
