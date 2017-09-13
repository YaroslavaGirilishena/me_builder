package com.yg.models;

import java.math.BigDecimal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
/**
 * Mobile elemnt general data model
 * 
 * @author Yaroslava Girilishena
 *
 */
public class MobileElement {

	private String chromosome;
	private long position;
	private char orientation;
	private long contig1AlignLength;
	private long contig2AlignLength;

	private String ref;
	private BigDecimal quality;
	private ListMultimap<String, String> info = ArrayListMultimap.create();

	//private List<String> ids = new ArrayList<>();
	//private List<String> alt = new ArrayList<>();
	//private List<String> alleles = new ArrayList<>();
	//private List<String> filter = new ArrayList<>();
	//private List<String> format = new ArrayList<>();
	//private List<VcfMESample> samples = new ArrayList<>();
	
	// To store supporting .bed files (to search for raw reads)
	//public List<String> bedFiles = new ArrayList<>();
	
	
	public MobileElement() {}
	
	public MobileElement(String chromosome, long position) {
		this.chromosome = chromosome;
		this.position = position;
	}
	
	/**
	 * 
	 * @param chromosome
	 * @param position
	 * @param ref
	 * @param quality
	 * @param info
	 */
	public MobileElement(String chromosome, long position, String ref,
			BigDecimal quality, ListMultimap<String, String> info) {
		
		this.chromosome = chromosome;
		this.position = position;
		this.ref = ref;
		this.quality = quality;
		this.info = info;
	}
	
	/**
	 * 
	 */
	public String toString() {
		String toPrint = "";
		toPrint += "CHROM: " + this.chromosome + '\t' + 
				"POS: " + this.position + '\t' +
				"REF: " + this.ref + '\t' +
				"QUAL: " + this.quality + '\t' +
				"INFO: " + this.info + '\t';
		return toPrint;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public char getOrientation() {
		return orientation;
	}

	public void setOrientation(char orientation) {
		this.orientation = orientation;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public BigDecimal getQuality() {
		return quality;
	}

	public void setQuality(BigDecimal quality) {
		this.quality = quality;
	}

	public ListMultimap<String, String> getInfo() {
		return info;
	}

	public void setInfo(ListMultimap<String, String> info) {
		this.info = info;
	}

	public long getContig1AlignLength() {
		return contig1AlignLength;
	}

	public void setContig1AlignLength(long contig1AlignStart) {
		this.contig1AlignLength = contig1AlignStart;
	}

	public long getContig2AlignLength() {
		return contig2AlignLength;
	}

	public void setContig2AlignLength(long contig2AlignStart) {
		this.contig2AlignLength = contig2AlignStart;
	}
	
	
}
