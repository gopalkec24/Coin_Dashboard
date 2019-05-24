package com.trade.dao.livecoin;

import java.math.BigDecimal;

public class LivecoinTradeVO {

	int trades;
	BigDecimal amount;
	BigDecimal quantity;
	BigDecimal avg_price;
	BigDecimal commission;
	BigDecimal bonus;
	public int getTrades() {
		return trades;
	}
	public void setTrades(int trades) {
		this.trades = trades;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	public BigDecimal getAvg_price() {
		return avg_price;
	}
	public void setAvg_price(BigDecimal avg_price) {
		this.avg_price = avg_price;
	}
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public BigDecimal getBonus() {
		return bonus;
	}
	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}
	
	
	
}
