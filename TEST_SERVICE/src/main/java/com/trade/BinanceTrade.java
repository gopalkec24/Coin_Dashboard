package com.trade;

import com.trade.utils.TradeLogger;

public class BinanceTrade extends BaseTrade
{

	
	
	public void getMarketStatics() {
		
		if(getFetchConfigurationForExchange("BINANCE") != null) {
			TradeLogger.LOGGER.info(getFetchConfigurationForExchange("BINANCE").getFetchURL());
		}
	}

	public void buyCoin() {
		// TODO Auto-generated method stub
		
	}

	
	public void sellCoin() {
		// TODO Auto-generated method stub
		
	}

}
