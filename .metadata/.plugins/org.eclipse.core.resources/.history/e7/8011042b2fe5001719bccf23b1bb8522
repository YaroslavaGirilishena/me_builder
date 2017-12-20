package com.yg.assembler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.yg.exceptions.InputParametersException;
import com.yg.io_handlers.IOParameters;
import com.yg.models.BLASTAlignmentData;
import com.yg.models.ConsensusLocation;
import com.yg.models.FASTASeq;
import com.yg.models.MEInsertion;
import com.yg.parsers.BLASTnOutParser;
import com.yg.parsers.FastaParser;
import com.yg.utilities.IOGeneralHelper;
import com.yg.utilities.ProcessStream;

/**
 * This class formats consensus databases for running BLAST alignment tool;
 * Performs alignments using blastn command;
 * Filters alignment data to collect good contigs;
 * Collects info about MEI from the alignment data
 *
 * @author Yaroslava Girilishena
 *
 */
public class BLASTAlignment {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // init logger
	
	// -----------------------------------------------------------------------------------
	// FORMAT THE CONSENSUS DATABASE
    // -----------------------------------------------------------------------------------
	
	/**
	 * Every database file should be indexed ahead
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InputParametersException
	 */
	public static void formatDB() throws IOException, InterruptedException, InputParametersException {
		String inputDB = IOParameters.CONSENSUS_DB; // path to consensus database (should be provided by user)
		// Check if db file exists
		File input = new File(inputDB);
		if (!input.exists() || input.isDirectory()) {
			throw new InputParametersException("BLASTN Formating DB ERROR: No DB file found!\n");
		}
			
		LOGGER.info("BLASTN Formating DB:\n");
		
		// Build command for formating (indexing) DB
		List<String> blastnCommands = new ArrayList<String>();
		blastnCommands.add(IOParameters.BLAST_EXEC_PATH + "/makeblastdb");
		blastnCommands.add("-dbtype");
		blastnCommands.add("nucl");
		blastnCommands.add("-out");
		blastnCommands.add(IOParameters.CONSENSUS_DB.substring(0, IOParameters.CONSENSUS_DB.lastIndexOf(".")));
		blastnCommands.add("-in");
		blastnCommands.add(inputDB);

		// Run the process
		ProcessBuilder blastnPB = new ProcessBuilder(blastnCommands);
        Process blastnProcess = blastnPB.start();
        
        // Collect error messages
        ProcessStream errStream = new ProcessStream(blastnProcess.getErrorStream(), "ERROR");            
        errStream.start();
        
        if (errStream.getOutput() != null && !errStream.getOutput().equals("") && errStream.getOutput().length() != 0) {
        	LOGGER.severe("BLASTN Formating DB ERROR:\n" + errStream.getOutput() + "\n");
        	throw new InputParametersException("BLASTN Formating DB ERROR:\n" + errStream.getOutput());
        } else {
        	errStream.cleanBuffer();
        }
        
        // Collect output
        ProcessStream outputStream = new ProcessStream(blastnProcess.getInputStream(), "OUTPUT");
        outputStream.start();
        
        blastnProcess.waitFor();
        
        LOGGER.info(outputStream.getOutput() + "\n"); // log output
        
        outputStream.cleanBuffer(); // clean buffer
	}
	
	// -----------------------------------------------------------------------------------
	// ALIGN SEQUENCE TO THE CONSENSUS
	// -----------------------------------------------------------------------------------
	
