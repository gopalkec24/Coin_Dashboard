package com.gopal.currency;

public class CoinMeanVO {
	
	private double buyPrice;
	private double sellPrice;
	private double buyVolume;
	private double sellVolume;
	private double commission;


	private double nBuyPrice;
	private double nBuyPriceInUSD;
	private double nBuyPriceInINR;
	private double nBuyVolume;
	private double nBuyCount;
	
	
	private double buyPriceInUSD;
	private double sellPriceInUSD;
	
	private double buyPriceInINR;
	private double sellPriceInINR;
	
	public double getBuyPrice() {
		return buyPrice;
	}
	public double getnBuyPrice() {
		return nBuyPrice;
	}
	public void setnBuyPrice(double nBuyPrice) {
		this.nBuyPrice = nBuyPrice;
	}
	public double getnBuyVolume() {
		return nBuyVolume;
	}
	public void setnBuyVolume(double nBuyVolume) {
		this.nBuyVolume = nBuyVolume;
	}
	public double getnBuyCount() {
		return nBuyCount;
	}
	public void setnBuyCount(double nBuyCount) {
		this.nBuyCount = nBuyCount;
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
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public double getBuyPriceInUSD() {
		return buyPriceInUSD;
	}
	public void setBuyPriceInUSD(double buyPriceInUSD) {
		this.buyPriceInUSD = buyPriceInUSD;
	}
	public double getSellPriceInUSD() {
		return sellPriceInUSD;
	}
	public void setSellPriceInUSD(double sellPriceInUSD) {
		this.sellPriceInUSD = sellPriceInUSD;
	}
	public double getBuyPriceInINR() {
		return buyPriceInINR;
	}
	public void setBuyPriceInINR(double buyPriceInINR) {
		this.buyPriceInINR = buyPriceInINR;
	}
	public double getSellPriceInINR() {
		return sellPriceInINR;
	}
	public void setSellPriceInINR(double sellPriceInINR) {
		this.sellPriceInINR = sellPriceInINR;
	}
	public double getnBuyPriceInUSD() {
		return nBuyPriceInUSD;
	}
	public void setnBuyPriceInUSD(double nBuyPriceInUSD) {
		this.nBuyPriceInUSD = nBuyPriceInUSD;
	}
	public double getnBuyPriceInINR() {
		return nBuyPriceInINR;
	}
	public void setnBuyPriceInINR(double nBuyPriceInINR) {
		this.nBuyPriceInINR = nBuyPriceInINR;
	}
}
