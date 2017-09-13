package com.yg.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.yg.models.BLASTAlignmentData;
import com.yg.utilities.PatternSplitter;

/**
 * This file parses output data from BLAST alignment tool
 * 
 * @author Yaroslava Girilishena
 *
 */
public class BLASTnOutParser {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private int lineNumber;				// number of current line being read
	private boolean alreadyFinished;
	private BufferedReader reader;
	private String filename;
	public List<BLASTAlignmentData> blastAlignments = new ArrayList<BLASTAlignmentData>(); // collected data
	
	public BLASTnOutParser(String filename) {
    	this.filename = filename;
    }
	
	public void parse() throws IOException {
		// Check if input file exist
    	File input = new File(this.filename);
    	if (!input.exists() || input.isDirectory()) {
			return;
		}
		this.reader = new BufferedReader(new FileReader(this.filename));
		boolean hasNext = true;
		while (hasNext) {
			//System.out.println("Line #: " + lineNumber + " - has next");
			hasNext = parseNextLine();
		}
    	IOUtils.closeQuietly(reader);
    }
	
	/**
	 * Parse each line of file
	 * @return boolean - does next line exist
	 * @throws IOException
	 */
	private boolean parseNextLine() throws IOException {
		// Read line with data
		String line = reader.readLine();
		if (line == null) {
			System.out.println("BLASTn output Parser - Finished reading");
			alreadyFinished = true;
			return false; // No more data to read
		}
		
		if (alreadyFinished) {
			// Prevents user errors from causing infinite loops
			throw new IllegalStateException("BLASTn output Parser - Already finished reading the stream");
		}
		
		lineNumber++;
		Map<String, String> meInfo = new HashMap<String, String>();
		
		try {
			List<String> data = PatternSplitter.toList(PatternSplitter.PTRN_TAB_SPLITTER, line);

			// qseqid - query (e.g., gene) sequence id
			meInfo.put("qseqid", data.get(0));
			
			// sseqid - subject (e.g., reference genome) sequence id
			meInfo.put("sseqid", data.get(1));

			// pident - percentage of identical matches
			meInfo.put("pident", data.get(2));
			
			// length - alignment length
			meInfo.put("length", data.get(3));

			// mismatch	- number of mismatches
			meInfo.put("mismatch", data.get(4));

			// gapopen - number of gap openings
			meInfo.put("gapopen", data.get(5));

			// qstart - start of alignment in query
			meInfo.put("qstart", data.get(6));

			// qend - end of alignment in query
			meInfo.put("qend", data.get(7));

			// sstart - start of alignment in subject
			meInfo.put("sstart", data.get(8));

			// send - end of alignment in subject
			meInfo.put("send", data.get(9));

			// evalue - expect value
			meInfo.put("evalue", data.get(10));

			// bitscore - bit score
			meInfo.put("bitscore", data.get(11));
			
			// Create and store alignment data
			BLASTAlignmentData alignment = new BLASTAlignmentData(meInfo);
			this.blastAlignments.add(alignment);
						
		} catch (RuntimeException e) {
			//System.out.println("Line #: " + lineNumber + " - Exception: " + e);
			throw new IllegalArgumentException("ERROR - Parsing: BLASTn output line #" + lineNumber + ": " + line, e);
		}
		
		return true;
	}
}
