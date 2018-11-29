package com.trade.dao.binance;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


public class BinanceExchangeInfo {
	
	private String timezone;
	long serverTime;
	List<RateLimit> rateLimits  ;
	List<SymbolDetails> symbols;
	
	List<Map<String,Object>> exchangeFilters;
	
	
	public List<Map<String, Object>> getExchangeFilters() {
		return exchangeFilters;
	}
	public void setExchangeFilters(List<Map<String, Object>> exchangeFilters) {
		this.exchangeFilters = exchangeFilters;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public long getServerTime() {
		return serverTime;
	}
	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}
	public List<RateLimit> getRateLimits() {
		return rateLimits;
	}
	public void setRateLimits(List<RateLimit> rateLimits) {
		this.rateLimits = rateLimits;
	}
	public List<SymbolDetails> getSymbols() {
		return symbols;
	}
	public void setSymbols(List<SymbolDetails> symbols) {
		this.symbols = symbols;
	}
	
	

}
