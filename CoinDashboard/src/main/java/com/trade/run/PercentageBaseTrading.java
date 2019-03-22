package com.trade.run;

import com.trade.impl.AutoTraderExecutor;

public class PercentageBaseTrading {

	public static void main(String[] args) {
		String jsonFilePath = "C:/Documents/autotradeData4.json";
		String traderType= "PercentageBaseTrader";
		long waitTimeInMS = 420000;
		int executionFrequency = 1000;
		AutoTraderExecutor.startTrade(jsonFilePath, traderType, waitTimeInMS, executionFrequency);

	}
}
