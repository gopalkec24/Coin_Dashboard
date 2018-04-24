package com.gopal.currency;

public class CoinMeanVO {
	
	private double buyPrice;
	private double sellPrice;
	private double buyVolume;
	private double sellVolume;
	public double getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	public double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public double getBuyVolume() {
		return buyVolume;
	}
	public void setBuyVolume(double buyVolume) {
		this.buyVolume = buyVolume;
	}
	public double getSellVolume() {
		return sellVolume;
	}
	public void setSellVolume(double sellVolume) {
		this.sellVolume = sellVolume;
	}
}
