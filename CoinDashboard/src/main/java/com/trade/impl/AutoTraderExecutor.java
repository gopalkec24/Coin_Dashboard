package com.trade.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.trade.dao.AutoTradeVO;
import com.trade.dao.TradeDataVO;
import com.trade.inf.AutoTradeFactory;
import com.trade.inf.IAutoTrader;
import com.trade.utils.TradeLogger;

public class AutoTraderExecutor {

	public static void main(String[] args) {

		//Read the json from filesystem
		String jsonFilePath = "C:/Documents/autotradeData1.json";
		String traderType= "AdvanceTrader";
		long waitTimeInMS = 420000;
		int executionFrequency = 1000;
		startTrade(jsonFilePath, traderType, waitTimeInMS, executionFrequency);
	}
	public static void startTrade(String jsonFilePath, String traderType, long waitTimeInMS, int executionFrequency) {
		for (int i = 0; i < executionFrequency; i++) {

			TradeLogger.LOGGER.
			info("LOOPING COUNT =============================================>"+i);
			List<TradeDataVO> list1; 
			try { 
				AutoTradeVO tradeData =   readAutoTradeDataFromJSONNew(jsonFilePath);
				 list1=tradeData.getTradeData();
				 process(list1,traderType);
				  Date dt = new Date();
				  AutoTradeVO tradeDataVO = new AutoTradeVO();
				  tradeDataVO.setLastUpdatedTime(dt.getTime());
				  tradeDataVO.setTradeData(list1);
				  writeAutoTradeDataToJSON(jsonFilePath, tradeDataVO);
			}
			catch (JsonSyntaxException e) {
				
				TradeLogger.LOGGER.log(Level.SEVERE, "Error in Writing the JSON file", e);

			} 
			catch (IOException e) {
				TradeLogger.LOGGER.log(Level.SEVERE, "Error in I/O Operation file", e);
				e.printStackTrace();

			}

			try { TradeLogger.LOGGER.info("Going to sleep for " + waitTimeInMS/60000+ " minutes . " +new Date());
			Thread.sleep(waitTimeInMS);
			TradeLogger.LOGGER.info("Going to wake up  now : " +new Date()); 
			}
			catch (InterruptedException e) {

				TradeLogger.LOGGER.log(Level.SEVERE, "Error in Thread Operation", e);
			}

		}
	}
	private static AutoTradeVO readAutoTradeDataFromJSONNew(String jsonFilePath) throws JsonSyntaxException, IOException {
		Gson gson = new Gson();
		 Type listType = new TypeToken<AutoTradeVO>(){}.getType();
		 AutoTradeVO configValues= gson.fromJson(FileUtils.readFileToString(new File(jsonFilePath)),listType);
		return configValues;
	}
	public static void process(List<TradeDataVO> transactionData,String traderType) {
		
		IAutoTrader trader = AutoTradeFactory.getAutoTrader(traderType);
		
		if (trader!=null) 
		{
			for (TradeDataVO data : transactionData) 
			{
				try 
				{
					//TODO initial a thread here to process individual data quickly	
					TradeLogger.LOGGER.finest("Before the processing  : " + data.toString());
					trader.processData(data);
					TradeLogger.LOGGER.finest("After the processing  : " + data.toString());
				} catch (Exception e) {
					TradeLogger.LOGGER.log(Level.SEVERE, "Error in processing the data object", e);

				}
			}
			if (trader.getNewTradeOrderList() != null && !trader.getNewTradeOrderList().isEmpty()) {
				transactionData.addAll(trader.getNewTradeOrderList());
				TradeLogger.LOGGER.info("Added new OrderList to existing list");
				trader.getNewTradeOrderList().clear();
				TradeLogger.LOGGER.info("Cleared new OrderList to avoid the duplicate one");
			} 
		}
		
		
		
	}
	public static void writeAutoTradeDataToJSON(String jsonFilePath, AutoTradeVO tradeDataVO) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			FileUtils.write(new File(jsonFilePath),mapper.defaultPrettyPrintingWriter().writeValueAsString(tradeDataVO));
		} catch (JsonGenerationException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
}
