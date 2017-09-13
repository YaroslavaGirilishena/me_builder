package com.yg.models;

/**
 * Fasta sequence model: 
 * >description
 * sequence
 * @author Yaroslava Girilishena
 *
 */
public class FASTASeq {
	
	private String description;
	private String sequence;
	
	public FASTASeq () {
		this.description = "";
		this.sequence = "";
	}
	
	public FASTASeq (String desc, String seq) {
		this.description = desc;
		this.sequence = seq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public String toString() {
		String faprint = description + '\n' + sequence + '\n';
		return faprint;
	}
	
	public String toPrint() {
		String faprint = '>' + description + '\n' + sequence + '\n';
		return faprint;
	}
	
}
