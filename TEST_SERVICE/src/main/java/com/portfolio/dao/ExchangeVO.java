package com.portfolio.dao;

import java.util.List;

public class ExchangeVO {
	
	List<ExchangeCurrencyVO> coinList;
	List<TransactionCurrency> transCurrency;
	
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
}
