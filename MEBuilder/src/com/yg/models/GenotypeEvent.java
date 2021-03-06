package com.yg.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Data model of event with genotype information
 * 
 * @author Yaroslava Girilishena
 *
 */
public class GenotypeEvent {

	private String chromosome;
	private long position;
	private List<String> ids = new ArrayList<>();
	private String ref;
	private List<String> alt = new ArrayList<>();
	private List<String> alleles = new ArrayList<>();
	private BigDecimal quality;
	private List<String> filter = new ArrayList<>();
	private ListMultimap<String, String> info = ArrayListMultimap.create();
	private List<String> format = new ArrayList<>();
	private List<String> genotypes = new ArrayList<>();
	
	// Map to store Sample-Genotype pair, e.g., < HG00096 : 0|0 >
	public Map<String, String> sampleGenotypeMap = new HashMap<String, String>();
	public Multimap<String, String> genotypeToSamplesMap = HashMultimap.create();
	
	
	/**
	 * Constructor
	 * 
	 * @param chromosome
	 * @param position
	 * @param ids
	 * @param ref
	 * @param alt
	 * @param quality
	 * @param filters
	 * @param info
	 * @param format
	 * @param sampleGenotypeMap
	 */
	public GenotypeEvent(String chromosome, long position, 
			List<String> ids, String ref, List<String> alt,
			BigDecimal quality, List<String> filters, 
			ListMultimap<String, String> info, List<String> format, 
			Map<String, String> sampleGenotypeMap) {
		
		this.chromosome = chromosome;
		this.position = position;
		this.ids = ids;
		this.ref = ref;
		this.alt = alt;
		this.quality = quality;
		this.filter = filters;
		this.info = info;
		this.format = format;
		this.sampleGenotypeMap = sampleGenotypeMap;
	
		// Invert - create values to keys map
		this.genotypeToSamplesMap = Multimaps.invertFrom(Multimaps.forMap(sampleGenotypeMap), HashMultimap.<String, String> create());
	}
	
	public String toString() {
		String toPrint = "";
		toPrint += "CHROM: " + this.chromosome + '\t' + 
				"POS: " + this.position + '\t' +
				"ID: " + this.ids + '\t' +
				"REF: " + this.ref + '\t' +
				"ALT: " + this.alt + '\t' +
				"QUAL: " + this.quality + '\t' +
				"FILTER: " + this.filter + '\t' +
				"INFO: " + this.info + '\t' +
				"FORMAT: " + this.format + '\t';
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

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public List<String> getAlt() {
		return alt;
	}

	public void setAlt(List<String> alt) {
		this.alt = alt;
	}

	public List<String> getAlleles() {
		return alleles;
	}

	public void setAlleles(List<String> alleles) {
		this.alleles = alleles;
	}

	public BigDecimal getQuality() {
		return quality;
	}

	public void setQuality(BigDecimal quality) {
		this.quality = quality;
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	public ListMultimap<String, String> getInfo() {
		return info;
	}

	public void setInfo(ListMultimap<String, String> info) {
		this.info = info;
	}

	public List<String> getFormat() {
		return this.format;
	}

	public void setFormat(List<String> format) {
		this.format = format;
	}
	
	public List<String> getGenotypes() {
		return this.genotypes;
	}

	public void setGenotypes(List<String> genotypes) {
		this.genotypes = genotypes;
	}
	
}
