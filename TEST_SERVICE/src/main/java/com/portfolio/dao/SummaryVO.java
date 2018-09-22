package com.portfolio.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SummaryVO {

	public SummaryVO() {
		timestamp=new Date();
	}
	
	private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");
	Map<String, ExchangeVO> exchangeList ;
	BigDecimal nonCryptoInvstAmt = BIG_DECIMAL_ZERO;
	BigDecimal currentMarketValue = BIG_DECIMAL_ZERO;
	BigDecimal netInvestValue=BIG_DECIMAL_ZERO;
	BigDecimal profit = BIG_DECIMAL_ZERO;
	List<InvestmentConversion> conversionList = new ArrayList<InvestmentConversion>();
	List<CryptoTransactionVO> listCryptoTransactions;
	Date timestamp;
	
	
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getNetInvestValue() {
		return netInvestValue;
	}

	public void setNetInvestValue(BigDecimal netInvestValue) {
		this.netInvestValue = netInvestValue;
	}

	public List<InvestmentConversion> getConversionList() {
		return conversionList;
	}

	public void setConversionList(List<InvestmentConversion> conversionList) {
		this.conversionList = conversionList;
	}
	/*Map<String,BigDecimal> currentMarketMap= new TreeMap<String, BigDecimal>();	
	Map<String,BigDecimal> nonCryptoInvstMap= new TreeMap<String, BigDecimal>();
	*/
	/*public void addNonCryptoInvtValue(String currency,BigDecimal value) {
		this.nonCryptoInvstMap.put(currency, value);
	}*/
	public void addInvestmentConversion(InvestmentConversion value) {
		this.conversionList.add(value);
	}
	
	/*public void addCurrentMarketValue(String currency,BigDecimal value)
	{
		this.currentMarketMap.put(currency, value);
	}
	public Map<String, BigDecimal> getCurrencyValMap() {
		return nonCryptoInvstMap;
	}
	public void setCurrencyValMap(Map<String, BigDecimal> currencyValMap) {
		this.nonCryptoInvstMap = currencyValMap;
	}
	public Map<String, BigDecimal> getCurrentMarketMap() {
		return currentMarketMap;
	}
	public void setCurrentMarketMap(Map<String, BigDecimal> currentMarketMap) {
		this.currentMarketMap = currentMarketMap;
	}*/
	
	
	public Map<String, ExchangeVO> getExchangeList() {
		return exchangeList;
	}
	public void setExchangeList(Map<String, ExchangeVO> exchangeList) {
		this.exchangeList = exchangeList;
	}
	public BigDecimal getNonCryptoInvstAmt() {
		return nonCryptoInvstAmt;
	}
	public void setNonCryptoInvstAmt(BigDecimal nonCryptoInvstAmt) {
		this.nonCryptoInvstAmt = nonCryptoInvstAmt;
	}
	public BigDecimal getCurrentMarketValue() {
		return currentMarketValue;
	}
	public void setCurrentMarketValue(BigDecimal currentMarketValue) {
		this.currentMarketValue = currentMarketValue;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public List<CryptoTransactionVO> getListCryptoTransactions() {
		return listCryptoTransactions;
	}

	public void setListCryptoTransactions(List<CryptoTransactionVO> listCryptoTransactions) {
		this.listCryptoTransactions = listCryptoTransactions;
	}

}
