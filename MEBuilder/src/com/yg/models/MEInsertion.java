package com.yg.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Mobile element insertion data model
 * 
 * @author Yaroslava Girilishena
 *
 */
public class MEInsertion extends MobileElement{

	private long startPos = -1;
	private long endPos = -1;

	private String typeOfMEI; // subtype of MEI, e.g. ALuYa5
	private String sequence; // actual sequence of insertion
	private int genotype; // not calculated in this version
	private int length; // length of insertion: can be omitted or used as approximation 
	private Boolean reference;
	private String consensusSeq; 
	private char strand; // strand +/-
	private String TSD; // TSD
	private String IMD; // IMD
	private String transduction5; // 5TR
	private String transduction3; // 3TR
	private String flankingL; // left flanking
	private String flankingR; // right flanking
	private List<ConsensusLocation> consensusAlignments;
	private List<FASTASeq> contigs = new ArrayList<>();
	private boolean full = false;
	private boolean partialChar = false;
	
	// for stats 
	public int rightSR; // number of split-reads at 3' end
	public int leftSR; // number of split-reads at 5' end
	public int discR; // number of discordant reads
	public int rawReads; // number of all raw reads that cover the region of interest

	
	/**
	 * Empty constructor
	 */
	public MEInsertion() {
		super();
		this.startPos = -1;
		this.endPos = -1;
		this.typeOfMEI = "Undef";
		initData();
	}
	
	/**
	 * Constructor
	 * @param start
	 * @param end
	 * @param typeOfMEI
	 */
	public MEInsertion(String typeOfMEI, long start, long end) {
		super(typeOfMEI, start);
		this.startPos = start;
		this.endPos = end;
		this.typeOfMEI = typeOfMEI;
		initData();
	}
	
	/**
	 * Reset data
	 */
	private void initData() {
		this.flankingL = "";
		this.flankingR = "";
		this.TSD = "";
		this.IMD = "";
		this.transduction5 = "";
		this.transduction3 = "";
		this.consensusAlignments = new ArrayList<ConsensusLocation>();
		this.full = false;
		this.partialChar = false;
		this.leftSR = 0;
		this.rightSR = 0;
		this.discR = 0;
	}
	
	/**
	 * Getters and Setters
	 * 
	 */
	
	// Left flanking + transduction check
	public String getFlankingL() {
		if (flankingL == null || flankingL.equals("")) {
			return "";
		}
		return flankingL;
	}
	
	public void setFlankingL(String flanking) {
		this.flankingL = flanking;
	}
	
	// Right flanking + transduction check
	public String getFlankingR() {
		if (flankingR == null || flankingR.equals("")) {
			return "";
		}
		return flankingR;
	}
	
	public void setFlankingR(String flanking) {
		this.flankingR = flanking;
	}

	// Start position
	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	// End position
	public long getEndPos() {
		if (endPos < 0)
			return startPos;
		else
			return endPos;
	}

	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}

	// Type of MEI
	public String getTypeOfMEI() {
		return typeOfMEI;
	}
	
	public void setTypeOfMEI(String typeOfMEI) {
		this.typeOfMEI = typeOfMEI;
	}
	
	// Insertion sequence
	public String getSequence() {
		return sequence;
	}
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	// TSD
	public String getTSD() {
		if (TSD == null || TSD.equals("")) {
			return "";
		}
		return TSD;
	}
	
	public void setTSD(String tsd) {
		this.TSD = tsd;
	}
	
	// IMD
	public String getIMD() {
		if (IMD == null || IMD.equals("")) {
			return "";
		}
		return IMD;
	}
	
	public void setIMD(String iMD) {
		IMD = iMD;
	}

	// 5' Trasnduction 
	public String getTransduction5() {
		if (transduction5 == null || transduction5.equals("")) {
			return "";
		}
		return transduction5;
	}
	
	public void setTransduction5(String transduction5) {
		this.transduction5 = transduction5;
	}
	
	// 3' Trasnduction 
	public String getTransduction3() {
		if (transduction3 == null || transduction3.equals("")) {
			return "";
		}
		return transduction3;
	}

	public void setTransduction3(String transduction3) {
		this.transduction3 = transduction3;
	}
	
	// Consensus sequence
	public String getConsensusSeq() {
		return consensusSeq;
	}
	
	public void setConsensusSeq(String consensusSeq) {
		this.consensusSeq = consensusSeq;
	}
	
	// Genotype 
	public int getGenotype() {
		return genotype;
	}
	
	public void setGenotype(int genotype) {
		this.genotype = genotype;
	}
	
	// Insertion length
	public int getLength() {
		if (length == 0 && sequence.length() != 0) {
			return sequence.length();
		}
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	// Reference
	public Boolean getReference() {
		return reference;
	}
	
	public void setReference(Boolean reference) {
		this.reference = reference;
	}

	// Consensus alignments
	public List<ConsensusLocation> getConsensusAlignments() {
		return consensusAlignments;
	}

	public void setConsensusAlignments(List<ConsensusLocation> consensusAlignments) {
		this.consensusAlignments = consensusAlignments;
	}

	// Breakpoint contigs
	public List<FASTASeq> getContigs() {
		return contigs;
	}

	public void setContigs(List<FASTASeq> contigs) {
		this.contigs = contigs;
	}

	// Strand
	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}

	// Full sequence characterized
	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

	public boolean isPartialChar() {
		return partialChar;
	}

	public void setPartialChar(boolean partialChar) {
		this.partialChar = partialChar;
	}

}
