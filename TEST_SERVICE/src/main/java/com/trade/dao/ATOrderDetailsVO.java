package com.trade.dao;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;

public class ATOrderDetailsVO {
	
	String exchange;
	String coin;
	String currency;
	String orderId;
	int orderType;
	int orderSubType;
	BigDecimal orderPrice = TraderConstants.NEGATIVE_ONE;
	BigDecimal quantity = TraderConstants.NEGATIVE_ONE;
	BigDecimal stopPrice = TraderConstants.NEGATIVE_ONE;
	long transactionTime;
	
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public int getOrderSubType() {
		return orderSubType;
	}
	public void setOrderSubType(int orderSubType) {
		this.orderSubType = orderSubType;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getCoin() {
		return coin;
	}
	public void setCoin(String coin) {
		this.coin = coin;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public BigDecimal getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(BigDecimal orderPrice) {
		this.orderPrice = orderPrice;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getStopPrice() {
		return stopPrice;
	}
	public void setStopPrice(BigDecimal stopPrice) {
		this.stopPrice = stopPrice;
	}
	public long getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(long transactionTime) {
		this.transactionTime = transactionTime;
	}
	
	
	

}
