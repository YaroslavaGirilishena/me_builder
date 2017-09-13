package com.yg.parsers.vcf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import com.google.common.collect.ListMultimap;

/**
 * 
 * @author Yaroslava Girilishena
 *
 */
public class VcfMetadata {
	
	private String fileFormat;
	private String source;
	private List<String> alt;
	private List<String> info;
	private List<String> filter;
	private List<String> format;
	private List<String> columns;
	private List<String> samples;
	private ListMultimap<String, String> properties;

	public static final Pattern FILE_FORMAT_PATTERN = Pattern.compile("VCFv[\\d\\.]+");
	public static final int INFO_FORMAT_COLUMNS = 9;

	public VcfMetadata() {
		this.info = new ArrayList<String>();
		this.format = new ArrayList<String>();
		this.filter = new ArrayList<String>();
		this.alt = new ArrayList<String>();
		this.samples = new ArrayList<String>();
	}
	
	/**
	 * Sets the VCF version string
	 * @param fileFormat: e.g., "VCFv4.0"
	 */
	public void setFileFormat(String fileFormat) {
		if (!FILE_FORMAT_PATTERN.matcher(fileFormat).matches()) {
			throw new IllegalStateException("ERROR - Not a VCF file: fileformat is " +fileFormat);
		}
		this.fileFormat = fileFormat;
	}
	
	/**
	 * Sets the VCF source string
	 * @param source: e.g., "RetroSeq v1.41"
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * Sets the VCF raw property
	 * @param name = value
	 */
	public void addRawProperty(@Nonnull String name, @Nonnull String value) {
		this.properties.put(name, value);
	}

	/**
	 * Sets the VCF columns 
	 * @param cols
	 */
	public void setColumns(List<String> cols) {
		this.columns = cols;
	}

	/**
	 * Return column index
	 * @param column
	 * @return index (int)
	 */
	public int getColumnIndex(@Nonnull String column) {
		return this.columns.indexOf(column);
	}

	/**
	 * Return sample index
	 * Sample numbering starts at 0
	 */
	public int getSampleIndex(@Nonnull String sampleId) {
		return this.columns.indexOf(sampleId) - INFO_FORMAT_COLUMNS;
	}

	/**
	 * Gets the number of samples in the VCF file
	 */
	public int getNumSamples() {
		if (this.columns.size() < INFO_FORMAT_COLUMNS) {
			return 0; // necessary because if we have no samples, we'll be missing FORMAT
		}
		return this.samples.size();
	}

	/**
	 * Gets the sample name (column name)
	 *
	 * @param idx sample index, first sample is at index 0
	 * @throws ArrayIndexOutOfBoundsException If the sample doesn't exist
	 */
	public @Nonnull String getSampleName(int idx) {
		return this.samples.get(idx);
	}

	public List<String> getSamples() {
		return this.samples;
	}

	public void setSamples(List<String> samples) {
		this.samples = samples;
	}

	public void addInfo(String propValue) {
		this.info.add(propValue);
	}
	
	public void addFormat(String propValue) {
		this.format.add(propValue);
	}

	public void addAlt(String propValue) {
		this.alt.add(propValue);
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public String getSource() {
		return source;
	}
}
