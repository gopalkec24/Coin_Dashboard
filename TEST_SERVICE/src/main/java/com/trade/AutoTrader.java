package com.trade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.portfolio.dao.FetchConfiguration;
import com.trade.constants.TraderConstants;
import com.trade.dao.TradeDataVO;
import com.trade.utils.TradeLogger;

public class AutoTrader {
	
	
	
	private static final int COMPARE_LOWER = -1;
	private static final int COMPARE_GREATER = 1;
	private static final int COMPARE_EQUAL = 0;
	/*public static Logger LOGGER= Logger.getLogger(AutoTrader.class.getName());
	static{
		Handler handler = new ConsoleHandler();
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.ALL);
	}*/
	private static final String LAST_PRICE = "lastPrice";
	private static final String LOW_PRICE = "lowPrice";
	private static final String HIGH_PRICE = "highPrice";
	private static final int MAXIMUM_BUY_WAIT_COUNT = 10;

	public static void main(String[] args){
	
		AutoTrader trader = new AutoTrader();
		//new TradeDataVO for TESTing object for Buy scenario
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		//new TradeDataVO for Testing object for sell Scenaio
		TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "ETH", "USDT", new BigDecimal("0.0125"), new BigDecimal("0.00"));
		/*TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));*/
		String jsonFilePath = "D:/Documents/autotradeData.json";
		
		//List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		List<TradeDataVO> list;
		try {
			list = readAutoTradeDataFromJSON(jsonFilePath);
			TradeLogger.LOGGER.info(list.toString());
			trader.process(list);
			
			writeAutoTradeDataToJSON(jsonFilePath,list);
			
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*list.add(tradeVO);
		list.add(tradeVO1);*/
		
		
		
	
	}
	private static List<TradeDataVO> readAutoTradeDataFromJSON(String jsonFilePath) throws JsonSyntaxException, IOException{

		Gson gson = new Gson();
		 Type listType = new TypeToken<List<TradeDataVO>>(){}.getType();
		 List<TradeDataVO> configValues= gson.fromJson(FileUtils.readFileToString(new File(jsonFilePath)),listType);

		return configValues;
		
	}
	private static void writeAutoTradeDataToJSON(String jsonFilePath,List<TradeDataVO> list) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			FileUtils.write(new File(jsonFilePath),mapper.writeValueAsString(list));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void process(List<TradeDataVO> transactionData) {

		for(TradeDataVO data : transactionData){
			//TODO initial a thread here to process individual data quickly	
			TradeLogger.LOGGER.finest("Before the processing  : "+data.toString());
			processData(data);
			TradeLogger.LOGGER.finest("After the processing  : "+data.toString());
		}
		
		
		
	}