	/**
	 * Align contigs obtained from assembly to the consensus DB
	 * @param contigsFA - path to a fasta file with contigs
	 * @param chromosome
	 * @param position
	 * @return a file with output (alignment) data 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InputParametersException
	 */
	public static String alignContigsToConsensus(String contigsFA, String chromosome, long position) throws IOException, InterruptedException, InputParametersException {
		// Check if input file exist
		File input = new File(contigsFA);
		if (!input.exists() || input.isDirectory()) {
			return null;
		}
			
		// Create output directory if it doesn't exist
		IOGeneralHelper.createOutDir("/intermediate_output/blastn_output/" + IOParameters.ME_TYPE);
		// BLAST cannot process spaces
		String blastOutFile = System.getProperty("user.dir").replace(" ", "\\ ") + "/intermediate_output/blastn_output/" + IOParameters.ME_TYPE + "/" + IOParameters.ME_TYPE + "." + chromosome + "_" + position + ".fa";
		
		
		// Build command for running BLASTN
		List<String> blastnCommands = new ArrayList<String>();
		blastnCommands.add(IOParameters.BLAST_EXEC_PATH + "/blastn");
		blastnCommands.add("-db");
		blastnCommands.add(IOParameters.CONSENSUS_DB.substring(0, IOParameters.CONSENSUS_DB.lastIndexOf(".")));
		blastnCommands.add("-query");
		blastnCommands.add(contigsFA);
		blastnCommands.add("-max_target_seqs");
		blastnCommands.add("1");
		blastnCommands.add("-perc_identity");
		blastnCommands.add(IOParameters.MIN_CONSENSUS_IDENT.get(IOParameters.ME_TYPE).toString()); //80-100
		blastnCommands.add("-out");
		blastnCommands.add(blastOutFile);
		blastnCommands.add("-outfmt");
		blastnCommands.add("6");
		blastnCommands.add("-evalue");
		blastnCommands.add("1e-5"); //1e-10
		blastnCommands.add("-word_size");
		blastnCommands.add("7"); //9  
		//blastnCommands.add("-F"); // for not filtering out the low complexity sequence match
		//blastnCommands.add("F");
		
		
		// Run the tool
		LOGGER.info("Running BLASTN tool for " + contigsFA + "\n");
		ProcessBuilder blastnPB = new ProcessBuilder(blastnCommands);
        Process blastnProcess = blastnPB.start();
        
        // Collect error messages
        ProcessStream errStream = new ProcessStream(blastnProcess.getErrorStream(), "ERROR");            
        errStream.start();
        
        // Catch error
        if (errStream.getOutput() != null && !errStream.getOutput().equals("") && errStream.getOutput().length() != 0) {
        	throw new InputParametersException("BLASTN ERROR:\n" + errStream.getOutput());
        } else {
        	errStream.cleanBuffer();
        }
        
        // Collect output
        ProcessStream outputStream = new ProcessStream(blastnProcess.getInputStream(), "OUTPUT");
        outputStream.start();

        blastnProcess.waitFor();
        outputStream.cleanBuffer(); // clean buffer
        
        return blastOutFile;
	}
	
	
	// -----------------------------------------------------------------------------------
	// COLLECT MEI INFO FROM THE ALIGNMENT DATA
    // -----------------------------------------------------------------------------------
	
