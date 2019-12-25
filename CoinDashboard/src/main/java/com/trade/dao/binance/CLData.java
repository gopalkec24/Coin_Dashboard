package com.trade.dao.binance;

import java.math.BigDecimal;

public class CLData {
	
	long openTime;
	BigDecimal open;
	BigDecimal high;
	BigDecimal low;
	BigDecimal close;
	BigDecimal volume;
	long closeTime;
	BigDecimal quoteAssetVol;
	int trades;
	BigDecimal takerBuyBaseAssetVol;
	BigDecimal takerBuyQuoteAssetVol;
	
	public long getOpenTime() {
		return openTime;
	}
	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}
	public BigDecimal getOpen() {
		return open;
	}
	public void setOpen(BigDecimal open) {
		this.open = open;
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
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public long getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}
	public BigDecimal getQuoteAssetVol() {
		return quoteAssetVol;
	}
	public void setQuoteAssetVol(BigDecimal quoteAssetVol) {
		this.quoteAssetVol = quoteAssetVol;
	}
	public int getTrades() {
		return trades;
	}
	public void setTrades(int trades) {
		this.trades = trades;
	}
	public BigDecimal getTakerBuyBaseAssetVol() {
		return takerBuyBaseAssetVol;
	}
	public void setTakerBuyBaseAssetVol(BigDecimal takerBuyBaseAssetVol) {
		this.takerBuyBaseAssetVol = takerBuyBaseAssetVol;
	}
	public BigDecimal getTakerBuyQuoteAssetVol() {
		return takerBuyQuoteAssetVol;
	}
	public void setTakerBuyQuoteAssetVol(BigDecimal takerBuyQuoteAssetVol) {
		this.takerBuyQuoteAssetVol = takerBuyQuoteAssetVol;
	}
	
	

}
