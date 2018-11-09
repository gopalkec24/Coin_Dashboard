package com.trade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
import com.portfolio.utilis.GetCurrentMarketPrice;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.MarketStaticsVO;
import com.trade.dao.TradeDataVO;
import com.trade.utils.TradeLogger;

public class AutoTrader {
	
	
	
	
	public static final String STR_UNKNOWN = "UNKNOWN";
	public static final String STR_LESSER = "LESSER";
	public static final String STR_EQUAL = "EQUAL";
	public static final String STR_GREATER = "GREATER";
	private static final int MAXIMUM_BUY_WAIT_COUNT = 10;
	private static final int MAXIMUM_SELL_WAIT_COUNT = 10;
	public static final int MAX_RETRIGGER_COUNT = 3;
	public static final BigDecimal MIN_SELL_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	public static final BigDecimal MAX_SELL_PERMISSIBLE_PERCENT =  new BigDecimal("2.75");
	public static final BigDecimal MIN_BUY_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	public static final BigDecimal MAX_BUY_PERMISSIBLE_PERCENT = new BigDecimal("2.75");

	public static void main(String[] args){
	
		AutoTrader trader = new AutoTrader();
		//new TradeDataVO for TESTing object for Buy scenario
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		//new TradeDataVO for Testing object for sell Scenaio
		TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "ETH", "USDT", new BigDecimal("0.0125"), new BigDecimal("0.00"));
		
		/*TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));*/
		String jsonFilePath = "C:/Documents/autotradeData.json";
		
	/*	List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		list.add(tradeVO);
		list.add(tradeVO1);
		
		
		trader.process(list);
		writeAutoTradeDataToJSON(jsonFilePath,list);*/
		
