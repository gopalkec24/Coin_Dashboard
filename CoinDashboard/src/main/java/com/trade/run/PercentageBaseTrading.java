package com.trade.run;

import com.trade.impl.AutoTraderExecutor;

public class PercentageBaseTrading {

	public static void main(String[] args) {
		String jsonFilePath = "C:/Documents/bitso.json";
		String traderType= "PercentageBaseTrader";
		long waitTimeInMS = 1000;
		int executionFrequency = 1;
		AutoTraderExecutor.startTrade(jsonFilePath, traderType, waitTimeInMS, executionFrequency);

	}
}
