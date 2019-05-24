package com.trade.dao.livecoin;

import java.math.BigDecimal;

public class LivecoinPriceStatusVO {

	BigDecimal last;
	BigDecimal high;
	BigDecimal low;
	BigDecimal volume;
	BigDecimal vwap;
	BigDecimal max_bid;
	BigDecimal min_ask;
	BigDecimal best_bid;
	BigDecimal best_ask;
	String symbol;
	String cur;
	
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public BigDecimal getLast() {
		return last;
	}
	public void setLast(BigDecimal last) {
		this.last = last;
	}
	public BigDecimal getHigh() {
		return high;
	}
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
	public BigDecimal getLow() {
		return low;
	}
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getVwap() {
		return vwap;
	}
	public void setVwap(BigDecimal vwap) {
		this.vwap = vwap;
	}
	public BigDecimal getMax_bid() {
		return max_bid;
	}
	public void setMax_bid(BigDecimal max_bid) {
		this.max_bid = max_bid;
	}
	public BigDecimal getMin_ask() {
		return min_ask;
	}
	public void setMin_ask(BigDecimal min_ask) {
		this.min_ask = min_ask;
	}
	public BigDecimal getBest_bid() {
		return best_bid;
	}
	public void setBest_bid(BigDecimal best_bid) {
		this.best_bid = best_bid;
	}
	public BigDecimal getBest_ask() {
		return best_ask;
	}
	public void setBest_ask(BigDecimal best_ask) {
		this.best_ask = best_ask;
	}
	
	
	
}
