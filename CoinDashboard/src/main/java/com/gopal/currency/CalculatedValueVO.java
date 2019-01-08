package com.gopal.currency;

public class CalculatedValueVO {
	
	double costPrice=0; 
	
	double costPriceInUSD = 0;


	
	double costPriceInINR = 0;

    double effectiveVolume = 0;

	public double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	public double getCostPriceInUSD() {
		return costPriceInUSD;
	}

	public void setCostPriceInUSD(double costPriceInUSD) {
		this.costPriceInUSD = costPriceInUSD;
	}

	public double getCostPriceInINR() {
		return costPriceInINR;
	}

	public void setCostPriceInINR(double costPriceInINR) {
		this.costPriceInINR = costPriceInINR;
	}

	public double getEffectiveVolume() {
		return effectiveVolume;
	}

	public void setEffectiveVolume(double effectiveVolume) {
		this.effectiveVolume = effectiveVolume;
	}

}
