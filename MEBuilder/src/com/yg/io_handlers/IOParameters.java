package com.yg.io_handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.yg.exceptions.InputParametersException;

/**
 * This class contains all parameters and paths for running tools
 * 
 * @author Yaroslava Girilishena
 *
 */
public class IOParameters {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static final boolean TEST_MODE = true;


	// INPUT DATA
	
	// MEI types and data
	public static final List<String> SUPPORTED_TYPES = new ArrayList<String>(Arrays.asList("Alu", "SVA", "L1")); //', "HERVK", "LTR5_Hs", "PSG"));
	
	public static String ME_TYPE = "L1";
	public static final Map<String, Integer> MIN_MEI_LENGTH = new HashMap<String, Integer>();
	static {
		MIN_MEI_LENGTH.put("Alu", 100);
		MIN_MEI_LENGTH.put("SVA", 50);
		MIN_MEI_LENGTH.put("L1", 70);
		MIN_MEI_LENGTH.put("HERVK", 100);
		MIN_MEI_LENGTH.put("LTR5_Hs", 100);
		MIN_MEI_LENGTH.put("PSG", 100);
	}
	
	public static final Map<String, Integer> MIN_CONSENSUS_IDENT = new HashMap<String, Integer>();
	static {
		MIN_CONSENSUS_IDENT.put("Alu", 90);
		MIN_CONSENSUS_IDENT.put("SVA", 80);
		MIN_CONSENSUS_IDENT.put("L1", 80);
		MIN_CONSENSUS_IDENT.put("HERVK", 100);
		MIN_CONSENSUS_IDENT.put("LTR5_Hs", 100);
		MIN_CONSENSUS_IDENT.put("PSG", 100);
	}
	
	public static final Map<String, Integer> MIN_CONTIG_LENGTH = new HashMap<String, Integer>();
	static {
		MIN_CONTIG_LENGTH.put("Alu", 100);
		MIN_CONTIG_LENGTH.put("SVA", 150);
		MIN_CONTIG_LENGTH.put("L1", 125);
		MIN_CONTIG_LENGTH.put("HERVK", 100);
		MIN_CONTIG_LENGTH.put("LTR5_Hs", 100);
		MIN_CONTIG_LENGTH	.put("PSG", 100);
	}
	
	public static final Map<String, Integer> AVG_INS_LENGTH = new HashMap<String, Integer>();
	static {
		AVG_INS_LENGTH.put("Alu", 220);
		AVG_INS_LENGTH.put("SVA", 1000);
		AVG_INS_LENGTH.put("L1", 500);
	}
	
	// Input file
	public static final List<String> SUPPORTED_FILE_FORMATS = new ArrayList<String>(Arrays.asList("vcf", "bed"));
	public static String INPUT_FILE_WITH_LOCATIONS = System.getProperty("user.dir") + "/src/com/yg/input/non_reference/total_nonref_positions_LINE.bed";
													// System.getProperty("user.dir") + "/src/com/yg/input/non_reference/total_nonref_positions_SINE.bed";
													//System.getProperty("user.dir") + "/src/com/yg/input/data1KP/Alu_failed.bed";
													// System.getProperty("user.dir") + "/src/com/yg/input/data1KP/1KP_RetroSeq_shared_ins." + ME_TYPE + ".freq.bed"; 
													//System.getProperty("user.dir") + "/src/com/yg/input/non_reference/total_nonref_positions_SINE.bed";
													// System.getProperty("user.dir") + "/src/com/yg/input/non_reference/dbRIP_nonRef.Alu.bed";	// for testing											
			
	// Flanking
	public static int FLANKING_REGION = 600; // distance expand on each side of the position to search for discordant reads (600 bp)
	// Flanking sequences tags
	public static final String LEFT_FLANK_TAG = "left_flank";
	public static final String RIGHT_FLANK_TAG = "right_flank";

	// Output
	public static String OUTPUT_FORMAT = ".fa"; // by default FASTA format
	
	// Chromosome and position only
	public static boolean INPUT_FILE = true;
	public static String DEF_CHROMOSOME; // user defined chromosome
	public static long DEF_POSITION; // user defined position
	public static boolean SE_SPECIFIED = false;
	public static int START_LOCI = 0; // start loci in the list of all events
	public static int END_LOCI = 1;
	
	// Reference for MEIs
	public static String REPEATMASK_LOCS_BED = System.getProperty("user.dir") + "/src/com/yg/input/ref/hg19_";
	public static String REFERENCE_SEQ_BED = "/work/lianglab/DB/genomeSeq/rmsk/h19_"; //System.getProperty("user.dir") + "/src/com/yg/input/ref/hg19_";
	public static String REFERENCE_END = ".BED";
	
	public static final String REF_SEQ_DIR = "/work/lianglab/DB/genomeSeq/hg19/"; //System.getProperty("user.dir") + "/src/com/yg/input/ref/";
	public static String REFERENCE_SEQ_FA =  System.getProperty("user.dir") + "/src/com/yg/input/ref/hg19.fa";
	
	// Non-referenced MEIs
	public static String NON_REF_MEIS = System.getProperty("user.dir") + "/src/com/yg/input/non_reference/dbRIP_nonRef.Alu.bed";
	
