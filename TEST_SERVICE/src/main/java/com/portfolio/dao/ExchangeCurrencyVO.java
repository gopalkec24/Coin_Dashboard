package com.portfolio.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCurrencyVO {

	 private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");
	String exchangeName;
	 String coinName;
	 BigDecimal depositAmt = BIG_DECIMAL_ZERO;
	 BigDecimal withdrawalAmt = BIG_DECIMAL_ZERO;
	
	 BigDecimal totalAmt = BIG_DECIMAL_ZERO;
	 
	 BigDecimal currentMarketValue = BIG_DECIMAL_ZERO;
	 public BigDecimal getCurrentMarketValue() {
		return currentMarketValue;
	}
	public void setCurrentMarketValue(BigDecimal currentMarketValue) {
		this.currentMarketValue = currentMarketValue;
	}

	//Holds the coins available in exchange here.
	 List<CurrencyMean> currency=new ArrayList<CurrencyMean>();
	 
	
	
	
	public boolean tradeCurrency = false;
	 
	 public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	
	 
	
	 
	 public BigDecimal getXchangeTransferCommission() {
			return xchangeTransferCommission;
		}
		public void setXchangeTransferCommission(BigDecimal xchangeTransferCommission) {
			this.xchangeTransferCommission = xchangeTransferCommission;
		}
	 
		BigDecimal xchangeTransferCommission = BIG_DECIMAL_ZERO;
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