	/**
	 * If one fully merged sequence is obtained - parse its alignment to the consensus
	 * @param me
	 * @param contigs - merged sequence
	 * @param blastnOutput - file with alignments data
	 * @throws IOException
	 */
	public static void collectMEinfoForFullSeq(MEInsertion me, Map<String, FASTASeq> contigs, String blastnOutput) throws IOException {
		// Check if input file exists
    	File input = new File(blastnOutput);
    	if (!input.exists() || input.isDirectory()) {
    		me.setFull(false);
			return;
		}
    	
		// Parse blastn output
		BLASTnOutParser blastnParser = new BLASTnOutParser(blastnOutput);
		blastnParser.parse();
		
		// Collect alignments
		List<BLASTAlignmentData> alignments = blastnParser.blastAlignments; // parse output from blastn 

		// Log alignments info
		LOGGER.info("Total BLASTN alignments for ONE FULL sequence for " + me.getChromosome() + "_" + me.getPosition() + ": " + alignments.size() + " found\n");
//		for (BLASTAlignmentData alignmentData: alignments) { // can be removed
//			LOGGER.info(alignmentData.toString());
//		}
		
		// If there is no alignments
		if (alignments == null || alignments.size() == 0) {
			me.setFull(false); // set full sequence found to false
			return;
		}
		
		// Search for a valid alignment
		int validAlignmentIdx = -1;
		for (int i=0; i < alignments.size(); i++) {
			if (alignments.get(i).getQend() <= 300 || alignments.get(i).getQstart() >= contigs.get(alignments.get(i).getQseqid()).getSequence().length() - 300) {
				// Not valid alignment of insertion
				continue;
			} else {
				// Add new alignment to the list
				me.getConsensusAlignments().add(new ConsensusLocation(alignments.get(i).getQseqid(), alignments.get(i).getSseqid(), alignments.get(i).getSstart(), alignments.get(i).getSend()));
				if (validAlignmentIdx == -1) validAlignmentIdx = i;
				break;
			}
		}
		
		if (validAlignmentIdx == -1) {
			// If none of the alignments are valid
			me.setFull(false); // set full sequence found to false
		} else {
			me.setFull(true); // set full sequence found to true
			me.setStrand(me.getConsensusAlignments().get(0).getStrand()); // set strand
			
			// Set sequence depending on MEI type
			if (IOParameters.ME_TYPE.equals("Alu")) {
				
				me.setLength(alignments.get(validAlignmentIdx).getLength()); // set the length of insertion
				
				// Set insertion sequence
				me.setSequence(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence()
						.substring((int)alignments.get(validAlignmentIdx).getQstart()-1, (int)alignments.get(validAlignmentIdx).getQend())); // set the insertion sequence
				
				// Set left flanking sequence 
				me.setFlankingL(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().substring(0, (int)alignments.get(validAlignmentIdx).getQstart()-1));
				// Set right flanking sequence
				me.setFlankingR(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().substring((int)alignments.get(validAlignmentIdx).getQend()));
				
			} else {
				//System.out.println("\nSet length of aligned insertion: " + alignments.get(validAlignmentIdx).getLength() + "\n" + "Sequence full length: " + contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().length());
				me.setLength(alignments.get(validAlignmentIdx).getLength()); // set the length of aligned insertion
				
				if (contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION > IOParameters.FLANKING_REGION) {
					
					// Set insertion sequence
					me.setSequence(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().substring(IOParameters.FLANKING_REGION, contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION));
					
					// Set left flanking sequence 
					me.setFlankingL(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().substring(0, IOParameters.FLANKING_REGION));
					// Set right flanking sequence
					me.setFlankingR(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().substring(contigs.get(alignments.get(validAlignmentIdx).getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION));
					
				} else {
					me.setFull(false);
					me.setLength(0);
				}
				
			}
			
		}
		return;
		
	}

	/**
	 * If two contigs are assembled, align them to consensus and get MEI info
	 * Check for gap in the alignment 
	 * @param me
	 * @param contigs - left and right merged sequences
	 * @param blastnOutput - file with alignments data
	 * @throws IOException
	 */
	public static void collectMEinfoForTwoPaths(MEInsertion me, Map<String, FASTASeq> contigs, String blastnOutput) throws IOException {
		// Check if input file exists
    	File input = new File(blastnOutput);
    	if (!input.exists() || input.isDirectory()) {
    		LOGGER.info("No file with alignment to the consensus: " + blastnOutput);
    		me.setFull(false);
			return;
		}
    	
		// Parse blastn output
		BLASTnOutParser blastnParser = new BLASTnOutParser(blastnOutput);
		blastnParser.parse();
		
		// Collect alignments
		List<BLASTAlignmentData> alignments = blastnParser.blastAlignments; // parse output from blastn 

		LOGGER.info("Total BLASTN alignments for TWO sequences for " + me.getChromosome() + "_" + me.getPosition() + ": " + alignments.size() + " found\n");
		
		if (alignments == null || alignments.size() == 0) {
			LOGGER.info("No alignments found\n");
			me.setFull(false);
			return;
		}
		
		BLASTAlignmentData contig1 = null, contig2 = null;
		
		// Get two best alignments for both sequences
		for (int i=0; i<alignments.size(); i++) {
			if (contig1 == null) {
				
				if ((alignments.get(i).getQseqid().contains("left") && alignments.get(i).getQend() > IOParameters.FLANKING_REGION) ||
					alignments.get(i).getQseqid().contains("right") && alignments.get(i).getQstart() < contigs.get(alignments.get(i).getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION){
					
					contig1 = alignments.get(i);
					continue;
				}
			} else if (contig2 == null && !contig1.getQseqid().equals(alignments.get(i).getQseqid())) {
				
				if ((alignments.get(i).getQseqid().contains("left") && alignments.get(i).getQend() > IOParameters.FLANKING_REGION) ||
					alignments.get(i).getQseqid().contains("right") && alignments.get(i).getQstart() < contigs.get(alignments.get(i).getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION){
					
					// Different query id and contig2 is undefined yet
					contig2 = alignments.get(i);
					break;
				}
			}
		}
	
		if (contig1 == null) {
			LOGGER.info("Alignments are not valid!\n");
			me.setFull(false); // set found full sequence to false
			return;
		}
		
		// Only one contig is aligned
		if (contig2 == null || alignments.size() < 2) {
			
			if (contig1.getLength() > IOParameters.AVG_INS_LENGTH.get(IOParameters.ME_TYPE)) {
				LOGGER.info("ONE alignment insertion found. Contains FULL insertion\n");
				me.setFull(true);
				me.setSequence(contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQstart()-1, (int)contig1.getQend()));

				// Get flanking regions
				if (contig1.getQseqid().contains("left")) {

					String rightFlankFile = System.getProperty("user.dir") + "/intermediate_output/contigs_for_merging/" + IOParameters.ME_TYPE + "/" + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + "/contigs/right_flank.fa";
					// Get right flanking
					FastaParser parseFlank = new FastaParser(rightFlankFile);
					FASTASeq rightFlank = parseFlank.parse().get(0);
					
					me.setFlankingL(contigs.get(contig1.getQseqid()).getSequence().substring(0, (int)contig1.getQstart()-1)); // left
					
					// Get extra bases in front of the insertion
					if (contigs.get(contig1.getQseqid()).getSequence().length() - (int)contig1.getQend() > 0) {
						// attach extra bases to the left flanking
						me.setFlankingR(contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQend()) + rightFlank.getSequence());
					} else {
						me.setFlankingR(rightFlank.getSequence());
					}
					
				} else {

					String leftFlankFile = System.getProperty("user.dir") + "/intermediate_output/contigs_for_merging/" + IOParameters.ME_TYPE + "/" + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + "/contigs/left_flank.fa";
					// Get left flanking
					FastaParser parseFlank = new FastaParser(leftFlankFile);
					FASTASeq leftFlank = parseFlank.parse().get(0);
					
					me.setFlankingR(contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQend())); // right
					
					// Get extra bases in front of the insertion
					if ((int)contig1.getQstart() > 1) {
						// attach extra bases to the left flanking
						me.setFlankingL(leftFlank.getSequence() + contigs.get(contig1.getQseqid()).getSequence().substring(0, (int)contig1.getQstart()));
					} else {
						me.setFlankingL(leftFlank.getSequence());
					}
					
				}
				
			} else {
				me.setFull(false); // set found full sequence to false
			}
			
			me.setLength(contig1.getLength()); // set length
			me.setTypeOfMEI(contig1.getSseqid()); //set type
			me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend())); // add consensus alignment
			me.setStrand(me.getConsensusAlignments().get(0).getStrand()); // set strand
			
