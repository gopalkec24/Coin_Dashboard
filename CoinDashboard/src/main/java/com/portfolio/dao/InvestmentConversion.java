package com.portfolio.dao;

import java.math.BigDecimal;

public class InvestmentConversion {
	
	String currency;
	BigDecimal currentMarketValue;
	BigDecimal investmentAmt;
	BigDecimal netInvestAmt;
	BigDecimal currencyValue;
	
	
	public BigDecimal getCurrencyValue() {
		return currencyValue;
	}
	public void setCurrencyValue(BigDecimal currencyValue) {
		this.currencyValue = currencyValue;
	}
	public BigDecimal getNetInvestAmt() {
		return netInvestAmt;
	}
	public void setNetInvestAmt(BigDecimal netInvestAmt) {
		this.netInvestAmt = netInvestAmt;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getCurrentMarketValue() {
		return currentMarketValue;
	}
	public void setCurrentMarketValue(BigDecimal currentMarketValue) {
		this.currentMarketValue = currentMarketValue;
	}
	public BigDecimal getInvestmentAmt() {
		return investmentAmt;
	}
	public void setInvestmentAmt(BigDecimal investmentAmt) {
		this.investmentAmt = investmentAmt;
	}

}
