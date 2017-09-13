package com.yg.parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.yg.models.Bl2seqOutputData;
import com.yg.utilities.PatternSplitter;

/**
 * Custom parser for bl2seq tabulated output files
 * @author Yaroslava Girilishena
 *
 */
public class Bl2seqOutTabParser {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private BufferedReader reader;
	private String filename;
	private int lineNumber;
	
	public List<Bl2seqOutputData> alignmentsList;
	public boolean checkIdentities = true;
	
	/*
	 * Constructor
	 */
	public Bl2seqOutTabParser(String filename, boolean checkIdentities) {
		alignmentsList = new ArrayList<Bl2seqOutputData>();

    	this.filename = filename;
    	this.checkIdentities = checkIdentities;   
    	this.lineNumber = 0;
    }
	
	
	public void parse() throws IOException {
		this.reader = new BufferedReader(new FileReader(this.filename));
		boolean hasNext = true;
		while (hasNext) {
			hasNext = parseNextLine();
		}
    	IOUtils.closeQuietly(reader);
    }
	
	private boolean parseNextLine() throws IOException {
		String line = reader.readLine();
		if (line == null) {
			return false; // No more data to read
		}
		
		// Skip header 
		if (line.startsWith("#")) {
			return true;
		}
		
		lineNumber++;
		
		try {
			// Get alignment data
			List<String> data = PatternSplitter.toList(PatternSplitter.PTRN_TAB_SPLITTER, line.trim());
			
			if (data == null || data.size() < 10) {
				return false;
			}
			
			// Parse bl2seq output data
			Bl2seqOutputData bl2seqData = new Bl2seqOutputData();
			
			bl2seqData.setQueryId(data.get(0)); // set query id
			bl2seqData.setSubjectId(data.get(1)); // set subject id
			
			bl2seqData.setIdentities(Double.parseDouble(data.get(2))); // set % of identities
			bl2seqData.setAlignmentLength(Integer.parseInt(data.get(3))); // set alignment length
			
			bl2seqData.setQueryStart(Integer.parseInt(data.get(6))); // get query start
			bl2seqData.setQueryEnd(Integer.parseInt(data.get(7))); // set query end
			
			bl2seqData.setSubjectStart(Integer.parseInt(data.get(8))); // set subject start
			bl2seqData.setSubjectEnd(Integer.parseInt(data.get(9))); // set subject end
			bl2seqData.setHitsFound(true);
			
			// set query strand
			if (Integer.parseInt(data.get(6)) < Integer.parseInt(data.get(7))) {
				bl2seqData.setQueryStrand('+');
			} else {
				bl2seqData.setQueryStrand('-');
			}
			
			// set subject strand
			if (Integer.parseInt(data.get(8)) < Integer.parseInt(data.get(9))) {
				bl2seqData.setSubjectStrand('+');
			} else {
				bl2seqData.setSubjectStrand('-');
			}
			
			
			// Add data to the list of alignments 
			if (!this.checkIdentities || bl2seqData.getIdentities() >= 90) {
				this.alignmentsList.add(bl2seqData);
			}
			
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("ERROR - Parsing: BL2SEQ output line #" + lineNumber + ": " + line, e);
		}
		
		return true;
	}
	
	/*
	 * For testing purposes
	 */
	public static void main(String[] arg) {
		String fileToParse = "/Users/YG/Desktop/left_right_new2.fa";
		Bl2seqOutTabParser parser = new Bl2seqOutTabParser(fileToParse, false);
		
		try {
			parser.parse();
			System.out.println("Alignments:");
			for (Bl2seqOutputData bl2seq : parser.alignmentsList) {
				System.out.println(bl2seq.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
