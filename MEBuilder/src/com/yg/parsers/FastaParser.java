package com.yg.parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.yg.exceptions.FileException;
import com.yg.exceptions.InputParametersException;
import com.yg.io_handlers.IOParameters;
import com.yg.models.FASTASeq;
import com.yg.utilities.IOGeneralHelper;

/**
 * This class parses fasta files and returns list of FASTASeq instances;
 * Extracts flanking sequences
 * 
 * @author Yaroslava Girilishena
 *
 */
public class FastaParser {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
    private String filename;
    
    
    public FastaParser(String filename) {
    	this.filename = filename;
    }
    
    public List<FASTASeq> parse() throws IOException {
    	// Check if input file exist
    	File input = new File(this.filename);
    	if (!input.exists() || input.isDirectory()) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new FileReader(this.filename));
		List<FASTASeq> fastaSequences = parseFile(reader);
    	IOUtils.closeQuietly(reader);
    	return fastaSequences;
    }

    public List<FASTASeq> parseFile(BufferedReader reader) throws IOException {
    	List<FASTASeq> fastaSequences= new ArrayList<FASTASeq>();
    	
    	List<String> desc= new ArrayList<>();
    	List<String> seq = new ArrayList<>();
    	
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
 
		if (line == null) {
			return null;
		}
 
		if (line.charAt(0) != '>') {
			throw new IOException( "ERROR - FastaParser: first line of " + this.filename + " has to start with '>'" );
		} else {
			desc.add(line.trim().substring(1, line.length())); // remove '>' character in front
		}
        
		for (line = reader.readLine().trim(); line != null; line = reader.readLine()) {
			if (line.length() > 0 && line.charAt(0) == '>' ) {
				seq.add(buffer.toString());
				buffer = new StringBuffer();
				desc.add(line.substring(1, line.length())); // remove '>' character in front
			} else  {
				buffer.append(line.trim());
			}
		}   
		
		if (buffer.length() != 0) {
			seq.add(buffer.toString());
		}
    	
    	for (int i=0; i<desc.size(); i++) {
    		FASTASeq faSeq = new FASTASeq(desc.get(i), seq.get(i));
    		fastaSequences.add(faSeq);
    	}
    	buffer.setLength(0);
    	return fastaSequences;
    }
    
    
    /**
     * Parse the reference genome to extract the sequence from the given range
     * @param filename
     * @param start
     * @param interval
     * @return
     * @throws FileException
     * @throws IOException
     */
	public FASTASeq parseRefGenome(String filename, int start, int interval) throws FileException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
    	
    	FASTASeq fastaSequence = new FASTASeq();
    	
    	String desc = null;
    	String seq = null;
    	
		String line = reader.readLine();
 
		if (line == null) {
			reader.close();
			//LOGGER.severe("ERROR - FastaParser: file + " + filename + " is an empty file" );
			throw new FileException( "ERROR - FastaParser: file + " + filename + " is an empty file" );
		}
 
		if (line.charAt(0) != '>') {
			reader.close();
			//LOGGER.severe("ERROR - FastaParser: first line of " + filename + " should start with '>'");
			throw new FileException( "ERROR - FastaParser: first line of " + filename + " should start with '>'" );
		} else {
			desc = line.trim().substring(1, line.length()); // remove '>' character in front
		}
		
		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line.trim());
		}
		
		seq = buffer.toString().substring(start, start+interval);
		buffer.setLength(0);
		
		fastaSequence.setDescription(desc);
		fastaSequence.setSequence(seq);
    	
		reader.close();
    	return fastaSequence;
	}
	
	/**
	 * Extract contigs from fasta file
	 * @param contigsFA - file with contigs
	 * @return Map of <contig_name: fasta sequence>
	 * @throws IOException
	 */
	public static Map<String, FASTASeq> extractContigs(String contigsFA) throws IOException {
		if (contigsFA == null || contigsFA.length() == 0) {
			return null;
		}
		
		FastaParser faParser = new FastaParser(contigsFA);
		List<FASTASeq> contigs = faParser.parse();
		
		if (contigs == null || contigs.isEmpty()) {
			return null;
		}
		
		LOGGER.info("Number of contigs for " + contigsFA + ": " + contigs.size() + "\n");
		
		Map<String, FASTASeq> descToSeqMap = new HashMap<String, FASTASeq>();
		for (FASTASeq seq : contigs) {
			descToSeqMap.put(seq.getDescription(), seq);
		}
		
		return descToSeqMap;
	}
	
	/**
	 * Extract flanking sequences from the reference genome
	 * @param chromosome
	 * @param position
	 * @return
	 * @throws IOException
	 * @throws FileException 
	 */
	public static List<String> collectFlankingSequences(String outputDir, String chromosome, long position) throws IOException, FileException {
		String chromosomeRefFile = IOParameters.REF_SEQ_DIR + chromosome + ".fa";
		// Check if input file exist
		File input = new File(chromosomeRefFile);
		if (!input.exists() || input.isDirectory()) {
			return null;
		}
				
		LOGGER.info("Parsing refrence sequence file: " + chromosomeRefFile + " FOR " + chromosome + "_" + position + " flanking\n");
		
		 // Extract flanking sequences
        FastaParser faParser = new FastaParser(chromosomeRefFile);
		FASTASeq refSeq = faParser.parseRefGenome(chromosomeRefFile, (int)position - IOParameters.FLANKING_REGION, 2*IOParameters.FLANKING_REGION);
		
		String flankingSeq = refSeq.getSequence().substring(0, IOParameters.FLANKING_REGION);
		FASTASeq leftFlanking = new FASTASeq(IOParameters.LEFT_FLANK_TAG, flankingSeq);
		
		flankingSeq = refSeq.getSequence().substring(IOParameters.FLANKING_REGION); // to the end of the sequence
		FASTASeq rightFlanking = new FASTASeq(IOParameters.RIGHT_FLANK_TAG, flankingSeq);

		
		// Write flanking sequences into files
		IOGeneralHelper.writeFASeqIntoFile(outputDir + "/left_flank.fa", leftFlanking, false);
		IOGeneralHelper.writeFASeqIntoFile(outputDir + "/right_flank.fa", rightFlanking, false);
		
		// Write full flanking into a separate file
		IOGeneralHelper.createOutDir("/intermediate_output/ref_flanking/" + IOParameters.ME_TYPE);
		IOGeneralHelper.writeFASeqIntoFile(System.getProperty("user.dir") + "/intermediate_output/ref_flanking/" + IOParameters.ME_TYPE + "/" + chromosome + "_" + position + ".fa", refSeq, false);
		
		// Generate output
		List<String> flankingSequences = new ArrayList<String>();
		flankingSequences.add(outputDir + "/left_flank.fa");
		flankingSequences.add(outputDir + "/right_flank.fa");
		
		return flankingSequences;
	}

	/**
	 * Parse the whole reference file into files that contain only one chromosome
	 * @throws InputParametersException
	 * @throws FileException
	 */
	public static void parseRefIntoChromosomes() throws InputParametersException, FileException {
		// Check if input file exist
		File input = new File(IOParameters.REFERENCE_SEQ_FA);
		if (!input.exists() || input.isDirectory()) {
			throw new InputParametersException("NO REFERENCE FILE");
		}
		
		String line, chromosome;
		BufferedReader reader;
		BufferedWriter bw;
		
		try {
			reader = new BufferedReader(new FileReader(IOParameters.REFERENCE_SEQ_FA));

			line = reader.readLine();
			if (line == null) {
				reader.close();
				throw new FileException( "ERROR - FastaParser: file + " + IOParameters.REFERENCE_SEQ_FA + " is an empty file" );
			}
	 
			while ( (line=reader.readLine()) != null ) {
				if (line.charAt(0) == '>') {
					chromosome = line.trim().substring(1, line.length()); // remove '>' character in front
					
					System.out.println("Parsing chromosome: " + chromosome + " into separate file");
					
					bw = new BufferedWriter(new FileWriter(IOParameters.REF_SEQ_DIR + chromosome + ".fa"));
		            while ((line=reader.readLine()).charAt(0) != '>') {
		            	bw.write(line);
		            }
		            
		            bw.close();
				}
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
