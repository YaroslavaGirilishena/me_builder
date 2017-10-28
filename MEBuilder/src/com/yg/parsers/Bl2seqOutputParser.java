package com.yg.parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.yg.utilities.PatternSplitter;

/**
 * Custom parser for bl2seq detailed output files
 * @author Yaroslava Girilishena
 *
 */
public class Bl2seqOutputParser {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private int lineNumber;				// number of current line being read
	private boolean alreadyFinished;
	private BufferedReader reader;
	private String filename;
	
	public boolean hitsFound;
	public int identities;
	public int queryLength;
	public char queryStrand;
	public int queryStart;
	public int queryEnd;
	public int subjectLength;
	public char subjectStrand;
	public int subjectStart;
	public int subjectEnd;
	
	public int subjectLeftover;
	public boolean checkIdentities = true;
	
	
	/*
	 * Constructor
	 */
	public Bl2seqOutputParser(String filename, boolean checkIdentities) {

    	this.filename = filename;
    	this.subjectLeftover = 0;
    	this.checkIdentities = checkIdentities;
    	
    	this.hitsFound = false;
    	this.queryStart = 0;
    	this.queryEnd = 0;
    	this.subjectStart = 0;
    	this.subjectEnd = 0;
    	this.identities = 0;
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
		// Read line with data
		String line = reader.readLine();
		if (line == null) {
			alreadyFinished = true;
			return false; // No more data to read
		}
		
		if (alreadyFinished) {
			// Prevents user errors from causing infinite loops
			throw new IllegalStateException("BL2SEQ output Parser - Already finished reading the stream");
		}
		
		lineNumber++;
		List<String> data;
		
		try {
			// If no hits found
			if (line.length() !=0 && line.contains("No hits found")) {
				this.hitsFound = false;
				return false;
			}
			
			// Get the identities %
			if (line.contains("Identities")) {
				// If this is the second alignment, we can return 
				if (this.hitsFound) {
					return false;
				}
				data = PatternSplitter.toList(PatternSplitter.PTRN_SPACES_SPLITTER, line.substring(0, line.indexOf('%')+2).trim());
				this.identities = Integer.parseInt(data.get(data.size()-1).substring(1, data.get(data.size()-1).length()-2));
				if (this.identities < 90) {
					this.hitsFound = false;
					return true;
				}
			}
			
			if (!this.checkIdentities || this.identities >= 90) {
				// Get the strand
				if (line.contains("Strand")) {
					data = PatternSplitter.toList(PatternSplitter.PTRN_SPACES_SPLITTER, line.trim());
					this.queryStrand = data.get(2).equals("Plus") ? '+' : '-';
					this.subjectStrand = data.get(4).equals("Plus") ? '+' : '-';
				}
				
				// Get the query start and end positions
				if (line.contains("Query:")) {
					data = PatternSplitter.toList(PatternSplitter.PTRN_SPACES_SPLITTER, line.trim());
					if (this.queryStart == 0) {
						this.queryStart = Integer.parseInt(data.get(1));
					}
					this.queryEnd = Integer.parseInt(data.get(data.size() - 1));
					this.hitsFound = true;
				}
				
				// Get the subject start and end positions
				if (line.contains("Sbjct:")) {
					data = PatternSplitter.toList(PatternSplitter.PTRN_SPACES_SPLITTER, line.trim());
					if (this.subjectStart == 0) {
						this.subjectStart = Integer.parseInt(data.get(1));
					}
					this.subjectEnd = Integer.parseInt(data.get(data.size() - 1));
					this.hitsFound = true;
				}
			} 
						
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("ERROR - Parsing: BL2SEQ output line #" + lineNumber + ": " + line, e);
		}
		
		return true;
	}
}

