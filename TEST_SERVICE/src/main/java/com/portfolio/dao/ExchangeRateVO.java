package com.portfolio.dao;

import java.math.BigDecimal;

public class ExchangeRateVO {
	
	private String coinName;
	
	private String currencny;
	
	private BigDecimal lastTradePrice;
	
	private BigDecimal lastBuyPrice;
	
	private BigDecimal lastSellPrice;
	
	private String symbol;
	
	private String price;
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	private String exchangeName;

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getCurrencny() {
		return currencny;
	}

	public void setCurrencny(String currencny) {
		this.currencny = currencny;
	}

	public BigDecimal getLastTradePrice() {
		return lastTradePrice;
	}

	public void setLastTradePrice(BigDecimal lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	public BigDecimal getLastBuyPrice() {
		return lastBuyPrice;
	}

	public void setLastBuyPrice(BigDecimal lastBuyPrice) {
		this.lastBuyPrice = lastBuyPrice;
	}

	public BigDecimal getLastSellPrice() {
		return lastSellPrice;
	}

	public void setLastSellPrice(BigDecimal lastSellPrice) {
		this.lastSellPrice = lastSellPrice;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	
	
	
	
}
