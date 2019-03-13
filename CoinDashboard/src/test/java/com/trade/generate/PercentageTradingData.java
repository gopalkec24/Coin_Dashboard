package com.trade.generate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.trade.constants.TraderConstants;
import com.trade.dao.AutoTradeVO;
import com.trade.dao.TradeDataVO;

public class PercentageTradingData {

	
	public static void main(String[] args) {
		addDataToExistingFile();
		//addFreshData();
	}
	private static void addDataToExistingFile() {
		// TODO Auto-generated method stub
		TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "PHX", "ETH", new BigDecimal("0.00"), new BigDecimal("0.010009"),TraderConstants.BUY_CALL);
		tradeVO1.setPlaceAvgPriceOrder(false);
		tradeVO1.setBasePrice(new BigDecimal("0.00008666"));
		tradeVO1.setAdvanceTrade(false);
		List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		list.add(tradeVO1);
		String jsonFilePathNew = "C:/Documents/autotradeData2.json";
		AutoTradeVO tradeData;
		try {
			tradeData = readAutoTradeDataFromJSONNew(jsonFilePathNew);
			List<TradeDataVO> list1=tradeData.getTradeData();
			list1.addAll(list);
			 AutoTradeVO tradeDataVO = new AutoTradeVO();
			  tradeDataVO.setLastUpdatedTime(new Date().getTime());
			  tradeDataVO.setTradeData(list1);
			  writeAutoTradeDataToJSON(jsonFilePathNew, tradeDataVO);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static AutoTradeVO readAutoTradeDataFromJSONNew(String jsonFilePath) throws JsonSyntaxException, IOException {
		Gson gson = new Gson();
		 Type listType = new TypeToken<AutoTradeVO>(){}.getType();
		 AutoTradeVO configValues= gson.fromJson(FileUtils.readFileToString(new File(jsonFilePath)),listType);
		return configValues;
	}

	
	private static void addFreshData() {
		TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "NEO", "USDT", new BigDecimal("0.00"), new BigDecimal("10.30"),TraderConstants.BUY_CALL);
		tradeVO1.setPlaceAvgPriceOrder(false);
		tradeVO1.setAdvanceTrade(false);
		
		TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "LTC", "USDT", new BigDecimal("0.18412"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		
		String jsonFilePathNew = "C:/Documents/autotradeData2.json";
		
		List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		
		list.add(tradeVO1);
		list.add(tradeVO2);
		
		writeDataToFile(jsonFilePathNew, list);
	}
	private static void writeDataToFile(String jsonFilePathNew, List<TradeDataVO> list) {
		AutoTradeVO tradeDataVO = new AutoTradeVO();
		 Date dt = new Date();
		  tradeDataVO.setLastUpdatedTime(dt.getTime());
		  tradeDataVO.setTradeData(list);
		  writeAutoTradeDataToJSON(jsonFilePathNew, tradeDataVO);
	}
	private static void writeAutoTradeDataToJSON(String jsonFilePath, AutoTradeVO tradeDataVO) {
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
