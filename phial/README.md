# Polyjuice Phial

A [phial](https://en.wiktionary.org/wiki/phial) is a lightweight web service build on top of the Polyjuice core library.

## Usage

Current available as a [service](http://mitochon.me/api/polyjuice/status) though its not guaranteed to be up.

To build manually, see instructions on the [main page](../README.md)


## Endpoints

### Status

GET `/api/polyjuice/status`

Shows basic information about the particular instance, i.e. the Ensembl build and the list of genes that are available.

```json
{
  "ensemblBuild": "GRCh38.build91",
  "geneList": ["ABL1","AKT1","ALK", ...]
}
```

### Gene or Transcript


GET `/api/polyjuice/gene/<gene_symbol>`
GET `/api/polyjuice/transcript/<transcript>`

Gene symbols are not case-sensitive, i.e. _ERBB2_ and _erbb2_ are equivalent.

Transcripts are build agnostic, i.e. _ENST00000479537.5_ and _ENST00000479537_ are equivalent.

```json
{
  "ENST00000554581": {
    "geneSymbol": "AKT1",
    "transcript": "ENST00000554581.5",
    "chr": "14",
    "start": 104769349,
    "end": 104794124,
    "strand": "-",
    "utr5": [...],
    "utr3": [...],
    "exons": [...]
    ],
    "codingSequence": "ATGAGCGACGTG..."
  }
}
```

### Exon

GET `/api/polyjuice/gene/<gene_symbol>/exon/num/<num>`
GET `/api/polyjuice/transcript/<transcript>/exon/num/<num>`

Show information about a particular exon, if it exists

```json
{
  "ENST00000554581": {
    "ensemblId": "ENSE00003619354",
    "transcript": "ENST00000554581",
    "rank": 10,
    "chr": "14",
    "start": 104772878,
    "end": 104773092,
    "phase": "0",
    "endPhase": "2"
  }
}
```

### Codon

GET `/api/polyjuice/gene/<gene_symbol>/codon/pos/<pos>`
GET `/api/polyjuice/transcript/<transcript>/codon/pos/<pos>`

Shows the three bases of a codon in a particular location, if it exists

```json
{
  "ENST00000554581": {
    "first": "A",
    "second": "C",
    "third": "A"
  }
}
```

#### Codon coordinates

GET `/api/polyjuice/gene/<gene_symbol>/codon/coord/<pos>`
GET `/api/polyjuice/transcript/<transcript>/codon/coord/<pos>`

Shows the three bases of a codon in absolute genomic coordinates, taking the gene strand into account.

```json
{
  "ENST00000269571": {
    "contig": "17",
    "pos": 39700266,
    "bases": {
      "first": "G",
      "second": "G",
      "third": "G"
    },
    "break": null
  }
}
```

The _break_ field indicates if there's an intron between the codon bases, and the distance to the next base, e.g.

```json
{
  "ENST00000541774": {
    "contig": "17",
    "pos": 39699587,
    "bases": {
      "first": "G",
      "second": "T",
      "third": "G"
    },
    "break": {
      "SplitAtFirst": {
        "distance": 7403
      }
    }
  }
}
```

In this example the codon is split between the first and second base, and the distance between the first and second base is _7403_ base pairs. Possible values for the _break_ are _SplitAtFirst_ and _SplitAtSecond_.

### Coding sequence

GET `/api/polyjuice/gene/<gene_symbol> /cds/pos/<pos>`
GET `/api/polyjuice/transcript/<transcript>/cds/pos/<pos>`

Shows the coding sequence at a particular location, if available

```json
{
  "ENST00000269571": "G",
  "ENST00000578709": "C"
}
```

#### Coding sequence coordinates

GET `/api/polyjuice/gene/<gene_symbol> /cds/coord/<pos>`
GET `/api/polyjuice/transcript/<transcript>/cds/coord/<pos>`

Shows the coding sequence location in absolute genomic coordinates, taking the gene strand into account.

```json
{
  "ENST00000269571": {
    "contig": "17",
    "pos": 39700248,
    "base": "G"
  }
}
```

### HGVS

GET `/api/polyjuice/gene/<gene_symbol>/hgvs/<hgvs>`
GET `/api/polyjuice/transcript/<transcript>/hgvs/<hgvs>`

Show possible variants for a given HGVS string for [protein](http://varnomen.hgvs.org/recommendations/protein/) or [DNA coding sequence](http://varnomen.hgvs.org/recommendations/DNA/).

Protein HGVS must start with **p.** whereas DNA HGVS must start with a **c.**.

Only a limited types of substitutions are currently supported:
  * Notations involving intronic regions and UTR regions are not supported.
  * Protein HGVS only supports simple substitutions.
  * DNA coding sequence HGVS does not support conversions.

#### Syntax Checker

GET `/api/polyjuice/hgvscheck/<hgvs>`

Checks if a HGVS string syntax is valid. Note this is currently Work-In-Progress.

For p.A11Q

```json
{
  "ProteinSub": {
    "pos": 11,
    "from": "Ala",
    "to": "Gln"
  }
}
```

For c.123_127delinsAG

```json
{
  "CdsDelIns": {
    "start": 123,
    "end": 127,
    "bases": [
      "A",
      "G"
    ]
  }
}
```

#### VCF Output

POST `/api/polyjuice/hgvs2vcf`

##### Request Body
| Parameter | Type | Description |
|-----------|------|-------------|
| entries | List of HGVS entry objects | Mutations of interest. |
| appendInfoFields | List of VCF key builder objects | Extra __INFO__ fields. Optional. |
| appendFormatFields | List of VCF key builder objects | Extra __FORMAT__ fields. Optional. |
| oneVariantPerTranscript | Boolean | Pick one variant per transcript. Optional. Defaults to false. |
| vcfFileFormat | String | __fileformat__ per [VCF specifications](https://samtools.github.io/hts-specs/VCFv4.3.pdf), e.g. __4.3__. |
| addChrPrefix | Boolean | Add 'chr' prefix to contigs. Optional. Defaults to false. |

##### HGVS entry object

| Parameter | Type | Description |
|-----------|------|-------------|
| gene | String | Gene symbol. Optional. |
| transcript | String | Gene transcript. Takes precedence over _gene_ if defined. Optional. |
| hgvs | String | HGVS string (p. / c.) |

Either __gene__ or __transcript__ must be defined.

##### VCF key builder object

| Parameter | Type | Description |
|-----------|------|-------------|
| id | String | ID field VCF header. |
| number | String | Should follow [VCF specifications](https://samtools.github.io/hts-specs/VCFv4.3.pdf). |
| dataType | String | Should follow [VCF specifications](https://samtools.github.io/hts-specs/VCFv4.3.pdf). |
| description | String | Header field description. |
| value | String | The static value to be appended. |

Sample request body

```json
{
	"entries": [
		{
			"gene": "EGFR",
			"hgvs": "p.T790M"
		},
		{
			"gene": "EGFR",
			"hgvs": "p.L858R"
		}q
	],
	"appendInfoFields": [
		{
			"id": "DP",
			"number": "1",
			"dataType": "IntegerType",
			"description": "Depth",
			"value": "1000"
		},
		{
			"id": "ALTDP",
			"number": "1",
			"dataType": "IntegerType",
			"description": "Allele Count",
			"value": "100"
		}
	],
	"appendFormatFields": [
		{
			"id": "GT",
			"number": "1",
			"dataType": "StringType",
			"description": "Genotype",
			"value": "0/1"
		}
	],
	"addChrPrefix": true,
	"oneVariantPerTranscript": true,
	"vcfFileFormat": "4.2"
}
```

Sample response

`Content-Type text/plain; charset=UTF-8`

```
##fileformat=VCFv4.2
##INFO=<ID=TR,Number=1,Type=String,Description="Transcript">
##INFO=<ID=HGVS,Number=1,Type=String,Description="HGVS string">
##INFO=<ID=DP,Number=1,Type=Integer,Description="Depth">
##INFO=<ID=ALTDP,Number=1,Type=Integer,Description="Allele Count">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##PolyJuiceVersion="0.1.0-SNAPSHOT"
##EnsemblBuild="GRCh38.build91"
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	SAMPLE0
chr7	55181378	.	C	T	.	.	TR=ENST00000275493;HGVS=EGFR_T790M;DP=1000;ALTDP=100	GT	0/1
chr7	55191822	.	TG	GC	.	.	TR=ENST00000275493;HGVS=EGFR_L858R;DP=1000;ALTDP=100	GT	0/1
```