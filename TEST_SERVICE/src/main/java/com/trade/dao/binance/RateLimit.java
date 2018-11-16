package com.trade.dao.binance;

public class RateLimit {
	
	String rateLimitType;
	String interval;
	int limit;
	int intervalNum;
	
	
	public int getIntervalNum() {
		return intervalNum;
	}
	public void setIntervalNum(int intervalNum) {
		this.intervalNum = intervalNum;
	}
	public String getRateLimitType() {
		return rateLimitType;
	}
	public void setRateLimitType(String rateLimitType) {
		this.rateLimitType = rateLimitType;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	
	
}