	private void processData(TradeDataVO data) {
		
		if(data.getTriggerEvent() == TraderConstants.COUNTER_NOINIT)
		{
			
				initializeLastPrice(data);		
			
			
		}
		else if(data.getTriggerEvent() == TraderConstants.INTITAL_TRIGGER)
		{
			TradeLogger.LOGGER.info(data.toString());
			checkConditionTriggerTransaction(data);
		}
		else if(data.getTriggerEvent() == TraderConstants.LIMITORDER_TRIGGER){
			
		}
		else if(data.getTriggerEvent() == TraderConstants.MARKETORDER_TRIGGER){
			
		}
		
	}
	
	
	private void checkConditionTriggerTransaction(TradeDataVO data) {
		Map<String,Object> values= getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) 
		{	
			BigDecimal lastPrice = getValidBigDecimal(values,LAST_PRICE);

			BigDecimal lowPrice = getValidBigDecimal(values,LOW_PRICE);

			BigDecimal highPrice = getValidBigDecimal(values,HIGH_PRICE);
			boolean advance= false;
			if(isValidMarketData(data.getLastPrice()) && isValidMarketData(lastPrice))
			{
				// 1 = current Price is higher than previous last Price
				// -1 = Current Price is lower than Previous last Price
				// 0 = Current Price is equal to previous last Price
				int lastCompare = lastPrice.compareTo(data.getLastPrice());


				//Trade transaction of type Buy Call
				if(data.getTransactionType() == TraderConstants.BUY_CALL && data.isLastBuyCall() )
				{	
					int lowCompare = -1;
					if(isValidMarketData(data.getTriggeredPrice()) && isValidMarketData(lowPrice)){
						
						lowCompare = data.getTriggeredPrice().compareTo(lowPrice);
					}
					if(lowCompare == COMPARE_GREATER){
						
						reinitilizeTriggerPrice(data, lowPrice);
					}
					else 
					{
						if(lastCompare == COMPARE_GREATER)
						{

							//checking for maximum retry processing 
							if(data.getWaitCount() <= MAXIMUM_BUY_WAIT_COUNT){
								data.increaseHigherCount();
								data.increaseWaitCount();
							}
							else
							{
								if(data.getLowCount() >= data.getHighCount())
								{
									//advance 2
									if(advance)
									{
										//List of last Price 
										List<BigDecimal> lastPriceList = new ArrayList<BigDecimal>();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										boolean buyTrigger = percentagePermissible(percentageDiffer, TraderConstants.MIN_PERMISSIBLE_PERCENT, TraderConstants.MAX_PERMISSIBLE_PERCENT);
										if(buyTrigger){
											//buy order with trigger price
										}
										else{
											//place buy with avg price or Reinitailize the count
											//
										}
										
									}
									else
									{
										
									}
								}
								else
								{
									
								}
							}
						}
						else if(lastCompare == COMPARE_EQUAL){
							//TODO need to check lower and Higher Count
							data.increaseWaitCount();
							data.increaseLowCount();

						}
						else if(lastCompare == COMPARE_LOWER){
							reinitilizeTriggerPrice(data, lastPrice);
						}
						
					}
					
				}
				//Trade transaction Type is Sell call
				else if(data.getTransactionType() == TraderConstants.SELL_CALL && data.isLastSellCall())
				{
					
					int highCompare = -1;
					if(isValidMarketData(data.getTriggeredPrice()) && isValidMarketData(lowPrice)){
						
						highCompare = highPrice.compareTo(data.getTriggeredPrice());
					}
					if(highCompare == COMPARE_GREATER){
						
						reinitilizeTriggerPrice(data, highPrice);
					}
					else
					{

						if(lastCompare == COMPARE_GREATER)
						{
							reinitilizeTriggerPrice(data, lastPrice);
						}
						else if(lastCompare == COMPARE_EQUAL){
							//TODO need to check lower
							data.increaseWaitCount();
							data.increaseHigherCount();

						}
						else if(lastCompare == COMPARE_LOWER)
						{
							//checking for maximum retry processing 
							if(data.getWaitCount() <= MAXIMUM_BUY_WAIT_COUNT){
								data.increaseLowCount();
								data.increaseWaitCount();
							}
							else
							{

							}

						}
						
					}
					//TODO Check later for this neccessary. Right now setting outside all condition
					/*data.setHighPrice(highPrice);
					data.setLowPrice(lowPrice);*/
				}
				else
				{

					//invalid scenario occured
					TradeLogger.LOGGER.warning("Invalid scenario occured in Trigger Transaction Method");
				}
				data.setLowPrice(lowPrice);
				data.setHighPrice(highPrice);
				data.setLastPrice(lastPrice);
				

			}

		}
		
	}
	private BigDecimal getAvgPrice(List<BigDecimal> lastPriceList) {
		BigDecimal totalValue = TraderConstants.BIGDECIMAL_ZERO;
		for(int i=0; i<lastPriceList.size();i++){
			totalValue= totalValue.add(lastPriceList.get(i));
		}
		if(lastPriceList.size()!= 0){
		totalValue = totalValue.divide(new BigDecimal(lastPriceList.size()),2,RoundingMode.HALF_UP);
		}
		return totalValue;
	}
	private void reinitilizeTriggerPrice(TradeDataVO data, BigDecimal lastPrice) {		
		data.setTriggeredPrice(lastPrice);
		data.setHighCount(TraderConstants.COUNTER_NOINIT);
		data.setLowCount(TraderConstants.COUNTER_NOINIT);
		data.setWaitCount(TraderConstants.COUNTER_NOINIT);
		data.increaseReTriggerCount();
	}
	private boolean isValidMarketData(BigDecimal value){
		boolean valid = false;
		int compare=value.compareTo(TraderConstants.NEGATIVE_ONE);
		if(compare!= COMPARE_EQUAL)
		{
			valid = true;
		}
		TradeLogger.LOGGER.finest("Valid MarketData passed is valid : "+ valid);
		
		return valid;
	}
	private void initializeLastPrice(TradeDataVO data){
		
		Map<String,Object> values= getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) {			
			BigDecimal lastPrice = getValidBigDecimal(values,LAST_PRICE);
			
			BigDecimal lowPrice = getValidBigDecimal(values,LOW_PRICE);
			
			BigDecimal highPrice = getValidBigDecimal(values,HIGH_PRICE);
			
			
			boolean buyTrigger=false;
			boolean sellTrigger = false;
			
			if(data.getTransactionType() ==TraderConstants.BUY_CALL  && isValidMarketData(lastPrice) && isValidMarketData(lowPrice)){
				int buyCompare = lastPrice.compareTo(lowPrice) ;
				//last price can be equal or more than low price , 
				//but could not be less than low price
				// so buyCompare cannot be -1, it should be either 0 or 1								
				if(buyCompare == COMPARE_EQUAL){
					buyTrigger= true;
					TradeLogger.LOGGER.finest("Buy Order @Last Price : "+lastPrice);
				}
				else if(buyCompare == COMPARE_GREATER){
					BigDecimal percent = getDifferInPercentage(lastPrice,lowPrice);
					buyTrigger = percentagePermissible(percent,TraderConstants.MIN_PERMISSIBLE_PERCENT,TraderConstants.MAX_PERMISSIBLE_PERCENT);
					TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
					if (buyTrigger) {
						TradeLogger.LOGGER.finest("Differential Buy Order @Last Price : " + lastPrice  +" with Percent"+percent);
					}
					
				}
				else{
					TradeLogger.LOGGER.warning("Invalid Scenario Raised in buy compare ");
				}
			}
			else if(data.getTransactionType()==TraderConstants.SELL_CALL && isValidMarketData(lastPrice) && isValidMarketData(highPrice)){
				int sellCompare = lastPrice.compareTo(highPrice);
				//last price can be equal or less than high Price, 
				//but could not be more than highprice
				// so sellCompare cannot be 1, it should be either 0 or -1	
					if(sellCompare == COMPARE_EQUAL){
						sellTrigger=true;
						TradeLogger.LOGGER.finest("Sell Order @Last Price : " + lastPrice);
					}
					else if(sellCompare == COMPARE_LOWER){
						BigDecimal percent = getDifferInPercentage(highPrice, lastPrice);
						sellTrigger = percentagePermissible(percent,TraderConstants.MIN_PERMISSIBLE_PERCENT,TraderConstants.MAX_PERMISSIBLE_PERCENT);
						TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
						if (sellTrigger) {
							TradeLogger.LOGGER.finest("Differential Sell Order @Last Price : " + lastPrice +" with Percent"+percent);
						}
						
					}
					else{
						TradeLogger.LOGGER.warning("Invalid Scenario Raised in sell compare ");
					}
				
				}
			if(buyTrigger || sellTrigger){
			data.setLastPrice(lastPrice);
			data.setLowPrice(lowPrice);
			data.setHighPrice(highPrice);
			data.setLastBuyCall(buyTrigger);
			data.setLastSellCall(sellTrigger);
			data.setTriggeredPrice(lastPrice);
			data.setTriggerEvent(TraderConstants.INTITAL_TRIGGER);
			data.setWaitCount(TraderConstants.ZERO_COUNT);
			data.setLowCount(TraderConstants.ZERO_COUNT);
			data.setHighCount(TraderConstants.ZERO_COUNT);
			TradeLogger.LOGGER.warning("TRIGGERED SCENARIO");
			}
			else{
				TradeLogger.LOGGER.warning("NO Scenario TRIGGERED ");
			}
			
		}
		
	}

	private boolean percentagePermissible(BigDecimal percent, BigDecimal mIN_PERMISSIBLE_PERCENT,
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

	private BigDecimal getDifferInPercentage(BigDecimal lastPrice, BigDecimal basePrice) {
		
		int baseCompare = basePrice.compareTo(TraderConstants.BIGDECIMAL_ZERO);
		
		BigDecimal percent = baseCompare > 0 ? (lastPrice.subtract(basePrice).divide(basePrice, 4, RoundingMode.HALF_UP)).multiply(TraderConstants.HUNDRED) : TraderConstants.NEGATIVE_ONE;
		
		return percent;
		
	}

	private BigDecimal getValidBigDecimal(Map<String, Object> values, String key) {
		
		if(values.get(key)!= null){
			return (BigDecimal) values.get(key);
		}
		else{
			return TraderConstants.NEGATIVE_ONE;
		}
	}

	private Map<String,Object> getExchangePrice(String exchange, String coin, String currency) {
		
		Map<String,Object> values = new TreeMap<String, Object>();
		
		values.put(LAST_PRICE,new BigDecimal("6660.00"));
		values.put(LOW_PRICE,new BigDecimal("6650.00"));
		values.put(HIGH_PRICE,new BigDecimal("6675.00"));
		
		return values;
		
		
	}

}
