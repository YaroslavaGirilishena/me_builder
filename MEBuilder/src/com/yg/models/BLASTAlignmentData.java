package com.yg.models;

import java.util.Map;

/**
 * blastn output data model
 * @author Yaroslava Girilishena
 *
 */

public class BLASTAlignmentData {
	
	private String qseqid; // query (e.g., gene) sequence id
	private String sseqid; // subject (e.g., reference genome) sequence id
	private Double pident; // percentage of identical matches
	private Integer length; // alignment length
	private Integer mismatch; // number of mismatches
	private Integer gapopen; // number of gap openings
	private long qstart; // start of alignment in query
	private long qend; // end of alignment in query
	private long sstart; // start of alignment in subject
	private long send; // end of alignment in subject
	private Double evalue; // expect value
	private Double bitscore; // bit score
	
	public BLASTAlignmentData(Map<String, String> data) {
		this.qseqid = data.get("qseqid");
		this.sseqid = data.get("sseqid");
		this.pident = Double.parseDouble(data.get("pident"));
		this.length = Integer.parseInt(data.get("length"));
		this.mismatch = Integer.parseInt(data.get("mismatch"));
		this.gapopen = Integer.parseInt(data.get("gapopen"));
		this.qstart = Long.parseLong(data.get("qstart"));
		this.qend = Long.parseLong(data.get("qend"));
		this.sstart = Long.parseLong(data.get("sstart"));
		this.send = Long.parseLong(data.get("send"));
		this.evalue = Double.parseDouble(data.get("evalue"));
		this.bitscore = Double.parseDouble(data.get("bitscore"));
	}

	public String getQseqid() {
		return qseqid;
	}

	public void setQseqid(String qseqid) {
		this.qseqid = qseqid;
	}

	public String getSseqid() {
		return sseqid;
	}

	public void setSseqid(String sseqid) {
		this.sseqid = sseqid;
	}

	public Double getPident() {
		return pident;
	}

	public void setPident(Double pident) {
		this.pident = pident;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getMismatch() {
		return mismatch;
	}

	public void setMismatch(Integer mismatch) {
		this.mismatch = mismatch;
	}

	public Integer getGapopen() {
		return gapopen;
	}

	public void setGapopen(Integer gapopen) {
		this.gapopen = gapopen;
	}

	public long getQstart() {
		return qstart;
	}

	public void setQstart(long qstart) {
		this.qstart = qstart;
	}

	public long getQend() {
		return qend;
	}

	public void setQend(long qend) {
		this.qend = qend;
	}

	public long getSstart() {
		return sstart;
	}

	public void setSstart(long sstart) {
		this.sstart = sstart;
	}

	public long getSend() {
		return send;
	}

	public void setSend(long send) {
		this.send = send;
	}

	public Double getEvalue() {
		return evalue;
	}

	public void setEvalue(Double evalue) {
		this.evalue = evalue;
	}

	public Double getBitscore() {
		return bitscore;
	}

	public void setBitscore(Double bitscore) {
		this.bitscore = bitscore;
	}
	
	public String toString() {
		String out = "";
		out += qseqid + "\t" + sseqid + "\t" + pident + "\t" + qstart + "\t" + qend + "\t" + sstart + "\t" + send;
		return out;
	}
}
