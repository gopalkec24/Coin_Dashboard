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
		/*TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "XLM", "USDT", new BigDecimal("0.00"), new BigDecimal("10.010"),TraderConstants.SELL_CALL);
		tradeVO1.setPlaceAvgPriceOrder(false);
		tradeVO1.setProfitType(2);
	//	tradeVO1.setBasePrice(new BigDecimal("0.00008666"));
		tradeVO1.setAdvanceTrade(false);
		tradeVO1.setBasePrice(new BigDecimal("0.123000"));
		tradeVO1.setMinPercentage(new BigDecimal("3"));
		tradeVO1.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "NEO", "USDT", new BigDecimal("0.00"), new BigDecimal("90.010"),TraderConstants.BUY_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
	//	tradeVO1.setBasePrice(new BigDecimal("0.00008666"));
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("12.0"));
		tradeVO2.setMinPercentage(new BigDecimal("7"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		
/*		TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "PHX", "ETH", new BigDecimal("0"), new BigDecimal("0.01001"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("0.00007900"));*/
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "XLM", "USDT", new BigDecimal("0"), new BigDecimal("15.52576"),TraderConstants.BUY_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("0.11416"));
		tradeVO2.setMinPercentage(new BigDecimal("1.5"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "TRX", "ETH", new BigDecimal("0"), new BigDecimal("0.027"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("0.00011197"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.003515"), new BigDecimal("0.00"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("3800.00"));
		tradeVO2.setMinPercentage(new BigDecimal("1"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "NEO", "USDT", new BigDecimal("0.0"), new BigDecimal("10.01"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("8.90"));
		tradeVO2.setMinPercentage(new BigDecimal("3"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
	/*	TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "AION", "ETH", new BigDecimal("34.0"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("0.001100"));
		tradeVO2.setMinPercentage(new BigDecimal("3"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
	/*	TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "ETH", "BTC", new BigDecimal("0.0"), new BigDecimal("0.001001"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("0.034100"));
		tradeVO2.setMinPercentage(new BigDecimal("3"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "ETH", "USDT", new BigDecimal("0.0"), new BigDecimal("10.01"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setBasePrice(new BigDecimal("273.77"));
		tradeVO2.setMinPercentage(new BigDecimal("1.5"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
//		TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "KEY", "ETH", new BigDecimal("585.00"), new BigDecimal("0.00"),TraderConstants.SELL_CALL);
//		tradeVO2.setPlaceAvgPriceOrder(false);
//		tradeVO2.setProfitType(1);
//		tradeVO2.setAdvanceTrade(false);
//		tradeVO2.setBasePrice(new BigDecimal("0.0000171"));
//		tradeVO2.setMinPercentage(new BigDecimal("10"));
//		tradeVO2.setMaxPercentage(new BigDecimal("1000"));
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "ONT", "USDT", new BigDecimal("0.0"), new BigDecimal("10.0016"),TraderConstants.BUY_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("1.12"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "ICX", "USDT", new BigDecimal("0.00"), new BigDecimal("27.00"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.38"));
		tradeVO2.setMinPercentage(new BigDecimal("13"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
	/*	TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "LTC", "USDT", new BigDecimal("0.12512500"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("90.00"));
		tradeVO2.setMinPercentage(new BigDecimal("12"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "XVG", "ETH", new BigDecimal("224.0"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(1);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.00004500"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		
		TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "DNT", "ETH", new BigDecimal("243"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(1);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.000084"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));
		/*TradeDataVO tradeVO2= new TradeDataVO("BINANCE", "ZIL", "USDT", new BigDecimal("584.8"), new BigDecimal("0.0"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(1);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.0171"));
		tradeVO2.setMinPercentage(new BigDecimal("3"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		/*TradeDataVO tradeVO2= new TradeDataVO("BITSO", "XRP", "MXN", new BigDecimal("0.0"), new BigDecimal("1000.0"),TraderConstants.BUY_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(1);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("8.50"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
	/*	TradeDataVO tradeVO2= new TradeDataVO("LIVECOIN", "DIG", "ETH", new BigDecimal("0.0"), new BigDecimal("0.021875"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.00002500"));
		tradeVO2.setMinPercentage(new BigDecimal("30"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		/*TradeDataVO tradeVO2= new TradeDataVO("LIVECOIN", "DIG", "USD", new BigDecimal("1000.0"), new BigDecimal("0.00"),TraderConstants.SELL_CALL);
		tradeVO2.setPlaceAvgPriceOrder(false);
		tradeVO2.setProfitType(2);
		tradeVO2.setAdvanceTrade(false);
		tradeVO2.setBasePrice(new BigDecimal("0.006500"));
		tradeVO2.setMinPercentage(new BigDecimal("10"));
		tradeVO2.setMaxPercentage(new BigDecimal("1000"));*/
		
		List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		list.add(tradeVO2);
		String jsonFilePathNew = "C:/Documents/autotradeData4.json";
		AutoTradeVO tradeData;
		try 
		{
			tradeData = readAutoTradeDataFromJSONNew(jsonFilePathNew);
			List<TradeDataVO> list1=tradeData.getTradeData();
			list1.addAll(list);
			AutoTradeVO tradeDataVO = new AutoTradeVO();
			tradeDataVO.setLastUpdatedTime(new Date().getTime());
			tradeDataVO.setTradeData(list1);
			writeAutoTradeDataToJSON(jsonFilePathNew, tradeDataVO);
		} 
		catch (JsonSyntaxException e) 
		{			
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
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
