package com.portfolio.dao;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeVO {
	
	List<ExchangeCurrencyVO> coinList;
	List<TransactionCurrency> transCurrency;
	List<String> tradeCoinList ;
	public static final BigDecimal ZERO_BIGDECIMAL = new BigDecimal(0);
	BigDecimal currentMarketValue =  ZERO_BIGDECIMAL;
	BigDecimal nonCryptoInvestValue = ZERO_BIGDECIMAL;
	
	public BigDecimal getCurrentMarketValue() {
		return currentMarketValue;
	}
	public void setCurrentMarketValue(BigDecimal currentMarketValue) {
		this.currentMarketValue = currentMarketValue;
	}
	public BigDecimal getNonCryptoInvestValue() {
		return nonCryptoInvestValue;
	}
	public void setNonCryptoInvestValue(BigDecimal nonCryptoInvestValue) {
		this.nonCryptoInvestValue = nonCryptoInvestValue;
	}
	public List<ExchangeCurrencyVO> getCoinList() {
		return coinList;
	}
	public void setCoinList(List<ExchangeCurrencyVO> coinList) {
		this.coinList = coinList;
	}
	public List<TransactionCurrency> getTransCurrency() {
		return transCurrency;
	}
	public void setTransCurrency(List<TransactionCurrency> transCurrency) {
		this.transCurrency = transCurrency;
	}
	public void addTransactionCurrencyVO(TransactionCurrency transactionCurrency){
		this.transCurrency.add(transactionCurrency);
	}
	public void addTradeCoinList(String symCurr)
	{
		this.tradeCoinList.add(symCurr);
	}
	public List<String> getTradeCoinList() {
		return tradeCoinList;
	}
	public void setTradeCoinList(List<String> tradeCoinList) {
		this.tradeCoinList = tradeCoinList;
	}
}
