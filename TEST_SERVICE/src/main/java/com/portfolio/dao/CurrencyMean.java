package com.portfolio.dao;

import java.math.BigDecimal;

public class CurrencyMean {
	private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");
	String currencyName;
	BigDecimal buyVolume =BIG_DECIMAL_ZERO;
	BigDecimal sellVolume = BIG_DECIMAL_ZERO;
	BigDecimal commissionRate =BIG_DECIMAL_ZERO;
	BigDecimal buyPrice = BIG_DECIMAL_ZERO;
	BigDecimal sellPrice =BIG_DECIMAL_ZERO;
	
	BigDecimal currentMarketPrice = BIG_DECIMAL_ZERO;
	BigDecimal lastPrice =BIG_DECIMAL_ZERO;
	
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}
	public BigDecimal getCurrentMarketPrice() {
		return currentMarketPrice;
	}
	public void setCurrentMarketPrice(BigDecimal currentMarketPrice) {
		this.currentMarketPrice = currentMarketPrice;
	}
	public boolean tradeCurrency;
	
	BigDecimal tradeVolume;
	
	
	
	BigDecimal soldInvestment;
	BigDecimal currentInvestment;
	BigDecimal buyMean;
	BigDecimal sellMean;
	
	BigDecimal profitRealised;
	BigDecimal profitRealPercentage;
	
	
	BigDecimal priceDiffer;
	BigDecimal priceDifferPercentage;
	
	
	public BigDecimal getPriceDiffer() {
		return priceDiffer;
	}
	public void setPriceDiffer(BigDecimal priceDiffer) {
		this.priceDiffer = priceDiffer;
	}
	public BigDecimal getPriceDifferPercentage() {
		return priceDifferPercentage;
	}
	public void setPriceDifferPercentage(BigDecimal priceDifferPercentage) {
		this.priceDifferPercentage = priceDifferPercentage;
	}
	public BigDecimal getTradeVolume() {
		return tradeVolume;
	}
	public void setTradeVolume(BigDecimal tradeVolume) {
		this.tradeVolume = tradeVolume;
	}
	public BigDecimal getSoldInvestment() {
		return soldInvestment;
	}
	public void setSoldInvestment(BigDecimal soldInvestment) {
		this.soldInvestment = soldInvestment;
	}
	public BigDecimal getCurrentInvestment() {
		return currentInvestment;
	}
	public void setCurrentInvestment(BigDecimal currentInvestment) {
		this.currentInvestment = currentInvestment;
	}
	public BigDecimal getBuyMean() {
		return buyMean;
	}
	public void setBuyMean(BigDecimal buyMean) {
		this.buyMean = buyMean;
	}
	public BigDecimal getSellMean() {
		return sellMean;
	}
	public void setSellMean(BigDecimal sellMean) {
		this.sellMean = sellMean;
	}
	public BigDecimal getProfitRealised() {
		return profitRealised;
	}
	public void setProfitRealised(BigDecimal profitRealised) {
		this.profitRealised = profitRealised;
	}
	public BigDecimal getProfitRealPercentage() {
		return profitRealPercentage;
	}
	public void setProfitRealPercentage(BigDecimal profitRealPercentage) {
		this.profitRealPercentage = profitRealPercentage;
	}
	public boolean isTradeCurrency() {
		return tradeCurrency;
	}
	public void setTradeCurrency(boolean tradeCurrency) {
		this.tradeCurrency = tradeCurrency;
	}
	public BigDecimal getBuyVolume() {
		return buyVolume;
	}
	public void setBuyVolume(BigDecimal buyVolume) {
		this.buyVolume = buyVolume;
	}
	public BigDecimal getSellVolume() {
		return sellVolume;
	}
	public void setSellVolume(BigDecimal sellVolume) {
		this.sellVolume = sellVolume;
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
	public BigDecimal getPrice() {
		return buyPrice;
	}
	public void setPrice(BigDecimal price) {
		this.buyPrice = price;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}


	public BigDecimal getCommissionRate() {
		return commissionRate;
	}
	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}
	public String toString(){
		return this.currencyName+",Sell Volume : "+this.sellVolume.toString()+",Buy Volume : "+this.buyVolume.toString()+",Buy Price : "+this.buyPrice.toString()+ ",Sell Price : "+this.sellPrice.toString() +",Commission Amt :"+this.commissionRate+"\n";
	}

}
