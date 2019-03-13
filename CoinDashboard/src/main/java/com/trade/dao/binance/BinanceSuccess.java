package com.trade.dao.binance;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BinanceSuccess {
	
	public String symbol;
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
	String origClientOrderId;
	
	String stopPrice;
	String icebergQty;
	
	long time;
	long updateTime;
	boolean isWorking;
	
	
	public String getIcebergQty() {
		return icebergQty;
	}
	public void setIcebergQty(String icebergQty) {
		this.icebergQty = icebergQty;
	}
	public String getStopPrice() {
		return stopPrice;
	}
	public void setStopPrice(String stopPrice) {
		this.stopPrice = stopPrice;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	public boolean isWorking() {
		return isWorking;
	}
	public void setIsWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}
	public String getOrigClientOrderId() {
		return origClientOrderId;
	}
	public void setOrigClientOrderId(String origClientOrderId) {
		this.origClientOrderId = origClientOrderId;
	}
	public List<OrderFillDetails> getFills() {
		return fills;
	}
	public void setFills(List<OrderFillDetails> fills) {
		this.fills = fills;
	}
	List<OrderFillDetails> fills ;
	
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
	
	
	
	

}
