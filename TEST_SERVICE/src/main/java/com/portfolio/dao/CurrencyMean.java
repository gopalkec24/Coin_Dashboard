package com.portfolio.dao;

import java.math.BigDecimal;

public class CurrencyMean {
	
	String currencyName;
	BigDecimal buyVolume;
	BigDecimal sellVolume;
	BigDecimal commissionRate;
	BigDecimal buyPrice;
	BigDecimal sellPrice;
	
	public boolean tradeCurrency;
	
	BigDecimal tradeVolume;
	
	
	
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
