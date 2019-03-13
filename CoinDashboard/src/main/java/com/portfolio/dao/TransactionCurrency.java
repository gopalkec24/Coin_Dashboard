package com.portfolio.dao;

import java.math.BigDecimal;

public class TransactionCurrency {
	
	private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");
	String name;
	boolean isCrypto;
	String exchange;
	
	BigDecimal depositVolume= BIG_DECIMAL_ZERO;
	BigDecimal withdrawalVolume =BIG_DECIMAL_ZERO;
	
	public BigDecimal getDepositVolume() {
		return depositVolume;
	}
	public void setDepositVolume(BigDecimal depositVolume) {
		this.depositVolume = depositVolume;
	}
	public BigDecimal getWithdrawalVolume() {
		return withdrawalVolume;
	}
	public void setWithdrawalVolume(BigDecimal withdrawalVolume) {
		this.withdrawalVolume = withdrawalVolume;
	}
	BigDecimal buyTransactionAmount =BIG_DECIMAL_ZERO;	
	BigDecimal sellTransactionAmount=BIG_DECIMAL_ZERO;
	BigDecimal commissionAmount=BIG_DECIMAL_ZERO;
	BigDecimal currentInvestAmount=BIG_DECIMAL_ZERO;
	BigDecimal soldInvestment=BIG_DECIMAL_ZERO;
	BigDecimal netProfit =BIG_DECIMAL_ZERO;
	public BigDecimal getNetProfit() {
		return netProfit;
	}
	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit = netProfit;
	}
	public BigDecimal getSoldInvestment() {
		return soldInvestment;
	}
	public void setSoldInvestment(BigDecimal soldInvestment) {
		this.soldInvestment = soldInvestment;
	}
	BigDecimal overallGainPercent= BIG_DECIMAL_ZERO;
	
	
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
	public BigDecimal getOverallGainPercent() {
		return overallGainPercent;
	}
	public void setOverallGainPercent(BigDecimal overallGainPercent) {
		this.overallGainPercent = overallGainPercent;
	}
	
	

}
