package com.gopal;

import java.util.HashMap;
import java.util.Map;

public class CoinSettingVO {

	public String coinName;
	
	public double percentage =10;
	
	public double currentRate = 0.0;
	
	public Map<String,Double> multiCurrencyCurrentRate = new HashMap<String,Double>();

	public Map<String, Double> getMultiCurrencyCurrentRate() {
		return multiCurrencyCurrentRate;
	}

	public void setMultiCurrencyCurrentRate(Map<String, Double> multiCurrencyCurrentRate) {
		this.multiCurrencyCurrentRate = multiCurrencyCurrentRate;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public double getCurrentRate() {
		return currentRate;
	}

	public void setCurrentRate(double currentRate) {
		this.currentRate = currentRate;
	}
	
	
}