			return;
		}
		
		// Two contigs are aligned
		
		// Contig1 stores the left flanking, contig2 stores the right flanking
		if (!contig1.getQseqid().contains("left") && contig1.getQseqid().contains("right")) {
			BLASTAlignmentData tempContig = contig1;
			contig1 = contig2;
			contig2 = tempContig;
		}
					
		// Start and end positions in the consensus
		long contig1Sstart = contig1.getSstart() < contig1.getSend() ? contig1.getSstart() : contig1.getSend();
		long contig1Send = contig1.getSstart() < contig1.getSend() ? contig1.getSend() : contig1.getSstart();
		long contig2Sstart = contig2.getSstart() < contig2.getSend() ? contig2.getSstart() : contig2.getSend();
		long contig2Send = contig2.getSstart() < contig2.getSend() ? contig2.getSend() : contig2.getSstart();
		
		// -----------------------
 		// VALIDATE
 		// -----------------------
//		if (Math.abs(contig1End - contigs.get(contig1.getQseqid()).getSequence().length()) > 20 &&
//			contig2Start < 20){
//			me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(),  contig2.getSend())); // add consensus alignment
//			me.setFull(false); // set found full sequence to false
//			return;
//		}
//		
//		if (Math.abs(contig1End - contigs.get(contig1.getQseqid()).getSequence().length()) < 20 &&
//			contig2Start > 20){
//			me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(),  contig1.getSend())); // add consensus alignment
//			me.setFull(false); // set found full sequence to false
//			return;
//		}
//		
//		if (Math.abs(contig1End - contigs.get(contig1.getQseqid()).getSequence().length()) > 20 &&
//			contig2Start > 20){
//			me.setFull(false); // set found full sequence to false
//			return;
//		}

