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
	
	public static BigDecimal getBigDecimalValue(Object object) {
		BigDecimal validValue = TraderConstants.NEGATIVE_ONE;
		if(object instanceof String) {
			validValue = new BigDecimal((String)object);
		}
		else if(object instanceof Integer) {
			validValue = new BigDecimal((Integer)object);
		}
		else if(object instanceof Double) {
			validValue = new BigDecimal((Double)object);
		}
		TradeLogger.LOGGER.finest("Valid  : "+ validValue);
		return validValue;
	}
}
