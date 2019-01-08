package com.portfolio.dao;

import java.math.BigDecimal;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnySetter;

public class CmpVO {

	int id;
	
	String name;
	
	String symbol;
	
	String slug;
	
	BigDecimal circulating_supply;
	BigDecimal total_supply;
	BigDecimal max_supply;
	int cmc_rank;
	
	Map<String,Object> quotes;
	
	@JsonAnySetter
	public void add(String key,Object value) {
		quotes.put(key, value);
	}
	
}
