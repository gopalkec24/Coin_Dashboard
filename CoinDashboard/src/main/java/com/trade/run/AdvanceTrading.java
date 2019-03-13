package com.trade.run;

import com.trade.impl.AutoTraderExecutor;

public class AdvanceTrading {

	public static void main(String[] args) {
		String jsonFilePath = "C:/Documents/autotradeData1.json";
		String traderType= "AdvanceTrader";
		long waitTimeInMS = 420000;
		int executionFrequency = 1000;
		AutoTraderExecutor.startTrade(jsonFilePath, traderType, waitTimeInMS, executionFrequency);

	}

}
