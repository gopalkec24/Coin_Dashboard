package com.portfolio.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCurrencyVO {

	 String exchangeName;
	 String coinName;
	 List<CurrencyMean> currency=new ArrayList<CurrencyMean>();
	 BigDecimal totalAmt = new BigDecimal("0");
	 
	 public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	BigDecimal depositAmt = new BigDecimal("0");
	 BigDecimal withdrawalAmt = new BigDecimal("0");
	 
	 public boolean tradeCurrency = false;
	 
	 
	 
	 
	public boolean isTradeCurrency() {
		return tradeCurrency;
	}
	public void setTradeCurrency(boolean tradeCurrency) {
		this.tradeCurrency = tradeCurrency;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public List<CurrencyMean> getCurrency() {
		return currency;
	}
	public void setCurrency(List<CurrencyMean> currency) {
		this.currency = currency;
	}
	public BigDecimal getDepositAmt() {
		return depositAmt;
	}
	public void setDepositAmt(BigDecimal depositAmt) {
		this.depositAmt = depositAmt;
	}
	public BigDecimal getWithdrawalAmt() {
		return withdrawalAmt;
	}
	public void setWithdrawalAmt(BigDecimal withdrawalAmt) {
		this.withdrawalAmt = withdrawalAmt;
	}
	public void addCurrencyVO(CurrencyMean mean){
		this.currency.add(mean);
	}
	
	public String toString()
	{
		return this.exchangeName+","+this.coinName+","+this.currency.toString();
	}
}