	// VCF (variant call format) input file (location of variants)
	public static String VCF_INPUT_FILE = "/work/lianglab/wgs/Human_SV/MEI_ins/RetroSeq_Alu.vcf"; // System.getProperty("user.dir") + "/src/com/yg/input/Alu/RetroSeq_Alu.vcf";
	public static String VCF_INPUT_FILE_BEG = "/work/lianglab/wgs/Human_SV/MEI_ins/RetroSeq_"; // System.getProperty("user.dir") + "/src/com/yg/input/Alu/RetroSeq_";
	public static String VCF_INPUT_FILE_END = "_ins.vcf";
	// not needed
	public static String INTEGRATED_VCF_INPUT_FILE_BEG = System.getProperty("user.dir") + "/src/com/yg/input/Alu/Integrated_SV_map2";
	public static String INTEGRATED_INPUT_HEADER = System.getProperty("user.dir") +"/src/com/yg/input/Integrated_SV.header";
	
	// BAM files (reads)
	public static boolean COLLECT_READS = true;
	public static String BAM_INPUT_PATH = "/work/lianglab/wgs/human/BAM/"; // "/work/lianglab/wgs/human/1000GPmerged/";
	public static List<String> LIST_OF_BAM_FILES = new ArrayList<String>();
	
	// SAMTOOLS
	public static String SAMTOOLS_PATH = ""; //"/usr/local/bin/"; //"/work/lianglab/bin"; // path for samtools
	// Parameters for running
	public static boolean EXCLUDE_RANDOM = false; // option for whether or not to exclude non-standard chromosomes in samtools
	public static int QR = 2; // default minimal number of reads
	public static int TE = 1; // default is to filter out those with anchoring reads mapping to the same TE/gene regions
	public static int SPLIT_READS_CUTOFF = 90; // minimal number of split-reads to be collected before aborting reading from .bam files 
	public static int DISCORDANT_READS_CUTOFF = 120; // minimal number of discordant reads to be collected before aborting reading from .bam files 
	public static boolean FILTERS_APPLY = true;

	// QUALITY filtering
	public static boolean CHECK_QUAL = true;
	public static int MIN_BASE_QUAL = 10;  // -q minimal quality for any individual bases, default: 26
	public static int MIN_AVG_READ_QUAL = 20; // -Q minimal average quality values across the entire read, default:28
	public static int PERCENT_BASE_ABOVE_QUAL = 95; // -p percentage of bases meeting above -q N, default: 90 (most of seq are 100bp)
	public static int MIN_READ_LENGTH = 50; // -l mininal read length, default: 50
	public static int MIN_NUM_OF_BASES_ABOVE_QUAL = 48; // -L minimal number of bases with quality above -q N, default: 48

	// VELVET
	public static String VELVET_TOOL_PATH = "/work/lianglab/bin"; //"/usr/local/bin"; // path for velvet
	public static Integer HASH_LENGTH = 21; // k-mer length
	public static Integer MIN_CONTIG_LENGTH_TO_KEEP = 200;
	public static Integer INS_LENGTH = 300;
	
	// CDHIT
	public static String CDHIT_TOOL_PATH = "/work/lianglab/bin/cdhit/"; //"/usr/local/bin"; // path for cd hit
	public static Double PERC_IDENTITY_CDHIT = 0.98; // percentage of identity 

	// CAP3
	public static String CAP3_TOOL_PATH = "/work/lianglab/bin/CAP3/"; //"/usr/local/bin"; // path for cap3
	public static Integer PERC_IDENTITY_CAP3 = 90; // overlap percentage identity cutoff
	public static Integer OVERLAP_CAP3 = 16;
	
	// BLASTN
	public static String BLAST_EXEC_PATH = "/work/lianglab/bin/ncbi-blast-2.2.29+/bin"; // "/usr/local/ncbi/blast/bin"; // path for blastn executable
	public static String CONSENSUS_DB = System.getProperty("user.dir") + "/src/com/yg/input/consensus/" + ME_TYPE + ".fa"; //"/work/lianglab/DB/blastDB/MEIs/SVA_A-F_full_length.fa"; // path and name of blast DB (consensus sequence)
	public static int LEFTOVER_THRESHOLD = 10; // max leftover bps in contig when aligned to consensus
	public static int FLANKING_ALIGNMENT_THRESHOLD = 70; //100; // min bps that should be aligned to flanking region in contigs
	public static int LENGTH_ALIGNED_THRESHOLD = 95; // contig max % of alignment length to insertion
	
	// BL2SEQ
	public static String BL2SEQ_EXEC_PATH = "/work/lianglab/bin/blast-2.2.26/bin"; // path to bl2seq executable
	
	// THREADS
	public static int THREADS = 1; // by default
	
	/**
	 * Collect all .bam files' names from specified directory
	 * @return
	 * @throws InputParametersException 
	 */
	public static void getListOfBamFiles() throws InputParametersException {
		File dir = new File(IOParameters.BAM_INPUT_PATH);
		if (dir == null || !dir.isDirectory() || dir.listFiles() == null || dir.listFiles().length == 0) {
			throw new InputParametersException("PARAMETERS ERROR: there is no .bam files in specified directory "  + IOParameters.BAM_INPUT_PATH);
		}
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((".bam"))) {
				IOParameters.LIST_OF_BAM_FILES.add(IOParameters.BAM_INPUT_PATH + file.getName());
			}
		}
		LOGGER.info("Input .bam files " + IOParameters.LIST_OF_BAM_FILES.size());
	}
	
}