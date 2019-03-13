package com.trade.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONObject;


import com.portfolio.utilis.GetCurrentMarketPrice;
import com.trade.constants.TraderConstants;

public class AutoTradeConfigReader {
	
	//To override the configFilePath
	public static String configFilePath = null;
	
	//Config value for AutoTrader Program
	public static Map<String,Object> configValues= null;

	private static long updatedCacheTime = -1;

	private static long defaultCacheTimeoutInMS = 180000;
	
	private static final String ORDER_WAIT_TIMEOUT = "orderWaitTimeOut";	
	private static final String MINIMUM_BUY_PERMISSIBLE_LIMT = "minimumBuyPermissibleLimt";
	private static final String MAXIMUM_BUY_PERMISSIBLE_LIMT = "maximumBuyPermissibleLimt";
	private static final String MINIMUM_SELL_PERMISSIBLE_LIMT = "minimumSellPermissibleLimt";
	private static final String MAXIMUM_SELL_PERMISSIBLE_LIMT = "maximumSellPermissibleLimt";
	private static final String ADVANCE_TRADE_MODE = "advanceTrading";
	
	private static final boolean DEFAULT_TRADE_MODE = false;	
	private static final String CLASS_NAME = null;
	private static final BigDecimal MIN_SELL_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	private static final BigDecimal MAX_SELL_PERMISSIBLE_PERCENT =  new BigDecimal("1");
	private static final BigDecimal MIN_BUY_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	private static final BigDecimal MAX_BUY_PERMISSIBLE_PERCENT = new BigDecimal("1");
	private static final String MINIMUM_CANCEL_PERMISSIBLE_LIMT = "minimumCancelOrderLimit";
	private static final BigDecimal MIN_CANCEL_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	private static final Object MAXIMUM_CANCEL_PERMISSIBLE_LIMT = "maximumCancelOrderLimit";
	private static final BigDecimal MAX_CANCEL_PERMISSIBLE_PERCENT =  new BigDecimal("0.20");
	private static final long DEFAULT_TRANSACTION_TIMEOUT = 86400000;
	private static final int DEFAULT_MAXIMUM_WAIT_COUNT = 10;

	private static final String PLACE_ORDER_MODE = "placeOrderMode";

	private static final String MAXIMUM_WAIT_COUNT = "maximumRetryCount";
	
	public void setConfigFilePath(String configFilePath) {		
		AutoTradeConfigReader.configFilePath = configFilePath;
	}
	
	public static String getConfigFilePath() {		
		return configFilePath;
	}
	
	public static Map<String,Object> getConfigValues() throws Exception{
		if( updatedCacheTime + defaultCacheTimeoutInMS < System.currentTimeMillis())
		{
			try
			{
				if (configFilePath!= null) 
				{
					configValues = loadJSONFromFileLocation(configFilePath);
					updatedCacheTime = System.currentTimeMillis();
				}
			} 
			catch (Exception e) 
			{					
				TradeLogger.LOGGER.log(Level.SEVERE, "Error in Loading config values from FileLocation "+ configFilePath, e);
				throw e;
			}
		}
		else 
		{
			TradeLogger.LOGGER.log(Level.FINEST,"Testing values here ::: "+ updatedCacheTime + " " + defaultCacheTimeoutInMS + " "+ System.currentTimeMillis());
		}
		return configValues;
	}

	static {
		TradeLogger.LOGGER.finest("Initialised static block");
		TradeLogger.LOGGER.setLevel(Level.FINEST);
				InputStream input = null;
				Properties prop = new Properties();
				String fileName="autotradeConfig.properties";
				try {
					input =AutoTradeConfigReader.class.getClassLoader().getResourceAsStream(fileName);
					if(input!=null) {
						prop.load(input);						
						configFilePath = prop.getProperty("configFileLocation");
						configValues = loadJSONFromFileLocation(configFilePath);
						updatedCacheTime = System.currentTimeMillis();
					}
					else {
						TradeLogger.LOGGER.severe("No Properties value found "+ fileName);
					}
				} 
				catch (Exception e) {
					
					e.printStackTrace();
				}
				finally {
					if(input!= null) {
						try {
							input.close();
						} catch (IOException e) {							
							e.printStackTrace();
						}
					}
				}
	}


	public static Map<String,Object> loadJSONFromFileLocation(String configFilePath2) throws Exception {		
		String response=GetCurrentMarketPrice.getDataFromJsonFile(configFilePath2);
		JSONObject jsonObj = new JSONObject(response);		
		return jsonObj.toMap();		
		
	}
	
	
	public static BigDecimal getMaxSellPermissibleLimit() {
		try {
			
			if(getConfigValues().containsKey(MAXIMUM_SELL_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_SELL_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Maximum Sell Permissible Limit " + MAX_SELL_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {	
			TradeLogger.LOGGER.logp(Level.WARNING, CLASS_NAME, "getMaxSellPermissibleLimit", "Error in getting Maximum Sell Permissible Limit", e);
			
		}
		return MAX_SELL_PERMISSIBLE_PERCENT;
	}
	public static BigDecimal getMinSellPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MINIMUM_SELL_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Minimum Sell Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MINIMUM_SELL_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Minimum Sell Permissible Limit " + MIN_SELL_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			TradeLogger.LOGGER.logp(Level.WARNING, CLASS_NAME, "getMinSellPermissibleLimit", "Error in getting Minimum Sell Permissible Limit", e);
			
		}
		return MIN_SELL_PERMISSIBLE_PERCENT;
	}
	
