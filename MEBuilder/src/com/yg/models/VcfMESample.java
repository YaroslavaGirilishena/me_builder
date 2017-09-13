package com.yg.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author Yaroslava Girilishena
 *
 */
public class VcfMESample {
	
	private LinkedHashMap<String, String> properties = new LinkedHashMap<>();


	public VcfMESample(List<String> keys, List<String> values) {
		if (keys == null) {
			if (values == null || values.size() == 0) {
				return;
			}
			throw new IllegalArgumentException("ERROR - VcfMESample init: keys are null but values are not");
		} else if (values == null) {
			throw new IllegalArgumentException("ERROR - VcfMESample init: values are null but keys are not");
		}
		
//		if (keys.size() != values.size()) {
//			throw new IllegalArgumentException("ERROR - VcfMESample init: number of keys  does not match number of values");
//		}
		
// TODO: - Consider putting 0 when number of values is 4 (CN = 0)
		
		for (int i = 0; i < values.size(); i++) {
			this.properties.put(keys.get(i), values.get(i));
		}
		validate();
	}
	
	public VcfMESample(LinkedHashMap<String, String> properties) {
		if (properties == null) {
			throw new IllegalArgumentException("ERROR - VcfMESample init: properties are null");
		}
		this.properties = properties;
		validate();
	}
	 
	private void validate() {
		for (Map.Entry<String, String> entry : this.properties.entrySet()) {
	      if (entry.getKey().contains("\n") || entry.getValue().contains("\n")) {
	        throw new IllegalArgumentException("ERROR - VcfMESample Validate: FORMAT [" + entry.getKey() + " = " + entry.getValue() + "] contains a newline");
	      }
	    }
	}
	
	public String getProperty(String key) {
		if (key == null) {
			throw new IllegalArgumentException("ERROR - VcfMESample getProperty: key is null");
		}
		return this.properties.get(key);
	}
	
	@Override
	public String toString() {
		String res = "";
		for (Map.Entry<String, String> entry : this.properties.entrySet()) {
			res += entry.getKey() + " = " + entry.getValue() + "; \t";
	    }
		return res;
	}
}
