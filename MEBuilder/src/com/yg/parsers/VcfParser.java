package com.yg.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.yg.models.MEInsertion;
import com.yg.models.Variants;
import com.yg.models.VcfMetadata;
import com.yg.utilities.PatternSplitter;

/**
 * This class parses .vcf files with MEIs and store these events in the global list
 * 
 * @author Yaroslava Girilishena
 *
 */
public class VcfParser {
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // init logger

	private int lineNumber;				// number of current line being read
	private boolean alreadyFinished;	
	private BufferedReader reader;
	private VcfMetadata vcfMetadata; 
	
	/**
	 * Constructor; Setup global reader
	 */
	public VcfParser(@Nonnull BufferedReader reader) {
		this.reader = reader;
	}
	
	/**
	 * Parses the entire VCF file
	 */
	public void parse() throws IOException {
		boolean hasNext = true;
		while (hasNext) {
			hasNext = parseNextLine();
		}
		IOUtils.closeQuietly(reader);
	}
	
	/**
	 * Parse each line of VCF file
	 * @return boolean - does next line exist
	 * @throws IOException
	 */
	public boolean parseNextLine() throws IOException {
		// Check for metadata
//		if (vcfMetadata == null) {
//			parseMetadata();
//		}
			
		// Read line with data
		String line = reader.readLine();
		
		if (line == null) {
			System.out.println("VCF Parser - Finished reading");
			alreadyFinished = true;
			return false; // No more data to read
		}
		
		if (alreadyFinished) {
			// Prevents user errors from causing infinite loops
			throw new IllegalStateException("Already finished reading the stream");
		}
		
		// Skip header lines for now
		while (line.equals("") || line.startsWith("#")) {
			line = reader.readLine();
		}
		
		lineNumber++;
		
		try {
			MEInsertion me = new MEInsertion();
			
			List<String> data = PatternSplitter.toList(PatternSplitter.PTRN_TAB_SPLITTER, line);

			// CHROM
			if (data.get(0).equals("")) {
				return true;
			}
			me.setChromosome(data.get(0));
			
			// POS
			try {
				me.setPosition(Long.parseLong(data.get(1)));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("ERROR - Parsing: position " + data.get(1) + " is not numerical");
			}
			
			// ID
			/*List<String> ids = null;
			if (!data.get(2).equals(".")) {
				ids = PatternSplitter.toList(PatternSplitter.PTRN_SEMICOLON_SPLITTER, data.get(2));
			}*/
			
			// REF
			if (!data.get(3).equals(".")) {
				me.setRef(data.get(3));
			}
			
			// ALT
			/*List<String> alt = null;
			if (!data.get(7).isEmpty() && !data.get(4).equals(".")) {
				alt = PatternSplitter.toList(PatternSplitter.PTRN_COMMA_SPLITTER, data.get(4));
			}*/
			
			// QUAL
			if (!data.get(5).isEmpty() && !data.get(5).equals(".")) {
				me.setQuality(new BigDecimal(data.get(5)));
			}
			
			// FILTER
			/*List<String> filters = null;
			if (!data.get(6).equals("PASS") && !data.get(6).equals(".")) {
				filters = PatternSplitter.toList(PatternSplitter.PTRN_SEMICOLON_SPLITTER, data.get(6));
			}*/
			
			// INFO
			ListMultimap<String, String> info = null;
			if (!data.get(7).equals("") && !data.get(7).equals(".")) {
				info = ArrayListMultimap.create();
				List<String> props = PatternSplitter.toList(PatternSplitter.PTRN_SEMICOLON_SPLITTER, data.get(7));
				for (String prop : props) {
					int idx = prop.indexOf('=');
					if (idx == -1) {
						info.put(prop, "");
					} else {
						String key = prop.substring(0, idx);
						String value = prop.substring(idx + 1);
						info.putAll(key, PatternSplitter.toList(PatternSplitter.PTRN_COMMA_SPLITTER, value));
					}	
				}
				me.setInfo(info);
			}
			
			// FORMAT
			/*List<String> format = null;
			if (data.size() >= 9 && data.get(8) != null) {
				format = PatternSplitter.toList(PatternSplitter.PTRN_COLON_SPLITTER, data.get(8));
			}*/
			
			// Properties of ME samples
			/*List<VcfMESample> samples = new ArrayList<>();
			for (int i = 9; i < data.size(); i++) {
				//List<String> genotypeSplit = toList(PTRN_SLASH_SPLITTER, data.get(i));
				List<String> values = PatternSplitter.toList(PatternSplitter.PTRN_COLON_SPLITTER, data.get(i));
				VcfMESample sample = new VcfMESample(format, values);
				// Assuming all data is filtered before
				samples.add(sample);
			}*/
			
			// Store ME data
			Variants.listOfMEI.add(me);
						
		} catch (RuntimeException e) {
			System.out.println("Line #: " + lineNumber + " - Exception: " + e);
			throw new IllegalArgumentException("ERROR - Parsing: VCF data line #" + lineNumber + ": " + line, e);
		}
		
		return true;
	}
	

	/**
	 * Parses entire metadata (header)
	 */
	public void parseMetadata() throws IOException {

		if (vcfMetadata != null) {
			throw new IllegalStateException("ERROR - Metadata has already been parsed");
		}
		vcfMetadata = new VcfMetadata();
		
		String line;
		while ((line = reader.readLine()) != null) {
			lineNumber++;
			if (line.startsWith("##")) {
				try {
					parseHeaderMetadata(line);
				} catch (RuntimeException e) {
					throw new IllegalArgumentException("Error parsing (## header) metadata on line #" + lineNumber + ": " + line, e);
				}
			} else if (line.startsWith("#")) {
				try {
					parseColumnInfo(line);
				} catch (RuntimeException e) {
					throw new IllegalArgumentException("Error parsing column (# header) on line #" + lineNumber + ": " + line, e);
				}
				break;
			}
		}
	}
	 
	/**
	 * Gets VCF metadata (if it has already been parsed)
	 */
	public @Nullable VcfMetadata getMetadata() {
		return this.vcfMetadata;
	}

	/**
	 * Parses a metadata line that starts with ##
	 * @param line
	 */
	private void parseHeaderMetadata(@Nonnull String line) {

		int idx = line.indexOf("=");
		String propName = line.substring(2, idx).trim();
		String propValue = line.substring(idx + 1).trim();

		switch (propName) {
		case "fileformat":
			vcfMetadata.setFileFormat(propValue);
			break;
		case "source":
			vcfMetadata.setSource(propValue);
			break;
		case "ALT":
			vcfMetadata.addAlt(propValue);
			break;
		case "INFO":
			vcfMetadata.addInfo(propValue);
			break;
		case "FORMAT":
			vcfMetadata.addFormat(propValue);
			break;

		default:
			vcfMetadata.addRawProperty(propName, propValue);
		}
	}
	
	/**
	 * Parses a metadata line that starts with #
	 * @param line
	 */
	private void parseColumnInfo(@Nonnull String line) {
		String lineWithValues = line.substring(1).trim();
		// The VCF file should be tab-delimited by standards 
		List<String> cols = PatternSplitter.toList(PatternSplitter.PTRN_TAB_SPLITTER, lineWithValues);
		
		vcfMetadata.setColumns(PatternSplitter.toList(PatternSplitter.PTRN_SPACES_SPLITTER, cols.get(0)));
		vcfMetadata.setSamples(cols.subList(1, cols.size()));
	}
}
