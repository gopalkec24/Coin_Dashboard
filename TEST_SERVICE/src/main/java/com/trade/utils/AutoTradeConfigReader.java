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
	
	
	public void setConfigFilePath(String configFilePath) {		
		AutoTradeConfigReader.configFilePath = configFilePath;
	}
	
	public static String getConfigFilePath() {		
		return configFilePath;
	}
	
	public static Map<String,Object> getConfigValues(){
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
					}
					else {
						TradeLogger.LOGGER.severe("No Properties value found "+ fileName);
					}
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					if(input!= null) {
						try {
							input.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
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
