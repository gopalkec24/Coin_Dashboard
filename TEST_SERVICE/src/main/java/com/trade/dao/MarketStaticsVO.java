package com.trade.dao;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;

public class MarketStaticsVO {
	
	int referenceId;
	long transactTime;
	BigDecimal lastPrice= TraderConstants.NEGATIVE_ONE;
	BigDecimal lowPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal highPrice= TraderConstants.NEGATIVE_ONE;
	public int getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}
	public long getTransactTime() {
		return transactTime;
	}
	public void setTransactTime(long transactTime) {
		this.transactTime = transactTime;
	}
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}
	public BigDecimal getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}
	public BigDecimal getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

}
