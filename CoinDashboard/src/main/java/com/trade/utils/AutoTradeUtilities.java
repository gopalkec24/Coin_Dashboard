package com.trade.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import com.portfolio.utilis.GetCurrentMarketPrice;
import com.trade.constants.TraderConstants;

public class AutoTradeUtilities {

	public static final String STR_UNKNOWN = "UNKNOWN";
	public static final String STR_LESSER = "LESSER";
	public static final String STR_EQUAL = "EQUAL";
	public static final String STR_GREATER = "GREATER";

	
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
	
	public static boolean percentagePermissible(BigDecimal percent, BigDecimal mIN_PERMISSIBLE_PERCENT,
			BigDecimal mAX_PERMISSIBLE_PERCENT) {
		
		 int minCompare = percent.compareTo(mIN_PERMISSIBLE_PERCENT);
		 int maxCompare = percent.compareTo(mAX_PERMISSIBLE_PERCENT);
		 if(minCompare >=0 && maxCompare <=0 ){
			 return true;
		 }
		
			 TradeLogger.LOGGER.finest("Min compare value is "+ minCompare);
			 TradeLogger.LOGGER.finest("Max compare value is "+ maxCompare);
		
		 return false;
		
	}
public static Map<String,Object> getExchangePrice(String exchange, String coin, String currency) {		
		
		Map<String,Object> values = null;
		try {
	
		ArrayList<String> tradePair = new ArrayList<String>();
		String coinPair = coin+"/"+currency;
		tradePair.add(coinPair);
		GetCurrentMarketPrice get= new GetCurrentMarketPrice();
		Map<String,Map<String,Object>> exchangeValues = get.getCurrentMarketPrice(exchange,tradePair,2);
			if(exchangeValues!= null && exchangeValues.containsKey(coinPair)) 
			{
				values = exchangeValues.get(coinPair);
			}
		} catch (Exception e) {
			TradeLogger.LOGGER.log(Level.SEVERE,"Error in getting current market value",e);
		}
		return values;			 
	}

public static  BigDecimal getValidBigDecimal(Map<String, Object> values, String key) {
	
	if(values.get(key)!= null){
		return (BigDecimal) values.get(key);
	}
	else{
		return TraderConstants.NEGATIVE_ONE;
	}
}

public static BigDecimal getDifferInPercentage(BigDecimal lastPrice, BigDecimal basePrice) {
	
	int baseCompare = basePrice.compareTo(TraderConstants.BIGDECIMAL_ZERO);
	
	BigDecimal percent = baseCompare > 0 ? (lastPrice.subtract(basePrice).divide(basePrice, 4, RoundingMode.HALF_UP)).multiply(TraderConstants.HUNDRED) : TraderConstants.NEGATIVE_ONE;
	
	return percent;
	
}
public static String getCompareResultType(int compareResults) {
	String result;
	if(compareResults == TraderConstants.COMPARE_GREATER) {
		result = STR_GREATER;
	}
	else if(compareResults == TraderConstants.COMPARE_EQUAL) {
		result = STR_EQUAL;
		
	}
	else if(compareResults == TraderConstants.COMPARE_LOWER) {
		result =  STR_LESSER;
	}
	else {
		result =STR_UNKNOWN;
	}
	return result;
}

public static BigDecimal calcuateAmountByLimit(BigDecimal permissibleLimit, BigDecimal basePrice) {
	
	BigDecimal amt = basePrice.multiply(permissibleLimit).divide(TraderConstants.HUNDRED,RoundingMode.HALF_UP );
	TradeLogger.LOGGER.finest("Permissible amount in limit calcuation part :  " +permissibleLimit);
	amt = amt.setScale(basePrice.scale(),RoundingMode.HALF_UP);
	return amt;
}

public static boolean isNullorEmpty(String value) {
	
	if(value == null) {
		return true;
	}
	
	if(value.length() ==0 || value.equalsIgnoreCase("")) {
		return true;
	}
	 return false;
	
}

public static int reverseTransaction(int transactionType) {
	if(transactionType == TraderConstants.BUY_CALL){
		return TraderConstants.SELL_CALL;
	}
	else if(transactionType == TraderConstants.SELL_CALL){
		TradeLogger.LOGGER.info("Reserving the transaction from " + transactionType +" to "+ TraderConstants.BUY_CALL);
		return TraderConstants.BUY_CALL;
	}
	return 0;
}

public static boolean isZero(BigDecimal value) {
	int compare=value.compareTo(TraderConstants.BIGDECIMAL_ZERO);
	if(compare == TraderConstants.COMPARE_EQUAL) {
		return true;
	}
	return false;
}	
}
