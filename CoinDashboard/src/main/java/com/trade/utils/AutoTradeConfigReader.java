package com.trade.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONObject;


import com.portfolio.utilis.GetCurrentMarketPrice;

public class AutoTradeConfigReader {
	
	//To override the configFilePath
	public static String configFilePath = null;
	
	//Config value for AutoTrader Program
	public static Map<String,Object> configValues= null;

	private static long updatedCacheTime = -1;

	private static long defaultCacheTimeoutInMS = 180000;
	
	
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
		else {
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
}
