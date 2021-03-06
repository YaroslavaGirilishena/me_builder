package com.yg;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.*;

import org.apache.commons.io.FileUtils;

import com.yg.assembler.BLASTAlignment;
import com.yg.assembler.CAP3Assembler;
import com.yg.assembler.BridgeAssembly;
import com.yg.exceptions.InputParametersException;
import com.yg.io_handlers.IOParameters;
import com.yg.io_handlers.InputDataHandler;
import com.yg.io_handlers.OutputData;
import com.yg.io_handlers.UserInputHandler;
import com.yg.logger.CustomLogger;
import com.yg.models.FASTASeq;
import com.yg.models.MEInsertion;
import com.yg.models.Variants;
import com.yg.parsers.FastaParser;
import com.yg.utilities.IOGeneralHelper;

/**
 * Main class for running the pipeline for MEIs characterization 
 * 
 * @author Yaroslava Girilishena
 *
 */
public class Main {
	
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // initialize global logger

	/**
	 * Main method that starts the process
	 * @param args - array of user's input parameters
	 */
	public static void main(String[] args) {
		// Setup logger
		try {
			CustomLogger.setup(System.getProperty("user.dir") + "/log/start.log"); // init a custom folder for logger
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		// Check input parameters
		try {
			if (!UserInputHandler.parseCLParameters(args)) { // parse user's input
				return;
			}
		} catch (InputParametersException e) {
			LOGGER.log(Level.SEVERE, "EXCEPTION:\n" + e.toString(), e);
			UserInputHandler.printHelp();
			e.printStackTrace();
			return;
		}
		
		try {
			// Initialize IO handler
			InputDataHandler ioHandler = new InputDataHandler();
			
			// Process input locations (from a file or from a command line)
			inputPreprocessing(ioHandler);
			
			// Run the pipeline - main algorithm
			runThePipeline(ioHandler);
			
		} catch (Exception e) {
			// Log all exceptions
			LOGGER.log(Level.SEVERE, "EXCEPTION:\n" + e.toString(), e);
			// Print the help message
			UserInputHandler.printHelp();
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Parse a list of mei locations
	 * @param ioHandler
	 * @throws IOException
	 * @throws InputParametersException 
	 * @throws InterruptedException 
	 */
	public static void inputPreprocessing(InputDataHandler ioHandler) throws IOException, InputParametersException, InterruptedException {
		LOGGER.info(Main.class.getName() + " LOGGER STARTED \n");

		// Index consensus databases (to use blast tool)
		BLASTAlignment.formatDB();
		
		// Parse RepeatMasker output into files that store each subtype data separately 
		//BEDParser.parseByTypes(IOParameters.REPEATMASK_LOCS_BED + IOParameters.ME_TYPE + IOParameters.REFERENCE_END);
		
		if (IOParameters.INPUT_FILE) { // if input locations are in a file
			// Process different input file formats
			if (IOParameters.INPUT_FILE_WITH_LOCATIONS.endsWith("bed")) {
				// Parse and process BED file
				ioHandler.parseBED(IOParameters.INPUT_FILE_WITH_LOCATIONS);
			} else if (IOParameters.INPUT_FILE_WITH_LOCATIONS.endsWith("vcf")) {
				// Parse and process VCF file
				ioHandler.parseVCF(IOParameters.INPUT_FILE_WITH_LOCATIONS);
			} else {
				throw new InputParametersException("Input file format is not supported");
			}
		} else {
			// If one mei location is provided
			Variants.listOfMEI.add(new MEInsertion(IOParameters.DEF_CHROMOSOME, IOParameters.DEF_POSITION, IOParameters.DEF_POSITION));
		}
	}
	
	/**
	 * Main algorithm implementation (corresponds to a flowchart)
	 * @param ioHandler
	 * @throws Exception 
	 */
	public static void runThePipeline(InputDataHandler ioHandler) throws Exception {
		// Total number of mei locations
		LOGGER.info("LIST OF MEIs: " + Variants.listOfMEI.size() + "\n");
		
		// If start and end positions in a mei list are not specified, take a full list
		if (!IOParameters.SE_SPECIFIED) {
			IOParameters.START_LOCI = 0;
			IOParameters.END_LOCI =  Variants.listOfMEI.size();
		} else {
			if (IOParameters.END_LOCI > Variants.listOfMEI.size()) {
				IOParameters.END_LOCI = Variants.listOfMEI.size();
			}
		}
		
		int i = 0; // count the position in the list
		
		// For each mei location
		for (MEInsertion me: Variants.listOfMEI.subList(IOParameters.START_LOCI, IOParameters.END_LOCI)) {
			
			// Setup log file for each event
			CustomLogger.setup(System.getProperty("user.dir") + "/log/" + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + ".log");
			
			LOGGER.info("\n---------------------------------------------------------------------------------------------------------------------------------\n" +
					"---------------------------------------------------------------------------------------------------------------------------------\n");
			LOGGER.info("Process STARTED for: " + IOParameters.ME_TYPE + "." + me.getChromosome() + '_' + me.getPosition() + "\t#" + (i+1) + " in a list\n");
			i++;
			
			// Collect discordant reads and split-reads by running SAMtools on the collection of .bam files
			Integer estimatedCoverage = 0; // estimated coverage
			if (IOParameters.COLLECT_READS) { // if reeds need to be collected
				
				// Check if reads are already collected for a given location
				String readsFile = System.getProperty("user.dir") + "/disc_reads/" + me.getChromosome() + '_' + me.getPosition() + ".fa";
				File input = new File(readsFile);
				
				// If file with reads does not exist, run SAMtools
				if (!input.exists() || input.isDirectory()) {
					estimatedCoverage = ioHandler.collectDiscordantReads(me.getChromosome(), me.getPosition()); // returns coverage (it was used for assembly)
					if (estimatedCoverage < 2) { 
						LOGGER.info("Estimated COVERAGE: " + estimatedCoverage + " - not qualified \n");
						continue; 
					} else {
						LOGGER.info("Estimated COVERAGE: " + estimatedCoverage + "\n");
					}
				}
			}
			
			// Perform local assembly on collected reads using cap3 assembler
			String contigsFA = CAP3Assembler.doAssembly(me.getChromosome(), me.getPosition());
			if (contigsFA == null || contigsFA.equals("")) {
				LOGGER.info("No ASSEMBLED contigs");
				OutputData.writeFailedMEOut(me); // write down failed mei with no contigs
				continue;
			}
			
			// Check obtained contigs after the assembly
			Map<String, FASTASeq> contigs = FastaParser.extractContigs(contigsFA); // extract contigs
			if (contigs == null || contigs.isEmpty()) {
				LOGGER.info("NO CONTIGS ASSEMBLED for " + me.getChromosome() + ":" + me.getPosition() + "\n");
				OutputData.writeFailedMEOut(me); // write down failed mei with no contigs
				continue;
			}
						
			// Parse contigs into separate files
			String contigsOutDir = "/intermediate_output/contigs_for_merging/" + IOParameters.ME_TYPE + "/" + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + "/contigs";
			// Create new directory
			IOGeneralHelper.createOutDir(contigsOutDir);
			// Remove all existing files from that directory
			FileUtils.cleanDirectory(new File(System.getProperty("user.dir") + contigsOutDir));
			
			String contigsDir = CAP3Assembler.parseContigsIntoSepFiles(contigsFA, System.getProperty("user.dir") + contigsOutDir); // write valid contigs into separate files
			if (contigsDir == null) {
				throw new InputParametersException("Contigs cannot be separated into files");
			}		
						
			// Run "bridge" assembly on obtained separated contigs
			BridgeAssembly bridgeAssebly = new BridgeAssembly(me);
			String mergedContigsFile = bridgeAssebly.findMaxOverlap(contigsDir); // return a file with merged contigs
			
			// Check if file with merged contigs exists
			if (mergedContigsFile == null) {
				LOGGER.info("NO file with MERGED contigs\n");
				OutputData.writeFailedMEOut(me); // write down failed mei
				continue;
			}
			File input = new File(mergedContigsFile);
			if (!input.exists() || input.isDirectory()) {
				LOGGER.info("NO file with MERGED contigs\n");
				OutputData.writeFailedMEOut(me); // write down failed mei
				continue;
			}
			
			// Check obtained contigs after the "bridge" assembly
			contigs = FastaParser.extractContigs(mergedContigsFile);
			if (contigs == null || contigs.isEmpty()) {
				LOGGER.info("NO MERGED contigs\n");
				OutputData.writeFailedMEOut(me); // write down failed mei
				
				LOGGER.info("\nProcess ENDED for: " + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + "\n");
				LOGGER.info("\n---------------------------------------------------------------------------------------------------------------------------------\n" +
						"---------------------------------------------------------------------------------------------------------------------------------\n\n");

				continue;
			}
			
			// Set all contigs to MEI
			for (String contigKey : contigs.keySet()) {
				me.getContigs().add(contigs.get(contigKey)); // add all contigs
			}
			
			// Generate output
			if (me.isFull() || me.isPartialChar()) { // insertion is fully or partially characterized
				
				OutputData.extractTSD(mergedContigsFile, me); // get TSD or IMD
				OutputData.writeMEOut(me); // write a characterized MEI and pre-integration sequence into a file
				
			} else { // no full insertion constructed
				OutputData.writeFailedMEOut(me); // write a failed MEI to a separate file
			}
			
			LOGGER.info("\nProcess ENDED for: " + IOParameters.ME_TYPE + "." + me.getChromosome() + "_" + me.getPosition() + "\n");
			LOGGER.info("\n---------------------------------------------------------------------------------------------------------------------------------\n" +
					"---------------------------------------------------------------------------------------------------------------------------------\n\n");

			// Close log file
			CustomLogger.fileTxt.close();
			
			// -------------------------------------------
			// FUTURE WORK FOR LONG INSERTIONS
			// -------------------------------------------
			
			// Obtain positions in ref sequence 			
			// Collect concordant reads from reference
			// Do bridge assembly to fill in gaps in the middle
			// Align full sequence to consensus
		}	
	}
}
