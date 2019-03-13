package com.trade.run;

import com.trade.impl.AutoTraderExecutor;

public class PercentageTrading {

	public static void main(String[] args) {
		String jsonFilePath = "C:/Documents/autotradeData2.json";
		String traderType= "PercentageTrader";
		long waitTimeInMS = 180000;
		int executionFrequency = 1000;
		AutoTraderExecutor.startTrade(jsonFilePath, traderType, waitTimeInMS, executionFrequency);

	}
}
