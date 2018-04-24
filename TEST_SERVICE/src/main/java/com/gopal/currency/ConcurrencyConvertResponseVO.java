package com.gopal.currency;

import java.util.List;
import java.util.Map;

public class ConcurrencyConvertResponseVO {
	
	boolean success;
	String terms;
	String privacy;
	long timestamp;
	String source;
	
	CurrencyVO quotes;
	
	public boolean isSuccess() {
		return success;
	}
	public CurrencyVO getQuotes() {
		return quotes;
	}
	public void setQuotes(CurrencyVO quotes) {
		this.quotes = quotes;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
	
	

}
