package com.yg.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.yg.utilities.PatternSplitter;
/**
 * 
 * @author Yaroslava Girilishena
 *
 */
public class Variants {
	
	public static List<MEInsertion> listOfMEI = new ArrayList<MEInsertion>();
	
	public static List<GenotypeEvent> integratedEvents = new ArrayList<GenotypeEvent>();
	// Use Table to store two keys for one value: [chr; pos] = ME
	public static Table<String, Long, MEInsertion> insertionMEsTable = HashBasedTable.create();

	public static List<RefEvent> refEvents = new ArrayList<RefEvent>();
	
	public static Map<String, RefEvent> rmRefLocs = new HashMap<String, RefEvent>();
	
	public static void putInsertionME(MEInsertion me) {
		insertionMEsTable.put(me.getChromosome(), me.getPosition(), me);
	}
	
	public static void putRepeatMaskRefEvent(RefEvent refEvent) {
		List<String> types = PatternSplitter.toList(PatternSplitter.PTRN_COLON_SPLITTER, refEvent.getName());
		rmRefLocs.put(types.get(types.size()-1), refEvent);
	}
}
