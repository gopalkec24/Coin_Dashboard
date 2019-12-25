package com.trade.dao.bitbns;

import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.annotate.JsonAnySetter;

public class BitbnsTickerDetailsVO {

	Map<String,BitbnsTickerVO> data =new TreeMap<String, BitbnsTickerVO>();

	
	@JsonAnySetter
	 void setData(String key, BitbnsTickerVO value) {
		data.put(key, value);
    }
	
	public Map<String, BitbnsTickerVO> getData() {
		return data;
	}

	public void setData(Map<String, BitbnsTickerVO> data) {
		this.data = data;
	}
	
	
}
