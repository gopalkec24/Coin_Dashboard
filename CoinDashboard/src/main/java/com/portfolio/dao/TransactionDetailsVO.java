package com.portfolio.dao;

import java.math.BigDecimal;

public class TransactionDetailsVO {
	
	String coinName ;
	String date;
	
	BigDecimal volume;
	BigDecimal price;
	BigDecimal commissionRate;
	
	int commissionType;
	int transactionType;
	
	String exchangeName;
	String currency;
	
	BigDecimal totalTransactionAmt=new BigDecimal("0");
	
	public BigDecimal getTotalTransactionAmt() {
		return totalTransactionAmt;
	}
	public void setTotalTransactionAmt(BigDecimal totalTransactionAmt) {
		this.totalTransactionAmt = totalTransactionAmt;
	}
	public String getCommissionCurrency() {
		return commissionCurrency;
	}
	public void setCommissionCurrency(String commissionCurrency) {
		this.commissionCurrency = commissionCurrency;
	}

	String commissionCurrency;
	
	
	
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getCommissionRate() {
		return commissionRate;
	}
	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}
	public int getCommissionType() {
		return commissionType;
	}
	public void setCommissionType(int commissionType) {
		this.commissionType = commissionType;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
   public String toString(){
	return this.date+","+this.coinName+","+this.commissionType+","+this.transactionType+","+this.exchangeName;
	   
   }
}
