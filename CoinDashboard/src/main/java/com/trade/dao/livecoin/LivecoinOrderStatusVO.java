package com.trade.dao.livecoin;

import java.math.BigDecimal;

public class LivecoinOrderStatusVO {

	String id;
	String client_id;
	String status;
	String symbol;
	BigDecimal price;
	BigDecimal quantity;
	BigDecimal remaining_quantity;
	BigDecimal blocked;
	BigDecimal blocked_remain;
	BigDecimal commission_rate;
	LivecoinTradeVO trades;
	String type;
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String types) {
		this.type = types;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getRemaining_quantity() {
		return remaining_quantity;
	}
	public void setRemaining_quantity(BigDecimal remaining_quantity) {
		this.remaining_quantity = remaining_quantity;
	}
	public BigDecimal getBlocked() {
		return blocked;
	}
	public void setBlocked(BigDecimal blocked) {
		this.blocked = blocked;
	}
	public BigDecimal getBlocked_remain() {
		return blocked_remain;
	}
	public void setBlocked_remain(BigDecimal blocked_remain) {
		this.blocked_remain = blocked_remain;
	}
	public BigDecimal getCommission_rate() {
		return commission_rate;
	}
	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}
	public LivecoinTradeVO getTrades() {
		return trades;
	}
	public void setTrades(LivecoinTradeVO trades) {
		this.trades = trades;
	}
	
	
	
	
}
