package com.trade.dao;

import java.util.List;

public class BinanceSuccess {
	
	String symbol;
	long orderId;
	String clientOrderId;
	long transactTime;
	String price;
	String origQty;
	String executedQty;
	String cummulativeQuoteQty;
	String status;
	String timeInForce;
	String type;
	String side;
	List<OrderFillDetails> filledOrder ;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getClientOrderId() {
		return clientOrderId;
	}
	public void setClientOrderId(String clientOrderId) {
		this.clientOrderId = clientOrderId;
	}
	public long getTransactTime() {
		return transactTime;
	}
	public void setTransactTime(long transactTime) {
		this.transactTime = transactTime;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getOrigQty() {
		return origQty;
	}
	public void setOrigQty(String origQty) {
		this.origQty = origQty;
	}
	public String getExecutedQty() {
		return executedQty;
	}
	public void setExecutedQty(String executedQty) {
		this.executedQty = executedQty;
	}
	public String getCummulativeQuoteQty() {
		return cummulativeQuoteQty;
	}
	public void setCummulativeQuoteQty(String cummulativeQuoteQty) {
		this.cummulativeQuoteQty = cummulativeQuoteQty;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimeInForce() {
		return timeInForce;
	}
	public void setTimeInForce(String timeInForce) {
		this.timeInForce = timeInForce;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public List<OrderFillDetails> getFilledOrder() {
		return filledOrder;
	}
	public void setFilledOrder(List<OrderFillDetails> filledOrder) {
		this.filledOrder = filledOrder;
	}
	
	

}
