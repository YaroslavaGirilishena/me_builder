package com.yg.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * 
 * @author Yaroslava Girilishena
 *
 */

public class VcfDataStore {

	//private VcfMetadata m_metadata;
	private Map<String, MobileElement> idToPosition = new HashMap<>();
	private Map<Locus, MobileElement> locusToPosition = new HashMap<>();
	private Map<String, List<VcfMESample>> idToSamples = new HashMap<>();
	private Map<Locus, List<VcfMESample>> locusToSamples = new HashMap<>();
	  
	/**
	 * Getters and Setters
	 * 
	 */
	public Map<String, MobileElement> getIdToPosition() {
		return this.idToPosition;
	}
	
	public void setIdToPosition(Map<String, MobileElement> idToPosition) {
		this.idToPosition = idToPosition;
	}

	public Map<Locus, MobileElement> getLocusToPosition() {
		return this.locusToPosition;
	}

	public void setLocusToPosition(Map<Locus, MobileElement> locusToPosition) {
		this.locusToPosition = locusToPosition;
	}
	
	public Map<Locus, List<VcfMESample>> getLocusToSamples() {
		return this.locusToSamples;
	}
	  
	public void setLocusToSamples(Map<Locus, List<VcfMESample>> locusToSamples) {
		this.locusToSamples = locusToSamples;
	}
	
	public Map<String, List<VcfMESample>> getIdToSamples() {
		return this.idToSamples;
	}

	public void setIdToSamples(Map<String, List<VcfMESample>> idToSamples) {
		this.idToSamples = idToSamples;
	}
	
	/**
	 * Internal class Locus
	 */
	@Immutable	
	public static class Locus {
	    private final String chromosome;
	    private final long position;
	
	    public Locus(String chromosome, long position) {
	      this.chromosome = chromosome;
	      this.position = position;
	    }
	
	    public String getChromosome() {
	      return chromosome;
	    }
	
	    public long getPosition() {
	      return position;
	    }
	
	    @Override
	    public boolean equals(Object o) {
	      if (this == o) {
	        return true;
	      }
	      if (o == null || getClass() != o.getClass()) {
	        return false;
	      }
	      final Locus locus = (Locus)o;
	      return Objects.equals(position, locus.getPosition()) &&
	          Objects.equals(chromosome, locus.getChromosome());
	    }
	
	    @Override
	    public int hashCode() {
	      return Objects.hash(chromosome, position);
	    }
	
	    @Override
	    public String toString() {
	      return chromosome + ":" + position;
	    }
	}
}
