package com.trade.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.trade.constants.TraderConstants;

public class TradeDataVO {
	
	int transactionType=TraderConstants.INVALID_CALL;
	
	boolean lastBuyCall= false;
	boolean lastSellCall = false;
	
	//price related 
	BigDecimal lastPrice= TraderConstants.NEGATIVE_ONE;
	BigDecimal lowPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal highPrice= TraderConstants.NEGATIVE_ONE;
	BigDecimal triggeredPrice= TraderConstants.NEGATIVE_ONE;
	BigDecimal previousLastPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal orderTriggeredPrice= TraderConstants.NEGATIVE_ONE;
	
	BigDecimal tradeCurrencyVolume= TraderConstants.NEGATIVE_ONE;
	BigDecimal coinVolume = TraderConstants.NEGATIVE_ONE;
	
	int profitType=1;
	
	long triggerTime;
	
	String atOrderId;
	
	String remarks;
	
	
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAtOrderId() {
		return atOrderId;
	}

	public void setAtOrderId(String atOrderId) {
		this.atOrderId = atOrderId;
	}

	public long getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(long triggerTime) {
		this.triggerTime = triggerTime;
	}

	public int getProfitType() {
		return profitType;
	}

	public void setProfitType(int profitType) {
		this.profitType = profitType;
	}

	public BigDecimal getPreviousLastPrice() {
		return previousLastPrice;
	}

	public void setPreviousLastPrice(BigDecimal previousLastPrice) {
		this.previousLastPrice = previousLastPrice;
	}
	String coin;
	String currency;
	String exchange;
	
	int triggerEvent = TraderConstants.COUNTER_NOINIT;
	
	int waitCount = TraderConstants.COUNTER_NOINIT;
	int highCount = TraderConstants.COUNTER_NOINIT;
	int lowCount = TraderConstants.COUNTER_NOINIT;
	
	int retryLoopCount = TraderConstants.COUNTER_NOINIT;
	int reTriggerCount = TraderConstants.COUNTER_NOINIT;
	
	boolean placeAvgPriceOrder = false;
	
	
	
	public boolean isPlaceAvgPriceOrder() {
		return placeAvgPriceOrder;
	}

	public void setPlaceAvgPriceOrder(boolean placeAvgPriceOrder) {
		this.placeAvgPriceOrder = placeAvgPriceOrder;
	}
	List<MarketStaticsVO> priceHistory = new ArrayList<MarketStaticsVO>();
	List<BigDecimal> triggerPriceHistory  = new ArrayList<BigDecimal>();
	
	public void addTriggerPriceHistory(BigDecimal price) {
		if(this.triggerPriceHistory== null) {
			this.triggerPriceHistory = new ArrayList<BigDecimal>();
		}
		this.triggerPriceHistory.add(price);
	}
	
	public int getReTriggerCount() {
		return reTriggerCount;
	}

	public void setReTriggerCount(int reTriggerCount) {
		this.reTriggerCount = reTriggerCount;
	}

	public List<BigDecimal> getTriggerPriceHistory() {
		return triggerPriceHistory;
	}

	public void setTriggerPriceHistory(List<BigDecimal> triggerPriceHistory) {
		this.triggerPriceHistory = triggerPriceHistory;
	}

	public void addPriceHistory(MarketStaticsVO staticsVO) {
		if(this.priceHistory== null) {
			this.priceHistory = new ArrayList<MarketStaticsVO>();
		}
		
		this.priceHistory.add(staticsVO);
		
	}
	
	public List<MarketStaticsVO> getPriceHistory() {
		return priceHistory;
	}

	public void setPriceHistory(List<MarketStaticsVO> priceHistory) {
		this.priceHistory = priceHistory;
	}
	
	
	public TradeDataVO(String exchange,String coinName,String currency,BigDecimal coinVol,BigDecimal currencyVolume)
	{
		this.exchange = exchange;
		this.coin=coinName;
		this.currency = currency;
		this.coinVolume = coinVol;
		this.tradeCurrencyVolume = currencyVolume;		
		initalizeTransactionType();	
		
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	private void initalizeTransactionType() {
		this.transactionType = this.coinVolume.compareTo(TraderConstants.BIGDECIMAL_ZERO) == 0 ? TraderConstants.BUY_CALL : TraderConstants.SELL_CALL;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public boolean isLastBuyCall() {
		return lastBuyCall;
	}

	public void setLastBuyCall(boolean lastBuyCall) {
		this.lastBuyCall = lastBuyCall;
	}

	public boolean isLastSellCall() {
		return lastSellCall;
	}

	public void setLastSellCall(boolean lastSellCall) {
		this.lastSellCall = lastSellCall;
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

	public BigDecimal getTriggeredPrice() {
		return triggeredPrice;
	}

	public void setTriggeredPrice(BigDecimal triggeredPrice) {
		this.triggeredPrice = triggeredPrice;
	}

	public BigDecimal getOrderTriggeredPrice() {
		return orderTriggeredPrice;
	}

	public void setOrderTriggeredPrice(BigDecimal orderTriggeredPrice) {
		this.orderTriggeredPrice = orderTriggeredPrice;
	}

	public BigDecimal getTradeCurrencyVolume() {
		return tradeCurrencyVolume;
	}

	public void setTradeCurrencyVolume(BigDecimal tradeCurrencyVolume) {
		this.tradeCurrencyVolume = tradeCurrencyVolume;
	}

	public BigDecimal getCoinVolume() {
		return coinVolume;
	}

	public void setCoinVolume(BigDecimal coinVolume) {
		this.coinVolume = coinVolume;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getTriggerEvent() {
		return triggerEvent;
	}

	public void setTriggerEvent(int triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	public int getWaitCount() {
		return waitCount;
	}

	public void setWaitCount(int waitCount) {
		this.waitCount = waitCount;
	}

	public int getHighCount() {
		return highCount;
	}

	public void setHighCount(int highCount) {
		this.highCount = highCount;
	}

	public int getLowCount() {
		return lowCount;
	}

	public void setLowCount(int lowCount) {
		this.lowCount = lowCount;
	}

	@Override
	public String toString() {
		
		return "Coin Name : "+this.coin+" Currency :"+this.currency
				+" Exchange : "+this.exchange +" Transaction Type : " +this.transactionType +
				" Trigger Condition : " +this.triggerEvent +" Waiting Count : " +this.waitCount+
				" Last Price : "+this.lastPrice +" Triggered Price : "+this.triggeredPrice;
	}
	
	public void increaseHigherCount(){
		this.highCount++;
	}
	
	public void increaseReTriggerCount(){
		this.reTriggerCount++;
	}
	
	public void increaseRetryLoopCount(){
		this.retryLoopCount++;
	}
	
	public void increaseWaitCount(){
		this.waitCount++;
	}
	public void increaseLowCount(){
		this.lowCount++;
	}
	public void decreaseHigherCount(){
		this.highCount--;
	}
	public void decreaseWaitCount(){
		this.waitCount--;
	}
	public void decreaseLowerCount(){
		this.lowCount--;
	}
}