//		if (contig1.getQend() < IOParameters.FLANKING_REGION - 50 && contig2.getQstart() < contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION + 100) {
//			me.setTypeOfMEI(contig2.getSseqid()); //set type
//			me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(), contig2.getSend())); // add consensus alignment
//			me.setFull(false); // set found full sequence to false
//			return;
//		}
//		
//		if (contig2.getQstart() > contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION + 100) {
//			me.setTypeOfMEI(contig1.getSseqid()); //set type
//			me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend())); // add consensus alignment
//			me.setFull(false); // set found full sequence to false
//			return;
//		}
		
		// -----------------------
 		// TYPE
 		// -----------------------
		
		// Set type (sub-type)
		String meType = "";
		if (!contig1.getSseqid().equals(contig2.getSseqid())) {
			// If types of contigs are different, pick the one with higher % of identical matches with consensus
			meType = contig1.getPident() > contig2.getPident() ? contig1.getSseqid() : contig2.getSseqid();
		} else {
			meType = contig1.getSseqid();
		}
		me.setTypeOfMEI(meType);
		
		// -----------------------
 		// STRAND
 		// -----------------------
		
		if (contig1.getSstart() < contig1.getSend()) {
			me.setStrand('+');
		} else {
			me.setStrand('-');
		}
		
		// -----------------------
 		// SPECIAL CASES - ONE INSIDE ANOTHER
 		// -----------------------
		String sequence = "";
		
		// Left insertion inside the right one
		if (contig2Sstart < contig1Sstart && contig1Sstart < contig2Send 
				&& contig2Sstart < contig1Send && contig1Send < contig2Send) {
				
			sequence = contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1, (int)contig2.getQend());
		
			// Full sequence found
			me.setFull(true);
			
			// Set sequence and flanking regions
			me.setSequence(sequence);
			me.setFlankingL(contigs.get(contig1.getQseqid()).getSequence().substring(0, (int)contig1.getQstart()-1)); // left
			me.setFlankingR(contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQend())); // right
		
			// Set alignment information
			me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(), contig2.getSend()));
			me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend()));
			
			return;
		}
		
		// Right insertion inside the left one
		if (contig1Sstart < contig2Sstart && contig2Sstart < contig1Send 
				&& contig1Sstart < contig2Send && contig2Send < contig1Send) {
				
			sequence = contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQstart()-1, (int)contig1.getQend());
		
			// Full sequence found
			me.setFull(true);
			
			// Set sequence and flanking regions
			me.setSequence(sequence);
			me.setFlankingL(contigs.get(contig1.getQseqid()).getSequence().substring(0, (int)contig1.getQstart()-1)); // left
			me.setFlankingR(contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQend())); // right
		
			// Set alignment information
			me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend()));
			me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(), contig2.getSend()));
			
			return;
		}
		
		// -----------------------
 		// LENGTH
 		// -----------------------
		
		// Get gap length
		long leftBound, rightBound;
		
		if (contig1Sstart < contig2Sstart) {
			// contig1
			leftBound = contig1Send;
			// contig2
			rightBound = contig2Sstart;
		} else {
			// contig2
			leftBound = contig2Send;
			// contig1
			rightBound = contig1Sstart;
		}
		
		// Calculate the gap length
		int gapLength = (int) (rightBound - leftBound);
		System.out.println("GAP length: " + gapLength + "\n");
		// Calculate the insertion length
		int melength = (int) (contig1.getLength() + contig2.getLength() + gapLength);
		me.setLength(melength);
		
		
		// -----------------------
 		// SEQUENCE
 		// -----------------------
		
		// Make gap sequence
		String gaps = "";
		if (gapLength > 0) {
			StringBuffer outputSeqGap = new StringBuffer(Math.abs(gapLength));
			for (int i = 0; i < Math.abs(gapLength); i++){
				outputSeqGap.append("N");
			}
			gaps = outputSeqGap.toString();
			outputSeqGap.setLength(0);
		}
		
		
		// Extract sequence from contigs depending on MEI type
		if (IOParameters.ME_TYPE.equals("Alu")) {
		
			if (gapLength > 0) {
				sequence = contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQstart()-1, (int)contig1.getQend()) 
						+ gaps + contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1, (int)contig2.getQend());
				
				me.setContig1AlignLength(contig1.getLength());
				me.setContig2AlignLength(contig2.getLength());
				
				// Full sequence not found, but reconstructed with N's
				me.setFull(false);
				me.setPartialChar(true);
				
			} else {
				// Get more bp from the contig with higher % of identical matches
				if (contig1.getPident() > contig2.getPident()) {
					
					sequence = contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQstart()-1, (int)contig1.getQend()) + 
							   contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1 + Math.abs(gapLength), (int)contig2.getQend());
				} else {
					
					
					sequence = contigs.get(contig1.getQseqid()).getSequence().substring((int)contig1.getQstart()-1, (int)contig1.getQend() - Math.abs(gapLength)) + 
							   contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1, (int)contig2.getQend());
				}
				
				// Full sequence found
				me.setFull(true);
			}
			
			// Set sequence and flanking regions
			me.setSequence(sequence);
			me.setFlankingL(contigs.get(contig1.getQseqid()).getSequence().substring(0, (int)contig1.getQstart())); // left
			me.setFlankingR(contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQend())); // right
		
		} else {
			
			if (contig1.getQend() < IOParameters.FLANKING_REGION || contig2.getQstart()-1 > contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION) {
				me.setFull(false);
				
				// Set alignment information
				me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend()));
				me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(), contig2.getSend()));
				
				return;
			}
			
			if (gapLength > 0) {
				sequence = contigs.get(contig1.getQseqid()).getSequence().substring(IOParameters.FLANKING_REGION, (int)contig1.getQend()) 
						+ gaps + contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1, contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION);
				
				// Full sequence not found, but reconstructed with N's
				me.setFull(false);
				me.setPartialChar(true);
			} else {
				
				// Get more bp from the contig with higher % of identical matches
				if (contig1.getPident() > contig2.getPident()) {
					sequence = contigs.get(contig1.getQseqid()).getSequence().substring(IOParameters.FLANKING_REGION, (int)contig1.getQend()) 
							+ contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1 + Math.abs(gapLength), contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION);
				} else {
					sequence = contigs.get(contig1.getQseqid()).getSequence().substring(IOParameters.FLANKING_REGION, (int)contig1.getQend() - Math.abs(gapLength)) 
							+ contigs.get(contig2.getQseqid()).getSequence().substring((int)contig2.getQstart()-1, contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION);
				}
				
				// Full sequence found
				me.setFull(true);
			}
			
			// Set sequence and flanking regions
			me.setSequence(sequence);
			me.setFlankingL(contigs.get(contig1.getQseqid()).getSequence().substring(0, IOParameters.FLANKING_REGION)); // left
			me.setFlankingR(contigs.get(contig2.getQseqid()).getSequence().substring(contigs.get(contig2.getQseqid()).getSequence().length() - IOParameters.FLANKING_REGION)); // right
			
		}
		
		// Set alignment information
		me.getConsensusAlignments().add(new ConsensusLocation(contig1.getQseqid(), contig1.getSseqid(), contig1.getSstart(), contig1.getSend()));
		me.getConsensusAlignments().add(new ConsensusLocation(contig2.getQseqid(), contig2.getSseqid(), contig2.getSstart(), contig2.getSend()));
		
		return;
	}
}

