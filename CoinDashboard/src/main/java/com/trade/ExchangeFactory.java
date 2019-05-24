package com.trade;

import com.trade.utils.TradeLogger;

public class ExchangeFactory {
	
	public static ITrade getInstance(String exchangeName){
		
		ITrade exchange = null;
		
		if(exchangeName.equalsIgnoreCase("BINANCE"))
		{
			exchange = new BinanceTrade();
		}
		else if(exchangeName.equalsIgnoreCase("BITSO"))
		{
			exchange = new BitsoTrade();
		}
		else if(exchangeName.equalsIgnoreCase("LIVECOIN"))
		{
			exchange = new LivecoinTrade();
		}
		else
		{
			TradeLogger.LOGGER.severe("No Implementation for exchange : "+ exchangeName);
		}
		return exchange;
		
	}

}
