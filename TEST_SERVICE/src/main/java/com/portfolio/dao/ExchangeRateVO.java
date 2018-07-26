package com.portfolio.dao;

import java.math.BigDecimal;

public class ExchangeRateVO {
	//1
	private String coinName;
	//2
	private BigDecimal priceChangePercent24;
	//3
	int rank;
	//4
	private String exchange;
	//5
	private BigDecimal volumeChange24;
	//6	
	private String currency;
	//7
	private BigDecimal currentTradePrice;
	//8
	private String symbol;
	
	
	private BigDecimal lastBuyPrice;
	
	private BigDecimal lastSellPrice;
	
	private BigDecimal lastTradePrice;
	
	
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}



	private String exchangeName;

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getPriceChangePercent24() {
		return priceChangePercent24;
	}

	public void setPriceChangePercent24(BigDecimal priceChangePercent24) {
		this.priceChangePercent24 = priceChangePercent24;
	}

	public BigDecimal getVolumeChange24() {
		return volumeChange24;
	}

	public void setVolumeChange24(BigDecimal volumeChange24) {
		this.volumeChange24 = volumeChange24;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public BigDecimal getCurrentTradePrice() {
		return currentTradePrice;
	}

	public void setCurrentTradePrice(BigDecimal currentTradePrice) {
		this.currentTradePrice = currentTradePrice;
	}
	
	
	
	
}
