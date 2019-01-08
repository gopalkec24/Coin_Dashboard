package com.portfolio.dao;

import java.math.BigDecimal;

public class MainnetConfiguration {

	/*
	 *   "exchangeName" : "BINANCE",
  "original_coin": "VEN",
  "order" : 1,
  "replace_coin" : true,
  "replace_coin_name" : "VET",
 "coin_factor" : 100.00
	 */
	
	String exchangeName;
	
	String coinName;
	String replaceCoinName;
	boolean replaceCoin;
	BigDecimal coinFactor;
	int order;
	
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
	public String getReplaceCoinName() {
		return replaceCoinName;
	}
	public void setReplaceCoinName(String replaceCoinName) {
		this.replaceCoinName = replaceCoinName;
	}
	public boolean isReplaceCoin() {
		return replaceCoin;
	}
	public void setReplaceCoin(boolean replaceCoin) {
		this.replaceCoin = replaceCoin;
	}
	public BigDecimal getCoinFactor() {
		return coinFactor;
	}
	public void setCoinFactor(BigDecimal coinFactor) {
		this.coinFactor = coinFactor;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}
