package com.trade.inf;

import com.trade.impl.AdvanceTrader;
import com.trade.impl.PercentageBaseTrader;
import com.trade.impl.PercentageTrader;
import com.trade.utils.TradeLogger;

public class AutoTradeFactory {

	
		public static IAutoTrader getAutoTrader(String traderType) {
			
			IAutoTrader trader = null;
			
			if(traderType.equalsIgnoreCase("AdvanceTrader"))
			{
				trader = new AdvanceTrader();
			}
			else if(traderType.equalsIgnoreCase("PercentageTrader"))
			{
				trader = new PercentageTrader();
				TradeLogger.LOGGER.finest("PercentageTrader class is created here");
			}
			else if(traderType.equalsIgnoreCase("PercentageBaseTrader"))
			{
				trader = new PercentageBaseTrader();
				TradeLogger.LOGGER.finest("PercentageTrader class is created here");
			}
			else
			{
				TradeLogger.LOGGER.severe("No Implementation for exchange : "+ traderType);
			}
			return trader;
			
			
			
		}
}
