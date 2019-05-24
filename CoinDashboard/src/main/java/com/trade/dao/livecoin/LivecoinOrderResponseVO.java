package com.trade.dao.livecoin;

import java.math.BigDecimal;

public class LivecoinOrderResponseVO {

	boolean success;
	boolean added;
	String orderId;
	String exception;
	boolean cancelled;
	
	BigDecimal quantity;
	BigDecimal tradeQuantity;
	
	String message;
	
	
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTradeQuantity() {
		return tradeQuantity;
	}
	public void setTradeQuantity(BigDecimal tradeQuantity) {
		this.tradeQuantity = tradeQuantity;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isAdded() {
		return added;
	}
	public void setAdded(boolean added) {
		this.added = added;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
}