/*
 USAGE
  blastn [-h] [-help] [-import_search_strategy filename]
    [-export_search_strategy filename] [-task task_name] [-db database_name]
    [-dbsize num_letters] [-gilist filename] [-seqidlist filename]
    [-negative_gilist filename] [-entrez_query entrez_query]
    [-db_soft_mask filtering_algorithm] [-db_hard_mask filtering_algorithm]
    [-subject subject_input_file] [-subject_loc range] [-query input_file]
    [-out output_file] [-evalue evalue] [-word_size int_value]
    [-gapopen open_penalty] [-gapextend extend_penalty]
    [-perc_identity float_value] [-xdrop_ungap float_value]
    [-xdrop_gap float_value] [-xdrop_gap_final float_value]
    [-searchsp int_value] [-max_hsps int_value] [-sum_statistics]
    [-penalty penalty] [-reward reward] [-no_greedy]
    [-min_raw_gapped_score int_value] [-template_type type]
    [-template_length int_value] [-dust DUST_options]
    [-filtering_db filtering_database]
    [-window_masker_taxid window_masker_taxid]
    [-window_masker_db window_masker_db] [-soft_masking soft_masking]
    [-ungapped] [-culling_limit int_value] [-best_hit_overhang float_value]
    [-best_hit_score_edge float_value] [-window_size int_value]
    [-off_diagonal_range int_value] [-use_index boolean] [-index_name string]
    [-lcase_masking] [-query_loc range] [-strand strand] [-parse_deflines]
    [-outfmt format] [-show_gis] [-num_descriptions int_value]
    [-num_alignments int_value] [-html] [-max_target_seqs num_sequences]
    [-num_threads int_value] [-remote] [-version]

DESCRIPTION
   Nucleotide-Nucleotide BLAST 2.2.29+

OPTIONAL ARGUMENTS
 -h
   Print USAGE and DESCRIPTION;  ignore all other parameters
 -help
   Print USAGE, DESCRIPTION and ARGUMENTS; ignore all other parameters
 -version
   Print version number;  ignore other arguments

 *** Input query options
 -query <File_In>
   Input file name
   Default = `-'
 -query_loc <String>
   Location on the query sequence in 1-based offsets (Format: start-stop)
 -strand <String, `both', `minus', `plus'>
   Query strand(s) to search against database/subject
   Default = `both'

 *** General search options
 -task <String, Permissible values: 'blastn' 'blastn-short' 'dc-megablast'
                'megablast' 'rmblastn' >
   Task to execute
   Default = `megablast'
 -db <String>
   BLAST database name
    * Incompatible with:  subject, subject_loc
 -out <File_Out>
   Output file name
   Default = `-'
 -evalue <Real>
   Expectation value (E) threshold for saving hits 
   Default = `10'
 -word_size <Integer, >=4>
   Word size for wordfinder algorithm (length of best perfect match)
 -gapopen <Integer>
   Cost to open a gap
 -gapextend <Integer>
   Cost to extend a gap
 -penalty <Integer, <=0>
   Penalty for a nucleotide mismatch
 -reward <Integer, >=0>
   Reward for a nucleotide match
 -use_index <Boolean>
   Use MegaBLAST database index
   Default = `false'
 -index_name <String>
   MegaBLAST database index name

 *** BLAST-2-Sequences options
 -subject <File_In>
   Subject sequence(s) to search
    * Incompatible with:  db, gilist, seqidlist, negative_gilist,
   db_soft_mask, db_hard_mask
 -subject_loc <String>
   Location on the subject sequence in 1-based offsets (Format: start-stop)
    * Incompatible with:  db, gilist, seqidlist, negative_gilist,
   db_soft_mask, db_hard_mask, remote

 *** Formatting options
 -outfmt <String>
   alignment view options:
     0 = pairwise,
     1 = query-anchored showing identities,
     2 = query-anchored no identities,
     3 = flat query-anchored, show identities,
     4 = flat query-anchored, no identities,
     5 = XML Blast output,
     6 = tabular,
     7 = tabular with comment lines,
     8 = Text ASN.1,
     9 = Binary ASN.1,
    10 = Comma-separated values,
    11 = BLAST archive format (ASN.1) 
   
   Options 6, 7, and 10 can be additionally configured to produce
   a custom format specified by space delimited format specifiers.
   The supported format specifiers are:
   	    qseqid means Query Seq-id
   	       qgi means Query GI
   	      qacc means Query accesion
   	   qaccver means Query accesion.version
   	      qlen means Query sequence length
   	    sseqid means Subject Seq-id
   	 sallseqid means All subject Seq-id(s), separated by a ';'
   	       sgi means Subject GI
   	    sallgi means All subject GIs
   	      sacc means Subject accession
   	   saccver means Subject accession.version
   	   sallacc means All subject accessions
   	      slen means Subject sequence length
   	    qstart means Start of alignment in query
   	      qend means End of alignment in query
   	    sstart means Start of alignment in subject
   	      send means End of alignment in subject
   	      qseq means Aligned part of query sequence
   	      sseq means Aligned part of subject sequence
   	    evalue means Expect value
   	  bitscore means Bit score
   	     score means Raw score
   	    length means Alignment length
   	    pident means Percentage of identical matches
   	    nident means Number of identical matches
   	  mismatch means Number of mismatches
   	  positive means Number of positive-scoring matches
   	   gapopen means Number of gap openings
   	      gaps means Total number of gaps
   	      ppos means Percentage of positive-scoring matches
   	    frames means Query and subject frames separated by a '/'
   	    qframe means Query frame
   	    sframe means Subject frame
   	      btop means Blast traceback operations (BTOP)
   	   staxids means unique Subject Taxonomy ID(s), separated by a ';'
   			 (in numerical order)
   	 sscinames means unique Subject Scientific Name(s), separated by a ';'
   	 scomnames means unique Subject Common Name(s), separated by a ';'
   	sblastnames means unique Subject Blast Name(s), separated by a ';'
   			 (in alphabetical order)
   	sskingdoms means unique Subject Super Kingdom(s), separated by a ';'
   			 (in alphabetical order) 
   	    stitle means Subject Title
   	salltitles means All Subject Title(s), separated by a '<>'
   	   sstrand means Subject Strand
   	     qcovs means Query Coverage Per Subject
   	   qcovhsp means Query Coverage Per HSP
   When not provided, the default value is:
   'qseqid sseqid pident length mismatch gapopen qstart qend sstart send
   evalue bitscore', which is equivalent to the keyword 'std'
   Default = `0'
 -show_gis
   Show NCBI GIs in deflines?
 -num_descriptions <Integer, >=0>
   Number of database sequences to show one-line descriptions for
   Not applicable for outfmt > 4
   Default = `500'
    * Incompatible with:  max_target_seqs
 -num_alignments <Integer, >=0>
   Number of database sequences to show alignments for
   Default = `250'
    * Incompatible with:  max_target_seqs
 -html
   Produce HTML output?

 *** Query filtering options
 -dust <String>
   Filter query sequence with DUST (Format: 'yes', 'level window linker', or
   'no' to disable)
   Default = `20 64 1'
 -filtering_db <String>
   BLAST database containing filtering elements (i.e.: repeats)
 -window_masker_taxid <Integer>
   Enable WindowMasker filtering using a Taxonomic ID
 -window_masker_db <String>
   Enable WindowMasker filtering using this repeats database.
 -soft_masking <Boolean>
   Apply filtering locations as soft masks
   Default = `true'
 -lcase_masking
   Use lower case filtering in query and subject sequence(s)?

 *** Restrict search or results
 -gilist <String>
   Restrict search of database to list of GI's
    * Incompatible with:  negative_gilist, seqidlist, remote, subject,
   subject_loc
 -seqidlist <String>
   Restrict search of database to list of SeqId's
    * Incompatible with:  gilist, negative_gilist, remote, subject,
   subject_loc
 -negative_gilist <String>
   Restrict search of database to everything except the listed GIs
    * Incompatible with:  gilist, seqidlist, remote, subject, subject_loc
 -entrez_query <String>
   Restrict search with the given Entrez query
    * Requires:  remote
 -db_soft_mask <String>
   Filtering algorithm ID to apply to the BLAST database as soft masking
    * Incompatible with:  db_hard_mask, subject, subject_loc
 -db_hard_mask <String>
   Filtering algorithm ID to apply to the BLAST database as hard masking
    * Incompatible with:  db_soft_mask, subject, subject_loc
 -perc_identity <Real, 0..100>
   Percent identity
 -culling_limit <Integer, >=0>
   If the query range of a hit is enveloped by that of at least this many
   higher-scoring hits, delete the hit
    * Incompatible with:  best_hit_overhang, best_hit_score_edge
 -best_hit_overhang <Real, (>=0 and =<0.5)>
   Best Hit algorithm overhang value (recommended value: 0.1)
    * Incompatible with:  culling_limit
 -best_hit_score_edge <Real, (>=0 and =<0.5)>
   Best Hit algorithm score edge value (recommended value: 0.1)
    * Incompatible with:  culling_limit
 -max_target_seqs <Integer, >=1>
   Maximum number of aligned sequences to keep 
   Not applicable for outfmt <= 4
   Default = `500'
    * Incompatible with:  num_descriptions, num_alignments

 *** Discontiguous MegaBLAST options
 -template_type <String, `coding', `coding_and_optimal', `optimal'>
   Discontiguous MegaBLAST template type
    * Requires:  template_length
 -template_length <Integer, Permissible values: '16' '18' '21' >
   Discontiguous MegaBLAST template length
    * Requires:  template_type

 *** Statistical options
 -dbsize <Int8>
   Effective length of the database 
 -searchsp <Int8, >=0>
   Effective length of the search space
 -max_hsps <Integer, >=0>
   Set maximum number of HSPs per subject sequence to save (0 means no limit)
   Default = `0'
 -sum_statistics
   Use sum statistics

 *** Search strategy options
 -import_search_strategy <File_In>
   Search strategy to use
    * Incompatible with:  export_search_strategy
 -export_search_strategy <File_Out>
   File name to record the search strategy used
    * Incompatible with:  import_search_strategy

 *** Extension options
 -xdrop_ungap <Real>
   X-dropoff value (in bits) for ungapped extensions
 -xdrop_gap <Real>
   X-dropoff value (in bits) for preliminary gapped extensions
 -xdrop_gap_final <Real>
   X-dropoff value (in bits) for final gapped alignment
 -no_greedy
   Use non-greedy dynamic programming extension
 -min_raw_gapped_score <Integer>
   Minimum raw gapped score to keep an alignment in the preliminary gapped and
   traceback stages
 -ungapped
   Perform ungapped alignment only?
 -window_size <Integer, >=0>
   Multiple hits window size, use 0 to specify 1-hit algorithm
 -off_diagonal_range <Integer, >=0>
   Number of off-diagonals to search for the 2nd hit, use 0 to turn off
   Default = `0'

 *** Miscellaneous options
 -parse_deflines
   Should the query and subject defline(s) be parsed?
 -num_threads <Integer, >=1>
   Number of threads (CPUs) to use in the BLAST search
   Default = `1'
    * Incompatible with:  remote
 -remote
   Execute search remotely?
    * Incompatible with:  gilist, seqidlist, negative_gilist, subject_loc,
   num_threads
 */