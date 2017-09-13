package com.yg.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ssplitting strings using any pattern
 * 
 * @author Yaroslava Girilishena
 *
 */
public class PatternSplitter {
	
	/** Setup patterns to match (find, filter) the input data
	 * A Pattern object is a compiled representation of a regular expression
	 */
	public static final Pattern PTRN_TAB_SPLITTER = Pattern.compile("\t");
	public static final Pattern PTRN_NEWLINE_SPLITTER = Pattern.compile("\n");
	public static final Pattern PTRN_COMMA_SPLITTER = Pattern.compile(",");
	public static final Pattern PTRN_COLON_SPLITTER = Pattern.compile(":");
	public static final Pattern PTRN_SEMICOLON_SPLITTER = Pattern.compile(";");
	public static final Pattern PTRN_SPACES_SPLITTER = Pattern.compile("\\s+");
	public static final Pattern PTRN_VERTI_BAR_SPLITTER = Pattern.compile("|");
	public static final Pattern PTRN_NUMBERS_LETTERS_SPLITTER = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

	public static final Pattern SOFT_CLIPPED_5_MS = Pattern.compile("\\d+M\\d+S$");
	public static final Pattern SOFT_CLIPPED_3_SM = Pattern.compile("^\\d+S\\d+M");
	public static final Pattern FULL_MATCH_M = Pattern.compile("^\\d+M$");
	
	public static final Pattern CHROMOSOME_VALID = Pattern.compile("^chr[0-9XY]+$");

	/**
	 * Convert line with patterns into List
	 * @param pattern
	 * @param string
	 * @return List of substrings
	 */
	public static List<String> toList(@Nonnull Pattern pattern, @Nullable String string) {
		List<String> list = new ArrayList<>();
		Collections.addAll(list, pattern.split(string));
		return list;
	}
}
