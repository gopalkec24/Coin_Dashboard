package com.trade.dao.binance;

public class OrderFillDetails {
	
	String price;
	String qty;
	String commission;
	String commissionAsset;
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getCommission() {
		return commission;
	}
	public void setCommission(String commission) {
		this.commission = commission;
	}
	public String getCommissionAsset() {
		return commissionAsset;
	}
	public void setCommissionAsset(String commissionAsset) {
		this.commissionAsset = commissionAsset;
	}
	
	

}
