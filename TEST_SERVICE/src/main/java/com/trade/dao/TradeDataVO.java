package com.trade.dao;

import java.math.BigDecimal;

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
	BigDecimal orderTriggeredPrice= TraderConstants.NEGATIVE_ONE;
	
	BigDecimal tradeCurrencyVolume= TraderConstants.NEGATIVE_ONE;
	BigDecimal coinVolume = TraderConstants.NEGATIVE_ONE;
	
	
	String coin;
	String currency;
	String exchange;
	int triggerEvent = TraderConstants.COUNTER_NOINIT;
	
	int waitCount = TraderConstants.COUNTER_NOINIT;
	int highCount = TraderConstants.COUNTER_NOINIT;
	int lowCount = TraderConstants.COUNTER_NOINIT;
	
	int retryLoopCount = TraderConstants.COUNTER_NOINIT;
	int reTriggerCount = TraderConstants.COUNTER_NOINIT;
	
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
				"Last Price : "+this.lastPrice +"Triggered Price : "+this.triggeredPrice;
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
