package com.yg.models;

/**
 * This class stores information about contig alignment to the consensus database
 * 
 * @author Yaroslava Girilishena
 *
 */
public class ConsensusLocation {

	private String contigId;
	private String consensus;
	private long start;
	private long end;
	private char strand;
	
	// Constructors
	
	public ConsensusLocation() {}
	
	public ConsensusLocation(String contigId, String type, long start, long end) {
		this.contigId = contigId;
		this.consensus = type;
		this.start = start;
		this.end = end;
		this.strand = start < end ? '+' : '-';
	}
	
	// Getters and setters
	
	public String getConsensus() {
		return consensus;
	}
	public void setConsensus(String consensus) {
		this.consensus = consensus;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}

	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}
	
	public String getContigId() {
		return contigId;
	}

	public void setContigId(String contigId) {
		this.contigId = contigId;
	}

	public String toString() {
		return contigId + "|" + consensus + "|" + start + "-" + end;
	}
	
}
