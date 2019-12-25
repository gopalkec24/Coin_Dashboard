package com.trade.dao.binance;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OrderFillDetails 
{
	
	String price;
	String qty;
	String commission;
	String commissionAsset;
	long tradeId;
	
	/*public long getTradeId() {
		return tradeId;
	}
	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}*/
	
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
