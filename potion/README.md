# Polyjuice Potion

This module contains the core elements of Polyjuice and is intended to have no external dependencies.

It is organized into the following packages:

* **io** - for reading Ensembl files.
* **model** - for modeling genomic data.
* **parser** - for parsing HGVS strings.
* **tracer** - for looking up bases, coordinates, and building variants.
* **vcf** - for creating and manipulating VCF files.

## Getting Started

Checkout the project and run a console:

```
sbt potion/console
```

Here are the imports needed to run the subsequent examples:

```scala
import polyjuice.potion.io._
import polyjuice.potion.model._
import polyjuice.potion.tracer._
import java.nio.file._
```

There are a few files under the [test resources](src/test/resources) directory, e.g. EGFR (forward strand) and BRAF (reverse strand). Get handle on the base directory and load a gene:

```scala
val baseDir = Paths.get(getClass.getResource("/").toURI.resolve("../test-classes"))
val gff3Path = baseDir.resolve("egfr.gff3.gz")
val fastaPath = baseDir.resolve("egfr.cds.fa.gz")
val geneReader = EnsemblGeneReader.getGene("EGFR", fastaPath, gff3Path)
val gene = geneReader.right.get
```

### CodingSequenceTracer

This tracer gets data for a particular coding sequence location.

```scala
val codingSequenceTracer = CodingSequenceTracer(gene)
```

Example: show the base at position 100 for transcript _ENST00000455089_.

```scala
codingSequenceTracer.cds(100, "ENST00000455089")
//> res0: Option[polyjuice.potion.model.Base] = Some(A)
```

Example: show coordinates at position 1000 for all known transcripts. A _Single_ is a coordinate for a single base.

```scala
codingSequenceTracer.coord(1000).foreach(println)
//> (ENST00000455089,Single(7,55156760,G))
//| (ENST00000420316,Single(7,55155940,C))
//| (ENST00000638463,Single(7,55161618,A))
//| ... etc
```

### CodonTracer

This tracer gets data for a particular codon location.

```scala
val codonTracer = CodonTracer(gene)
```

Example: show the codon at position 790 for transcript _ENST00000455089_.

```scala
codonTracer.codon(790, "ENST00000455089")
//> res0: Option[polyjuice.potion.model.Codon] = Some(Codon(C,A,C))
```

Example: show the amino acid for the same position.

```scala
codonTracer.aminoAcid(790, "ENST00000455089")
//> res1: Option[polyjuice.potion.model.AminoAcid] = Some(AminoAcid(His,H,Histid
//| ine,Set(Codon(C,A,T), Codon(C,A,C))))
```

Example: show the coordinates at position 858 for all transcripts. A _Triple_ is a coordinate for a codon.

```scala
codonTracer.coord(858).foreach(println)
//> (ENST00000455089,Triple(7,55198722,Codon(A,C,T),None))
//| (ENST00000275493,Triple(7,55191821,Codon(C,T,G),None))
//| (ENST00000454757,Triple(7,55198722,Codon(A,C,T),None))
```

Example: some codons may be split by an intron. At position 30 the _GTT_ codon is split after _G_ with an intron of size 122921.

```scala
codonTracer.coord(30).foreach(println)
//> (ENST00000454757,Triple(7,55019365,Codon(G,T,T),Some(SplitAtFirst(122921))))
//| (ENST00000342916,Triple(7,55019365,Codon(G,T,T),Some(SplitAtFirst(122921))))
//| ...
```

### CdsVariantTracer

This tracer gets data for a particular _c.name_ variant.

```scala
val cdsVariantTracer = CdsVariantTracer(gene)
```

Example: show all transcripts where there's a substitution at position 13 from _A_ to _T_ that matches the original base of _A_. If no transcripts match the base _A_ at position 13, an empty map would be returned.

```scala
cdsVariantTracer.cds(CdsSub(13, Base.A, Base.T))
//> res0: Map[polyjuice.potion.model.Transcript,polyjuice.potion.model.Snv] = Ma
//| p(ENST00000638463 -> Snv(7,55152548,A,T))
```

Other than substitution, these types of variants are also supported: deletion, duplication, insertion, inversion, deletion / insertion combination.

### ProteinVariantTracer

This tracer gets data for a particular _p.name_ variant.

```scala
val proteinVariantTracer = ProteinVariantTracer(gene)
```

Example: show all transcripts where there's a substitution at position 790 from _Thr_ to _Met_ that matches the original amino acid of _Thr_. If no transcripts match the amino acid _Thr_ at position 790, an empty map would be returned.

```scala
proteinVariantTracer.aminoAcid(ProteinSub(790, AminoAcid.Code.Thr, AminoAcid.Code.Met))
//> res0: Map[polyjuice.potion.model.Transcript,Set[polyjuice.potion.model.Varia
//| ntCoord]] = Map(ENST00000275493 -> Set(Snv(7,55181378,C,T)))
```

For protein variant tracer, only substitutions are supported.