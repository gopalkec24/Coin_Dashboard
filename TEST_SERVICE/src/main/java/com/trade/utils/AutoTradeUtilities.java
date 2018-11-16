package com.trade.utils;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;

public class AutoTradeUtilities {

	public static boolean isValidMarketData(BigDecimal value){
		boolean valid = false;
		int compare=value.compareTo(TraderConstants.NEGATIVE_ONE);
		if(compare!= TraderConstants.COMPARE_EQUAL)
		{
			valid = true;
		}
		TradeLogger.LOGGER.finest("Valid MarketData passed is valid : "+ valid);
		
		return valid;
	}
}