/* OUTPUT EXAMPLES: 
 
 
 ------------- HITS FOUND -------------
 
 
Query= Contig1
(133 letters)



>Contig5
 Length = 104

Score =  150 bits (78), Expect = 5e-42
Identities = 92/99 (92%)
Strand = Plus / Plus


Query: 1   ttttttgagacggagtctcgctctgtcgcccaggctggagtgcagtggcgggatctcggc 60
  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
Sbjct: 3   ttttttgagacggagtctcgctctgtcgcccaggctggagtgcagtggcgggatctcggc 62


Query: 61  tcactgcaagccctgccccccggggtcacgccattctcc 99
  || |||||| | ||||| ||| || |||| |||||||||
Sbjct: 63  tccctgcaacctctgcctccccggatcaccccattctcc 101


Lambda     K      H
1.33    0.621     1.12

Gapped
Lambda     K      H
1.33    0.621     1.12


Matrix: blastn matrix:1 -2
Gap Penalties: Existence: 5, Extension: 2
Number of Sequences: 1
Number of Hits to DB: 22
Number of extensions: 1
Number of successful extensions: 1
Number of sequences better than 1.0e-01: 1
Number of HSP's gapped: 1
Number of HSP's successfully gapped: 1
Length of query: 133
Length of database: 104
Length adjustment: 7
Effective length of query: 126
Effective length of database: 97
Effective search space:    12222
Effective search space used:    12222
X1: 11 (21.1 bits)
X2: 15 (28.8 bits)
X3: 52 (100.0 bits)
S1: 9 (18.0 bits)
S2: 9 (18.0 bits)



------------- MULTIPLE HITS -------------

Query= Contig62
         (639 letters)



>Contig70
          Length = 363

 Score = 85.3 bits (44), Expect = 5e-21
 Identities = 44/44 (100%)
 Strand = Plus / Plus


Query: 588 aaattttttttttttttttttttttttttttttttttttttttt 631
           ||||||||||||||||||||||||||||||||||||||||||||
Sbjct: 9   aaattttttttttttttttttttttttttttttttttttttttt 52



 Score = 81.4 bits (42), Expect = 7e-20
 Identities = 42/42 (100%)
 Strand = Plus / Plus


Query: 591 tttttttttttttttttttttttttttttttttttttttttg 632
           ||||||||||||||||||||||||||||||||||||||||||
Sbjct: 23  tttttttttttttttttttttttttttttttttttttttttg 64



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 13  ttttttttttttttttttttttttttttttttttttttttt 53



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 14  ttttttttttttttttttttttttttttttttttttttttt 54

Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 15  ttttttttttttttttttttttttttttttttttttttttt 55



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 16  ttttttttttttttttttttttttttttttttttttttttt 56



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 17  ttttttttttttttttttttttttttttttttttttttttt 57



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 18  ttttttttttttttttttttttttttttttttttttttttt 58

Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 19  ttttttttttttttttttttttttttttttttttttttttt 59



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 20  ttttttttttttttttttttttttttttttttttttttttt 60



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 21  ttttttttttttttttttttttttttttttttttttttttt 61



 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


Query: 591 ttttttttttttttttttttttttttttttttttttttttt 631
           |||||||||||||||||||||||||||||||||||||||||
Sbjct: 22  ttttttttttttttttttttttttttttttttttttttttt 62


 Score = 79.5 bits (41), Expect = 3e-19
 Identities = 41/41 (100%)
 Strand = Plus / Plus


 Score = 25.7 bits (13), Expect = 0.004
 Identities = 13/13 (100%)
 Strand = Plus / Plus


Query: 585 tgcaaattttttt 597
           |||||||||||||
Sbjct: 346 tgcaaattttttt 358



 Score = 21.8 bits (11), Expect = 0.059
 Identities = 15/17 (88%)
 Strand = Plus / Plus


Query: 589 aattttttttttttttt 605
           ||||||||| | |||||
Sbjct: 211 aatttttttgtattttt 227


Lambda     K      H
    1.33    0.621     1.12

Gapped
Lambda     K      H
    1.33    0.621     1.12


Matrix: blastn matrix:1 -2
Gap Penalties: Existence: 5, Extension: 2
Number of Sequences: 1
Number of Hits to DB: 758
Number of extensions: 74
Number of successful extensions: 74
Number of sequences better than 1.0e-01: 1
Number of HSP's gapped: 14
Number of HSP's successfully gapped: 14
Length of query: 639
Length of database: 363
Length adjustment: 10
Effective length of query: 629
Effective length of database: 353
Effective search space:   222037
Effective search space used:   222037
X1: 11 (21.1 bits)
X2: 15 (28.8 bits)
X3: 52 (100.0 bits)
S1: 11 (21.8 bits)
S2: 11 (21.8 bits)



------------- NO HITS -------------


Query= Contig1
         (133 letters)

 ***** No hits found ******


Lambda     K      H
    1.33    0.621     1.12

Gapped
Lambda     K      H
    1.33    0.621     1.12


Matrix: blastn matrix:1 -2
Gap Penalties: Existence: 5, Extension: 2
Number of Sequences: 1
Number of Hits to DB: 1
Number of sequences better than 1.0e-01: 0
Number of HSP's gapped: 0
Number of HSP's successfully gapped: 0
Length of query: 133
Length of database: 104
Length adjustment: 7
Effective length of query: 126
Effective length of database: 97
Effective search space:    12222
Effective search space used:    12222
X1: 11 (21.1 bits)
X2: 15 (28.8 bits)
X3: 52 (100.0 bits)
S1: 9 (18.0 bits)
S2: 9 (18.0 bits)


Query= Contig1
         (133 letters)
*/