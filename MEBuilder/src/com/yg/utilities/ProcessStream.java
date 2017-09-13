package com.yg.utilities;

import java.io.*;

import org.apache.commons.io.IOUtils;

/**
 * Custom class for handling input streams from CLI processes and flushing buffer
 * 
 * @author Yaroslava Girilishena
 *
 */
public class ProcessStream extends Thread {
	
	public InputStream is;
	public String type;
	public StringBuffer output;
	public BufferedReader br;
	
	private InputStreamReader isr;
	    
	public ProcessStream(InputStream is, String type) {
        this.is = is;
        this.type = type;
        this.output =  new StringBuffer();
    }
    
    public void run() {
    	try {
		    isr = new InputStreamReader(is);
		    br = new BufferedReader(isr);
		    output =  new StringBuffer();
		    
		    String line = null;
			while ( (line = br.readLine()) != null) {
				output.append(line + '\n'); 
			}
			// Close streams
			br.close();
			IOUtils.closeQuietly(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
		   
    public String getOutput() {
    	return output.toString();
    }
    
    public void cleanBuffer() {
    	output.setLength(0);
    }
}
