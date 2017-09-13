package com.yg.models;

/**
 * bl2seq output data model
 * @author Yaroslava Girilishena
 *
 */
public class Bl2seqOutputData {
	
	private boolean hitsFound;
	public double identities;
	public int alignmentLength;
	public String queryId;
	public int queryLength;
	public char queryStrand;
	public int queryStart;
	public int queryEnd;
	public String subjectId;
	public int subjectLength;
	public char subjectStrand;
	public int subjectStart;
	public int subjectEnd;
	public int subjectLeftover;
	
	public Bl2seqOutputData() {
		this.queryId = "";
		this.subjectId = "";
		this.setAlignmentLength(0);		
		this.hitsFound = false;
    	this.queryStart = 0;
    	this.queryEnd = 0;
    	this.subjectStart = 0;
    	this.subjectEnd = 0;
    	this.identities = 0;
    	this.subjectLeftover = 0;
	}

	public boolean isHitsFound() {
		return hitsFound;
	}

	public void setHitsFound(boolean hitsFound) {
		this.hitsFound = hitsFound;
	}

	public double getIdentities() {
		return identities;
	}

	public void setIdentities(double identities) {
		this.identities = identities;
	}

	public int getQueryLength() {
		return queryLength;
	}

	public void setQueryLength(int queryLength) {
		this.queryLength = queryLength;
	}

	public char getQueryStrand() {
		return queryStrand;
	}

	public void setQueryStrand(char queryStrand) {
		this.queryStrand = queryStrand;
	}

	public int getQueryStart() {
		return queryStart;
	}

	public void setQueryStart(int queryStart) {
		this.queryStart = queryStart;
	}

	public int getQueryEnd() {
		return queryEnd;
	}

	public void setQueryEnd(int queryEnd) {
		this.queryEnd = queryEnd;
	}

	public int getSubjectLength() {
		return subjectLength;
	}

	public void setSubjectLength(int subjectLength) {
		this.subjectLength = subjectLength;
	}

	public char getSubjectStrand() {
		return subjectStrand;
	}

	public void setSubjectStrand(char subjectStrand) {
		this.subjectStrand = subjectStrand;
	}

	public int getSubjectStart() {
		return subjectStart;
	}

	public void setSubjectStart(int subjectStart) {
		this.subjectStart = subjectStart;
	}

	public int getSubjectEnd() {
		return subjectEnd;
	}

	public void setSubjectEnd(int subjectEnd) {
		this.subjectEnd = subjectEnd;
	}

	public int getSubjectLeftover() {
		return subjectLeftover;
	}

	public void setSubjectLeftover(int subjectLeftover) {
		this.subjectLeftover = subjectLeftover;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public int getAlignmentLength() {
		return alignmentLength;
	}

	public void setAlignmentLength(int alignmentLength) {
		this.alignmentLength = alignmentLength;
	}
	
	public String toString() {
		String out = this.queryId + "\t"
				+ this.subjectId + "\t" 
				+ this.identities + '\t'
				+ this.alignmentLength + '\t'
				+ this.queryStrand + '\t'
				+ this.queryStart + '\t'
				+ this.queryEnd + '\t'
				+ this.subjectStrand + '\t'
				+ this.subjectStart + '\t'
				+ this.subjectEnd + '\t'
				+ this.subjectLeftover;
		
		return out;
	}
}
