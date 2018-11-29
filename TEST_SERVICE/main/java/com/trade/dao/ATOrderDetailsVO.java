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
	
	BigDecimal placedQuantity = TraderConstants.NEGATIVE_ONE;
	BigDecimal executedQuantity = TraderConstants.NEGATIVE_ONE;
	BigDecimal cummQuoteQty = TraderConstants.NEGATIVE_ONE;
	long transactionTime;
	int resultCode;
	String clientStatusCode;
	boolean success= false;
	
	String errorMsg="";
	
	String clientStatus ;
	
	int status;
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getPlacedQuantity() {
		return placedQuantity;
	}

	public void setPlacedQuantity(BigDecimal placedQuantity) {
		this.placedQuantity = placedQuantity;
	}

	public BigDecimal getExecutedQuantity() {
		return executedQuantity;
	}

	public void setExecutedQuantity(BigDecimal executedQuantity) {
		this.executedQuantity = executedQuantity;
	}

	public BigDecimal getCummQuoteQty() {
		return cummQuoteQty;
	}

	public void setCummQuoteQty(BigDecimal cummQuoteQty) {
		this.cummQuoteQty = cummQuoteQty;
	}

	public String getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(String clientStatus) {
		this.clientStatus = clientStatus;
	}
	
	
	
	public String getClientStatusCode() {
		return clientStatusCode;
	}

	public void setClientStatusCode(String clientStatusCode) {
		this.clientStatusCode = clientStatusCode;
	}

	public void addErrorMessage(String msg) {
		this.errorMsg = this.errorMsg+msg;
	}
		
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
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
