package com.trade.dao.bitbns;

import java.math.BigDecimal;

public class BitbnsTickerVO {
	
	BigDecimal highest_buy_bid;
	BigDecimal lowest_sell_bid;
	BigDecimal last_traded_price;
	BigDecimal yes_price;
	BigDecimal inr_price;
	
	BitbnsVolumeVO volume;
	
	

	public BigDecimal getInr_price() {
		return inr_price;
	}

	public void setInr_price(BigDecimal inr_price) {
		this.inr_price = inr_price;
	}

	public BigDecimal getHighest_buy_bid() {
		return highest_buy_bid;
	}

	public void setHighest_buy_bid(BigDecimal highest_buy_bid) {
		this.highest_buy_bid = highest_buy_bid;
	}

	public BigDecimal getLowest_sell_bid() {
		return lowest_sell_bid;
	}

	public void setLowest_sell_bid(BigDecimal lowest_sell_bid) {
		this.lowest_sell_bid = lowest_sell_bid;
	}

	public BigDecimal getLast_traded_price() {
		return last_traded_price;
	}

	public void setLast_traded_price(BigDecimal last_traded_price) {
		this.last_traded_price = last_traded_price;
	}

	public BigDecimal getYes_price() {
		return yes_price;
	}

	public void setYes_price(BigDecimal yes_price) {
		this.yes_price = yes_price;
	}

	public BitbnsVolumeVO getVolume() {
		return volume;
	}

	public void setVolume(BitbnsVolumeVO volume) {
		this.volume = volume;
	}

	
	public String toString() {
		return inr_price+"";
		
	}
}
