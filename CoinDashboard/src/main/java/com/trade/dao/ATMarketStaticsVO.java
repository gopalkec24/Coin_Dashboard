package com.trade.dao;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;

public class ATMarketStaticsVO {
	
	
	boolean exchangePrice;
	boolean success;
	
	String errorMsg="";

	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	String exchangeName;
	String symbol;
	String currency;
	
	BigDecimal lastPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal lowPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal highPrice = TraderConstants.NEGATIVE_ONE;
	
	BigDecimal volume;
	
	//TODO ask price and bid price. Need clarity on ask/buy price
	//sell price and bid price which is ones
	BigDecimal buyPrice;
	BigDecimal sellPrice;
	
	BigDecimal priceChange;
	BigDecimal pricePercentFor24H;
	BigDecimal pricePercentFor1H;
	BigDecimal pricePercentFor7D;
	
	
	public boolean isExchangePrice() {
		return exchangePrice;
	}
	public void setExchangePrice(boolean exchangePrice) {
		this.exchangePrice = exchangePrice;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
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
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getPriceChange() {
		return priceChange;
	}
	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}
	public BigDecimal getPricePercentFor24H() {
		return pricePercentFor24H;
	}
	public void setPricePercentFor24H(BigDecimal pricePerentFor24H) {
		this.pricePercentFor24H = pricePerentFor24H;
	}
	public BigDecimal getPricePercentFor1H() {
		return pricePercentFor1H;
	}
	public void setPricePercentFor1H(BigDecimal pricePercentFor1H) {
		this.pricePercentFor1H = pricePercentFor1H;
	}
	public BigDecimal getPricePercentFor7D() {
		return pricePercentFor7D;
	}
	public void setPricePercentFor7D(BigDecimal pricePercentFor7D) {
		this.pricePercentFor7D = pricePercentFor7D;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
