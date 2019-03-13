package com.trade.dao;

import java.util.List;

public class AutoTradeVO {

	long lastUpdatedTime;
	List<TradeDataVO> tradeData;
	public long getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(long lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public List<TradeDataVO> getTradeData() {
		return tradeData;
	}
	public void setTradeData(List<TradeDataVO> tradeData) {
		this.tradeData = tradeData;
	}
	
	
}
