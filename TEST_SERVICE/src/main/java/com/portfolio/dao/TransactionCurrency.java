package com.portfolio.dao;

import java.math.BigDecimal;

public class TransactionCurrency {
	
	String name;
	boolean isCrypto;
	String exchange;
	BigDecimal buyTransactionAmount =new BigDecimal("0");
	BigDecimal sellTransactionAmount=new BigDecimal("0");
	BigDecimal commissionAmount=new BigDecimal("0");
	BigDecimal currentInvestAmount=new BigDecimal("0");
	
	public BigDecimal getCurrentInvestAmount() {
		return currentInvestAmount;
	}
	public void setCurrentInvestAmount(BigDecimal currentInvestAmount) {
		this.currentInvestAmount = currentInvestAmount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCrypto() {
		return isCrypto;
	}
	public void setCrypto(boolean isCrypto) {
		this.isCrypto = isCrypto;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public BigDecimal getBuyTransactionAmount() {
		return buyTransactionAmount;
	}
	public void setBuyTransactionAmount(BigDecimal buyTransactionAmount) {
		this.buyTransactionAmount = buyTransactionAmount;
	}
	public BigDecimal getSellTransactionAmount() {
		return sellTransactionAmount;
	}
	public void setSellTransactionAmount(BigDecimal sellTransactionAmount) {
		this.sellTransactionAmount = sellTransactionAmount;
	}
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	
	
	

}