		for (int i = 0; i < 20; i++) {
			
			
			List<TradeDataVO> list;
			try {
				list = readAutoTradeDataFromJSON(jsonFilePath);
				TradeLogger.LOGGER.info(list.toString());
				trader.process(list);

				writeAutoTradeDataToJSON(jsonFilePath, list);

			} catch (JsonSyntaxException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} 
			
			try {
				TradeLogger.LOGGER.info("Going to sleep by 50 second" +new Date());
				Thread.sleep(50000);
				TradeLogger.LOGGER.info("Going to wake up" +new Date());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	
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
			
			FileUtils.write(new File(jsonFilePath),mapper.defaultPrettyPrintingWriter().writeValueAsString(list));
		} catch (JsonGenerationException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
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
		else if(data.getTriggerEvent() == TraderConstants.ORDER_TRIGGER){
			
		}
		
		
	}
	
	private String getCompareResultType(int compareResults) {
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
	private void checkConditionTriggerTransaction(TradeDataVO data) {
		Map<String,Object> values= getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) 
		{	
			BigDecimal lastPrice = getValidBigDecimal(values,TraderConstants.LAST_PRICE);

			BigDecimal lowPrice = getValidBigDecimal(values,TraderConstants.LOW_PRICE);

			BigDecimal highPrice = getValidBigDecimal(values,TraderConstants.HIGH_PRICE);
			boolean advance= true;
			ATOrderDetailsVO placeOrder = null;
			if(isValidMarketData(data.getLastPrice()) && isValidMarketData(lastPrice))
			{
				// 1 = current Price is higher than previous last Price
				// -1 = Current Price is lower than Previous last Price
				// 0 = Current Price is equal to previous last Price
				int lastCompare = lastPrice.compareTo(data.getLastPrice());
				TradeLogger.LOGGER.finer(" Previous Triggered Price : "+ data.getTriggeredPrice() + " Previous last Price : "+ data.getLastPrice() + " Current Last Price : " +lastPrice);
				//Trade transaction of type Buy Call
				if(data.getTransactionType() == TraderConstants.BUY_CALL && data.isLastBuyCall() )
				{	
					int lowCompare = -2;
					TradeLogger.LOGGER.finer("Current Low Price : "+lowPrice);
					//Check the price gets lower than triggered Price
					if(isValidMarketData(data.getTriggeredPrice()) && isValidMarketData(lowPrice))
					{
						lowCompare = data.getTriggeredPrice().compareTo(lowPrice);
						TradeLogger.LOGGER.finest("Comparing the last Triggered Price with Current Low Price "+ getCompareResultType(lowCompare));
					}
					TradeLogger.LOGGER.finest("Comparing the Current last Price  with  previous last Price "+ getCompareResultType(lastCompare));
					//low price is lower than triggered Price
					if(lowCompare == TraderConstants.COMPARE_GREATER)
					{
						
						//then Reinitialize Trigger Price count
						TradeLogger.LOGGER.finest("New triggered price with current low Price.");
						//Initial fresh Trade here
						//Making new Target here for me
						initializeFreshTrade(data,lowPrice);				
					}
					// low Price is more than triggered Price
					else 
					{		
						//last price is greater than previous Last price
						//indicating that price moving to upwards direction
						if(lastCompare == TraderConstants.COMPARE_GREATER)
						{
							//checking for maximum retry processing 
							if(data.getWaitCount() <= MAXIMUM_BUY_WAIT_COUNT)
							{
								TradeLogger.LOGGER.finest("Increasing the wait count with higher Count , adding history as well");
								data.addPriceHistory(getMarketStaticsVO(values,(data.getReTriggerCount()*10) +data.getWaitCount()));
								data.increaseHigherCount();
								data.increaseWaitCount();
								
							}
							else
							{

								//start of advance Trading 
								//Advanced Trading 
								//advance 2
								if(advance)
								{
									//higher count is more than Low count, price is moving towards up direction very fastly
									if(data.getLowCount() < data.getHighCount())
									{									
										//List of last Price 
										List<MarketStaticsVO> lastPriceList = data.getPriceHistory();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										TradeLogger.LOGGER.finest("Differential Percentage is "+percentageDiffer);
										boolean buyTrigger = percentagePermissible(percentageDiffer, TraderConstants.MIN_PERMISSIBLE_PERCENT, TraderConstants.MAX_PERMISSIBLE_PERCENT);
										if(buyTrigger)
										{
											//buy order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with TriggerPrice");
											placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											
										}
										else
										{
											//place buy with avg price or Reinitailize the count

											if(data.isPlaceAvgPriceOrder()) 
											{
												////place buy with avg price
												TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with Avg price");
												placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
												
											}
											else 
											{

												if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) {
													//Found new target here with trigger price as avgLast Price
													initializeFreshTrade(data,avgLastPrice);
												}
												else 
												{
													//place buy with avg price
													TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with Avg price");
													placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
												}
											}
										}
									}
									
									else {

										if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
										{											
											reinitilizeReTriggerCount(data, data.getTriggeredPrice());
										}
										else 
										{
											//place buy with triggered Price here
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
										}

									}
								}
								//End of advance Trading
								else
								{
									//Basic Trade 
									//Buy order with triggered price here
									TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
									placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
								}
							
								
							}
						}
					/*	else if(lastCompare == COMPARE_EQUAL){
							
							data.addPriceHistory(getMarketStaticsVO(values,data.getWaitCount()));							
							data.increaseWaitCount();
							data.increaseLowCount();

						}*/
						//indicating price is either moving to downwards or stay there
						else if(lastCompare == TraderConstants.COMPARE_LOWER || lastCompare == TraderConstants.COMPARE_EQUAL)
						{
							int triggerCompare = lastPrice.compareTo(data.getTriggeredPrice());
							if(triggerCompare == TraderConstants.COMPARE_LOWER) 
							{
							TradeLogger.LOGGER.finest("Fresh initailzing the triggered price with current last Price.");
							//Found New target for me.							
							initializeFreshTrade(data, lastPrice);
							}
							else
							{
								if(data.getWaitCount() <= MAXIMUM_BUY_WAIT_COUNT)
								{
								data.addPriceHistory(getMarketStaticsVO(values,(data.getReTriggerCount()*10) +data.getWaitCount()));								
								data.increaseWaitCount();
								data.increaseLowCount();
								}
								else 
								{
									//TODO alternate options
									//Right now reinitializing the triggeredPrice again
									//Make sure that you wait for triggered price
									//Check for alternate option 
									reinitilizeReTriggerCount(data,data.getTriggeredPrice());
								}
								
							}
						}
						
					}
					
				}
				//Trade transaction Type is Sell call
				else if(data.getTransactionType() == TraderConstants.SELL_CALL && data.isLastSellCall())
				{
					
					int highCompare = -2;
					if(isValidMarketData(data.getTriggeredPrice()) && isValidMarketData(lowPrice)){
						
						highCompare = highPrice.compareTo(data.getTriggeredPrice());
					}
					if(highCompare == TraderConstants.COMPARE_GREATER){
						
						reinitilizeTriggerPrice(data, highPrice);
					}
					else
					{

						if(lastCompare == TraderConstants.COMPARE_GREATER || lastCompare == TraderConstants.COMPARE_EQUAL)
						{
							int triggerCompare = lastPrice.compareTo(data.getTriggeredPrice());
							if(triggerCompare == TraderConstants.COMPARE_GREATER) 
							{
							reinitilizeTriggerPrice(data, lastPrice);
							}
							else 
							{
								
								data.addPriceHistory(getMarketStaticsVO(values,(data.getReTriggerCount()*10) +data.getWaitCount()));
								data.increaseWaitCount();
								data.increaseHigherCount();
							}
							
						}
						/*else if(lastCompare == TraderConstants.COMPARE_EQUAL){
							data.addPriceHistory(getMarketStaticsVO(values,data.getWaitCount()));
							//TODO need to check lower
							data.increaseWaitCount();
							data.increaseHigherCount();

						}*/
						else if(lastCompare == TraderConstants.COMPARE_LOWER)
						{
							//checking for maximum retry processing 
							if(data.getWaitCount() <= MAXIMUM_SELL_WAIT_COUNT){
								data.addPriceHistory(getMarketStaticsVO(values,(data.getReTriggerCount()*10) +data.getWaitCount()));
								data.increaseLowCount();
								data.increaseWaitCount();
							}
							else
							{


								//start of advance Trading 
								//Advanced Trading 
								//advance 2
								if(advance)
								{

									//higher count is more than Low count, price is moving towards down direction very fastly
									if(data.getLowCount() >= data.getHighCount())
									{									
										//List of last Price 
										List<MarketStaticsVO> lastPriceList = data.getPriceHistory();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										TradeLogger.LOGGER.finest("Differential Percentage is "+percentageDiffer);
										boolean sellTrigger = percentagePermissible(percentageDiffer, TraderConstants.MIN_PERMISSIBLE_PERCENT, TraderConstants.MAX_PERMISSIBLE_PERCENT);
										if(sellTrigger)
										{
											//sell order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
										}
										else
										{
											//place sell with avg price or Reinitailize the count

											if(data.isPlaceAvgPriceOrder()) 
											{
												//place sell with avg price
												TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
												placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
											}
											else 
											{

												if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) {
													//Found new target here with trigger price as avgLast Price
													initializeFreshTrade(data,avgLastPrice);
												}
												else 
												{
													//place sell with avg price
													TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
													placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
												}
											}
										}
									}
									
									else 
									{

										if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
										{											
											reinitilizeReTriggerCount(data, data.getTriggeredPrice());
										}
										else 
										{
											//place buy with triggered Price here
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
										}

									}
								}
								//End of advance Trading
								else
								{
									//Basic Trade 
									//sell order with triggered price here
									TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
									placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
								}
								
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
				
				if(data.getTriggerEvent() == TraderConstants.ORDER_TRIGGER && placeOrder!= null) {
					placeOrderToExchange(placeOrder);
				}
				data.setPreviousLastPrice(data.getLastPrice());				
				data.setLowPrice(lowPrice);
				data.setHighPrice(highPrice);
				data.setLastPrice(lastPrice);
				

			}

		}
		else {
			TradeLogger.LOGGER.severe("Get Exchange Live Data is null. Please check with administrator");
		}
		
	}
	private void placeOrderToExchange(ATOrderDetailsVO placeOrder) {
		
		
	}
	private ATOrderDetailsVO generateOrderDetails(int transactionType, int orderSubType, BigDecimal orderPrice,
			BigDecimal stopPrice,TradeDataVO data) {
		
		if(data.getProfitType() == 1) {
			
		}
		ATOrderDetailsVO orderDetailsVO = new ATOrderDetailsVO();
		orderDetailsVO.setCoin(data.getCoin());
		orderDetailsVO.setCurrency(data.getCurrency());
		orderDetailsVO.setExchange(data.getExchange());
		orderDetailsVO.setOrderPrice(orderPrice);
		orderDetailsVO.setTransactionTime(System.currentTimeMillis());
		orderDetailsVO.setOrderType(transactionType);
		orderDetailsVO.setOrderSubType(orderSubType);
		//TODO based lastBuycall /lastSellCall value
		if(data.isLastBuyCall()) {
			//TODO decimal points needs to be taken in both case
		orderDetailsVO.setQuantity(data.getTradeCurrencyVolume().divide(orderPrice));
		}
		else 
		{
			orderDetailsVO.setQuantity(data.getCoinVolume().multiply(orderPrice));
		}
		data.setOrderTriggeredPrice(orderPrice);
		data.setTriggerEvent(TraderConstants.ORDER_TRIGGER);
		return orderDetailsVO;
		
	}
	private void initializeFreshTrade(TradeDataVO data, BigDecimal lowPrice) {
		
		data.addTriggerPriceHistory(data.getTriggeredPrice());
		data.setTriggeredPrice(lowPrice);
		initializeWLHCountToZero(data);
		data.setReTriggerCount(TraderConstants.ZERO_COUNT);
		
	}
	private void initializeWLHCountToZero(TradeDataVO data) {
		data.setHighCount(TraderConstants.ZERO_COUNT);
		data.setLowCount(TraderConstants.ZERO_COUNT);
		data.setWaitCount(TraderConstants.ZERO_COUNT);
	}
	private void reinitilizeReTriggerCount(TradeDataVO data,BigDecimal price) {
		
		data.addTriggerPriceHistory(price);
		initializeWLHCountToZero(data);
		data.increaseReTriggerCount();
		
		
	}
	private MarketStaticsVO getMarketStaticsVO(Map<String, Object> values,int referenceId) {
		MarketStaticsVO staticsVO = new MarketStaticsVO();
		staticsVO.setReferenceId(referenceId);
		staticsVO.setTransactTime(System.currentTimeMillis());
		staticsVO.setHighPrice(getValidBigDecimal(values,TraderConstants.HIGH_PRICE));
		staticsVO.setLastPrice(getValidBigDecimal(values,TraderConstants.LAST_PRICE));
		staticsVO.setLowPrice(getValidBigDecimal(values,TraderConstants.LOW_PRICE));		
		return staticsVO;
		
	}
	private BigDecimal getAvgPrice(List<MarketStaticsVO> lastPriceList) {
		
		BigDecimal totalValue = TraderConstants.BIGDECIMAL_ZERO;
		for(int i=0; i<lastPriceList.size();i++){
			totalValue= totalValue.add(lastPriceList.get(i).getLastPrice());
		}
		if(lastPriceList.size()!= 0)
		{
		totalValue = totalValue.divide(new BigDecimal(lastPriceList.size()),2,RoundingMode.HALF_UP);
		}
		return totalValue;
	}
	private void reinitilizeTriggerPrice(TradeDataVO data, BigDecimal lastPrice) {		
		data.setTriggeredPrice(lastPrice);
		//Clearing all price history not necessary on  reinitilaize need fresh values
		data.getPriceHistory().clear();
		reinitilizeReTriggerCount(data,lastPrice);
		
	}
	private boolean isValidMarketData(BigDecimal value){
		boolean valid = false;
		int compare=value.compareTo(TraderConstants.NEGATIVE_ONE);
		if(compare!= TraderConstants.COMPARE_EQUAL)
		{
			valid = true;
		}
		TradeLogger.LOGGER.finest("Valid MarketData passed is valid : "+ valid);
		
		return valid;
	}
	private void initializeLastPrice(TradeDataVO data){
		
		Map<String,Object> values= getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) {			
			BigDecimal lastPrice = getValidBigDecimal(values,TraderConstants.LAST_PRICE);
			
			BigDecimal lowPrice = getValidBigDecimal(values,TraderConstants.LOW_PRICE);
			
			BigDecimal highPrice = getValidBigDecimal(values,TraderConstants.HIGH_PRICE);
			
			
			boolean buyTrigger=false;
			boolean sellTrigger = false;
			
			if(data.getTransactionType() ==TraderConstants.BUY_CALL  && isValidMarketData(lastPrice) && isValidMarketData(lowPrice)){
				int buyCompare = lastPrice.compareTo(lowPrice) ;
				//last price can be equal or more than low price , 
				//but could not be less than low price
				// so buyCompare cannot be -1, it should be either 0 or 1								
				if(buyCompare == TraderConstants.COMPARE_EQUAL){
					buyTrigger= true;
					TradeLogger.LOGGER.finest("Buy Order @Last Price : "+lastPrice);
				}
				else if(buyCompare == TraderConstants.COMPARE_GREATER){
					BigDecimal percent = getDifferInPercentage(lastPrice,lowPrice);
					buyTrigger = percentagePermissible(percent,MIN_BUY_PERMISSIBLE_PERCENT,MAX_BUY_PERMISSIBLE_PERCENT);
					TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
					if (buyTrigger) {
						TradeLogger.LOGGER.finest("Differential Buy Order @Last Price : " + lastPrice  +" with Percent "+percent);
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
					if(sellCompare == TraderConstants.COMPARE_EQUAL){
						sellTrigger=true;
						TradeLogger.LOGGER.finest("Sell Order @Last Price : " + lastPrice);
					}
					else if(sellCompare == TraderConstants.COMPARE_LOWER){
						BigDecimal percent = getDifferInPercentage(highPrice, lastPrice);
						sellTrigger = percentagePermissible(percent,MIN_SELL_PERMISSIBLE_PERCENT,MAX_SELL_PERMISSIBLE_PERCENT);
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
			data.setReTriggerCount(TraderConstants.ZERO_COUNT);
			TradeLogger.LOGGER.warning("TRIGGERED SCENARIO");
			}
			else
			{
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
		
		Map<String,Object> values = null;
		
		 /*values = new TreeMap<String, Object>();
		
		values.put(LAST_PRICE,new BigDecimal("6660.00"));
		values.put(LOW_PRICE,new BigDecimal("6650.00"));
		values.put(HIGH_PRICE,new BigDecimal("6675.00"));*/
		ArrayList<String> tradePair = new ArrayList<String>();
		String coinPair = coin+"/"+currency;
		tradePair.add(coinPair);
		GetCurrentMarketPrice get= new GetCurrentMarketPrice();
		Map<String,Map<String,Object>> exchangeValues = get.getCurrentMarketPrice(exchange,tradePair,2);
			if(exchangeValues!= null && exchangeValues.containsKey(coinPair)) 
			{
				values = exchangeValues.get(coinPair);
			}
		return values;
		
		
	}

}