	public static BigDecimal getMaxBuyPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MAXIMUM_BUY_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Maximum Buy Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_BUY_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Maximum buy Permissible Limit " + MAX_BUY_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			TradeLogger.LOGGER.logp(Level.WARNING, CLASS_NAME, "getMaxBuyPermissibleLimit", "Error in getting Maximum Buy Permissible Limit", e);
		}
		return MAX_BUY_PERMISSIBLE_PERCENT;
	}
	public static BigDecimal getMinBuyPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MINIMUM_BUY_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Minimun Buy Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MINIMUM_BUY_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Minimum buy Permissible Limit " + MIN_BUY_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			TradeLogger.LOGGER.logp(Level.WARNING, CLASS_NAME, "getMinBuyPermissibleLimit", "Error in getting Minimum Buy Permissible Limit", e);
		}
		return MIN_BUY_PERMISSIBLE_PERCENT;
	}


	public static boolean isPlaceTradeEnabled() 
	{
		try {
			
			if(AutoTradeConfigReader.getConfigValues().containsKey(PLACE_ORDER_MODE)) {
				TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
				return	(Boolean) AutoTradeConfigReader.getConfigValues().get(PLACE_ORDER_MODE);
			}
			else {
				TradeLogger.LOGGER.finest("Using default Place order mode " + PLACE_ORDER_MODE);
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return DEFAULT_TRADE_MODE;
	}
public static boolean isAdvanceTradeEnabled() 
{
	try {
		
		if(AutoTradeConfigReader.getConfigValues().containsKey(ADVANCE_TRADE_MODE)) {
			TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
			return	(Boolean) AutoTradeConfigReader.getConfigValues().get(ADVANCE_TRADE_MODE);
		}
		else {
			TradeLogger.LOGGER.finest("Using default Maximum Sell Permissible Limit " + DEFAULT_TRADE_MODE);
		}
	} catch (Exception e) {			
		e.printStackTrace();
	}
	return DEFAULT_TRADE_MODE;
}

public static BigDecimal getMinCancelLimit() {
	try {
		if(AutoTradeConfigReader.getConfigValues().containsKey(MINIMUM_CANCEL_PERMISSIBLE_LIMT)) {
			TradeLogger.LOGGER.finest("Using  Minimum Sell Permissible Limit from properties file ");
			return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MINIMUM_CANCEL_PERMISSIBLE_LIMT));
		}
		else {
			TradeLogger.LOGGER.finest("Using default Minimum Sell Permissible Limit " + MIN_CANCEL_PERMISSIBLE_PERCENT);
		}
	} catch (Exception e) {
		TradeLogger.LOGGER.logp(Level.SEVERE,CLASS_NAME,"getMinCancelLimit","Error in getting the minCancelLimit",e);
		e.printStackTrace();
	}
	return MIN_CANCEL_PERMISSIBLE_PERCENT;
}
public static  BigDecimal getMaxCancelLimit() {
	try {
		if(AutoTradeConfigReader.getConfigValues().containsKey(MAXIMUM_CANCEL_PERMISSIBLE_LIMT)) {
			TradeLogger.LOGGER.finest("Using  Minimum Sell Permissible Limit from properties file ");
			return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_CANCEL_PERMISSIBLE_LIMT));
		}
		else {
			TradeLogger.LOGGER.finest("Using default Minimum Sell Permissible Limit " + MAX_CANCEL_PERMISSIBLE_PERCENT);
		}
	} catch (Exception e) {
		TradeLogger.LOGGER.logp(Level.SEVERE,CLASS_NAME,"getMaxCancelLimit","Error in getting the minCancelLimit",e);
		e.printStackTrace();
	}
	return MAX_CANCEL_PERMISSIBLE_PERCENT;
}

public static long getTransactionTimeOut() {
	try {
		
		if(AutoTradeConfigReader.getConfigValues().containsKey(ORDER_WAIT_TIMEOUT)) {
			TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
			return	Long.parseLong(AutoTradeConfigReader.getConfigValues().get(ORDER_WAIT_TIMEOUT)+"");
		}
		else {
			TradeLogger.LOGGER.finest("Using default Maximum Sell Permissible Limit " + DEFAULT_TRANSACTION_TIMEOUT);
		}
	} catch (Exception e) {			
		e.printStackTrace();
	}
	
	return DEFAULT_TRANSACTION_TIMEOUT;
}

public static int getWaitCount(){
	try {
		
		if(AutoTradeConfigReader.getConfigValues().containsKey(MAXIMUM_WAIT_COUNT)) {
			TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
			return	Integer.parseInt(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_WAIT_COUNT)+"");
		}
		else {
			TradeLogger.LOGGER.finest("Using default Maximum Wait Count " + DEFAULT_MAXIMUM_WAIT_COUNT);
		}
	} catch (Exception e) {			
		e.printStackTrace();
	}
	
	return DEFAULT_MAXIMUM_WAIT_COUNT;
}
}
