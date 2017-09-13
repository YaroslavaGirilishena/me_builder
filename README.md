# Complete computational sequence characterization of mobile element variations in the human genome using meta-personal genome data


The developed tool is the first system that provides full sequence characterization of insertional structural variants in human genomes. It is capable of characterizing short mobile element insertions with a 92% accuracy. Overall, the characterization success rate for Alu is 75.03% with the mean of discordant and split-reads higher than 94 reads. For SVA, it is 71.43% with the threshold of 363 reads. And for L1 the values are 77.78% and 355 respectively.
The SV characterization depends on the allele frequency and is influenced by the repetitiveness of flanking regions. Therefore, addressing these problems is a key to further improvements.

Our method is based on collecting discordant, concordant and split-reads from all publicly available human genome data that cover breakpoints and an insertion. These reads are validated based on quality filters and patterns. Then, they are assembled into contigs using local de novo sequence assembly, and the contigs are merged from both sides of the insertion until they overlap. The information about a particular MEI is extracted from the alignment to the consensus, and TSD is deduced. In case of a successful characterization, the full insertion sequence is reported, otherwise, at least the breakpoint sequences are obtained.

# Third-party tools 
There is a list of required third-party tools that have to be installed before running the system. The paths to the executable files to each third-party tool have to be provided as input as described further. 

1. SAMtools is essential for processing .bam files and collecting reads. To download and install the tool, follow the link: https://github.com/samtools/samtools. 
2. CD-HIT is used to remove redundant reads before the assembly by running the CD-HIT-EST program. Follow the link to download the tool: https://github.com/weizhongli/cdhit. 
3. CAP3 tool is used to assemble the reads into contigs. The link to download: http://seq.cs.iastate.edu/cap3.html. 
4. BLAST program blastn is integrated to perform the alignment of the contigs to the consensus database. To download and install the tool, follow the link: https://blast.ncbi.nlm.nih.gov/Blast.cgi?PAGE_TYPE=BlastDocs&DOC_TYPE=Download and https://www.ncbi.nlm.nih.gov/books/NBK279671/. 
5. bl2seq program does the all-against-all pairwise alignment of contigs which is necessary for the "bridge" assembly of the insertion sequence. The program is distributed with the blastall program by the NCBI. The link to download is: https://blast.ncbi.nlm.nih.gov/Blast.cgi. 

# How to run 
The minimum required heap size that has to be allocated to run the system is 5 GB. This value depends on the size of a .bam file that contains raw reads. 5 GB was enough to process a 70 GB .bam file. 
The command to execute the program is: 

java -Xmx5g -d64 -jar MEBuilder.jar 

Where -Xmx5g is a parameter for memory allocation, and MEBuilder.jar is the executable file. 
For more information about Java command line execution, please refer to http://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html 

# Input 
The list of the required and optional input parameters is: 

1. -ME - typeofMEI. One of Alu/SVA/L1.  
2. -c - chromosome name - chr1-chr22, chrX, chrY. The parameter is used for running the system for one particular loci. It is an optional parameter, but is required when an input file with locations is not provided.  
3. -p - position in the chromosome, can take values from the range [0, 2^31-1]. It is an optional parameter, but is required when an input file with locations is not provided.  
4. -i - input file with MEI locations. The acceptable file formats are .bed or .vcf. The parameter is required, but can be substituted by specifying chromosome and position parameters.  
5. -min ins length - the minimum length of insertion alignment to the consensus database. The input values must be in the range [50, 200].

6. -BAMpath - full path to the folder with BAM files. Required. Can be substituted by -BAMfile when only one .bam is needed. For example, /wgs/human/BAM.  
7. -BAMfile - full BAM file path. Optional, if -BAMpath is specified. For example, /data/bam/ACB70G p10.bam. The .bam files have to be indexed ahead, and the indexed ACB70Gp10.bai file should also be present in the same directory.  
8. -SAMTOOLSpath - path to SAMtools executable. Required. For example, /usr/local/bin.  
9. -BLASTpath - path for blastn executable, required. For example, /usr/local/bin.  
10. -BLASTdb - full path to the blast database, required. The consensus database has to be in a FASTA format following the template: each sequence has a description (i.e. name of subtype) line above that starts with the «>» symbol. Then, the actual sequence is provided. There are no extra spaces or symbols between sequences. Before working with the consensus database, it will be indexed automatically.  
11. -BL2SEQpath - path to bl2seq executable, required. For example, /usr/local/bin.  
12. -CDHITPath - path to CD-HIT-EST executable, required. For example,  /usr/local/bin.  
13. -CAP3path - path to CAP3 executable, required. For example, /usr/local/bin.  
14. -startLoci - start location in the list of all events’ locations, optional.  
15. -endLoci - end location in the list of all events’ locations, optional.  
16. -config - configuration file with all input parameters described above. This parameter is optional, but if used, has to be the first and the only one. The file has to be in a .properties format. The template is included in the package.  

# Extra requirements 
The system creates intermediate output that is used by later steps. For that reason, a minimum of 2 GB extra space has to be provided. 
The intermediate output contains such directories: 

1. disc_reads/ folder that contains qualified reads in a FASTA format and the BAM folder with raw data of qualified reads in .txt format for studying purposes. To speed up the process when running the tool a second time do not delete this folder.  
2. intermediate_output/cap3_assembly folder contains the output from running CAP3 tool.  
3. intermediate_output/contigs_for_merging folder contains valid contigs parsed into separate files.  
4. intermediate_output/bl2seq_output folder stores pairwise alignments data for all contigs pairs for each location.  
5. intermediate_output/bl2seq_output flanking folder contains the alignment data of contigs to the reference genome (specifically to the region of interest - 600 bp before and after the event point).  
6. intermediate_output/merged_contigs folder stores the intermediate merged sequences of contigs and flanking regions.  
7. intermediate_output/ref_flanking directory stores the reference genome sequence of the region of interest (600 bp before and after the insertion point).  
8. intermediate_output/tsd_alignment folder contains the alignment data for TSD calculation.  
9. log/ where all logs are saved.  
10. results/ where all results are stored based on type, location and status: characterized, partially characterized (has a gap in the middle which is represented by Ns), and failed. It is best to remove or clean this folder before running the tool again.  
Inside each of the described folders, except for disc reads, each type has its own directory and each location has its own folder as well. 

# Package 
In the additional package, some input data is included such as: 

1. The consensus database for covered types - /com/yg/input/consensus.
2. The .bed files with locations for each type - /com/yg/input/data1KP/ and /com/yg/input/non_reference. 
3. The collected discordant and split-reads for input locations - /disc_reads. 
4. The version of the reference genome that was used - /com/yg//input/ref/hg19. 
