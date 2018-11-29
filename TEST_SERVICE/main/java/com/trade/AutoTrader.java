package com.trade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.portfolio.utilis.GetCurrentMarketPrice;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.MarketStaticsVO;
import com.trade.dao.TradeDataVO;
import com.trade.utils.AutoTradeConfigReader;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class AutoTrader {
	
	
	
	
	private static final String MINIMUM_BUY_PERMISSIBLE_LIMT = "minimumBuyPermissibleLimt";
	private static final String MAXIMUM_BUY_PERMISSIBLE_LIMT = "maximumBuyPermissibleLimt";
	private static final String MINIMUM_SELL_PERMISSIBLE_LIMT = "minimumSellPermissibleLimt";
	private static final String MAXIMUM_SELL_PERMISSIBLE_LIMT = "maximumSellPermissibleLimt";
	public static final String STR_UNKNOWN = "UNKNOWN";
	public static final String STR_LESSER = "LESSER";
	public static final String STR_EQUAL = "EQUAL";
	public static final String STR_GREATER = "GREATER";
	//Program default values
	private static final int MAXIMUM_BUY_WAIT_COUNT = 10;
	private static final int MAXIMUM_SELL_WAIT_COUNT = 10;
	public static final int MAX_RETRIGGER_COUNT = 3;
	public static final BigDecimal MIN_SELL_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	public static final BigDecimal MAX_SELL_PERMISSIBLE_PERCENT =  new BigDecimal("1");
	public static final BigDecimal MIN_BUY_PERMISSIBLE_PERCENT = TraderConstants.BIGDECIMAL_ZERO;
	public static final BigDecimal MAX_BUY_PERMISSIBLE_PERCENT = new BigDecimal("1");
	private static final int NO_OF_PERMISSIBLE = 1;

	public static void main(String[] args){
	
		AutoTrader trader = new AutoTrader();
		//new TradeDataVO for TESTing object for Buy scenario
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		tradeVO.setPlaceAvgPriceOrder(true);
		//new TradeDataVO for Testing object for sell Scenaio
		TradeDataVO tradeVO1= new TradeDataVO("BINANCE", "ETH", "USDT", new BigDecimal("0.0125"), new BigDecimal("0.00"));
		tradeVO1.setPlaceAvgPriceOrder(true);
		/*TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"));*/
	String jsonFilePath = "C:/Documents/autotradeData.json";
		
	/*		List<TradeDataVO> list = new ArrayList<TradeDataVO>();
		list.add(tradeVO);
		list.add(tradeVO1);
		
		
		trader.process(list);
		writeAutoTradeDataToJSON(jsonFilePath,list);*/
		
		for (int i = 0; i < 1000; i++) {
			
			TradeLogger.LOGGER.info("LOOPING COUNT =============================================>"+i);
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
				Thread.sleep(600000);
				TradeLogger.LOGGER.info("Going to wake up" +new Date());
			} catch (InterruptedException e) {
				
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
		else if(data.getTriggerEvent() == TraderConstants.PLACE_ORDER_TRIGGER)
		{
			orderToExchange(data);
		}
		else if(data.getTriggerEvent() == TraderConstants.ORDER_TRIGGERED) {
			
			checkOrderExecution(data);
		}
		
		
	}
	
	private void checkOrderExecution(TradeDataVO data) {
		
		if(!TradeClient.isNullorEmpty(data.getExchangeOrderId())) 
		{
			ITrade trade = ExchangeFactory.getInstance(data.getExchange());
			if(trade!= null) 
			{
				ATOrderDetailsVO getDetailVO= generateOrderDetailsForGet(data);
				trade.getOrderStatus(generateOrderDetailsForGet(data));
				if(getDetailVO.getStatus() == TraderConstants.NEW || getDetailVO.getStatus() == TraderConstants.PARTIALLY_EXECUTED)
				{
					
				}
				//Cancelled / Expired
				else if(getDetailVO.getStatus() == TraderConstants.DELETED || getDetailVO.getStatus() == TraderConstants.EXPIRED)
				{
					
				}
				else if(getDetailVO.getStatus() == TraderConstants.EXECUTED) {
					//TODO generate the counter transaction for executed
				}
			}
		}
		
	}
	private ATOrderDetailsVO generateOrderDetailsForGet(TradeDataVO data) {
		
		ATOrderDetailsVO orderDetailsVO = new ATOrderDetailsVO();
		orderDetailsVO.setCoin(data.getCoin());
		orderDetailsVO.setCurrency(data.getCurrency());
		orderDetailsVO.setExchange(data.getExchange());
		orderDetailsVO.setOrderId(data.getExchangeOrderId());
		orderDetailsVO.setOrderType(TraderConstants.GET_CALL);
		return orderDetailsVO;
	}
	private void orderToExchange(TradeDataVO data) {
		
		ATOrderDetailsVO orderDetailsVO = generateATOrderForBSTransaction(TraderConstants.LIMIT_ORDER, data.getOrderTriggeredPrice(), null,data);
		placeOrderToExchange(orderDetailsVO);
		updateTradeData(data, orderDetailsVO);
		
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
						//TODO straergy has to be framed here to avoid long waiting time
						initializeFreshTrade(data,lowPrice);	
						data.setRemarks("Low price is less than Triggered Price , Initialize the new Trade with Current Low price");
						data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Low price is less than Triggered Price , Initialize the new Trade with Current Low price"));
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
								data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Increase in Price so increasing High count"));
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
										boolean buyTrigger = percentagePermissible(percentageDiffer, getMinBuyPermissibleLimit(), getMaxBuyPermissibleLimit());
										if(buyTrigger)
										{
											//buy order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with TriggerPrice");
											placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											data.setRemarks("Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer);
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer));
										}
										else
										{											

												if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT)
												{
													
													//reinitalize with Triggered Price Let run and test
													reinitilizeReTriggerCount(data,data.getTriggeredPrice());
													data.setRemarks("Reinitialized looping with Triggered Price in Advance Trading since ReTriggerCount is less than Maximum level.");
													data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Price in Advance Trading since ReTriggerCount is less than Maximum level."));
												}
												else 
												{
													//place buy with avg price
													if(data.isPlaceAvgPriceOrder())
													{
													TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with Avg price");
													placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
													data.setRemarks("Triggering Buy order with Avg Last Price in Advance mode Trading after reaching maximum Re- trigger count and as Placeavgprice is true and Differs in %"+ percentageDiffer);
													data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering Buy order with Avg Last Price in Advance mode Trading after reaching maximum Re- trigger count and as Placeavgprice is true and Differs in %"+ percentageDiffer));
													}
													else
													{
														//Found new target here with trigger price as avgLast Price
														initializeFreshTrade(data, avgLastPrice);
														data.setRemarks("Initializing the new Trade with Avg Last Price as it reached maximum retry count and Differs is "+percentageDiffer);
														data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Initializing the new Trade with Avg Last Price as it reached maximum retry count and Differs is "+percentageDiffer));
													}
												}
											
										}
									}
									
									else 
									{

										if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
										{											
											reinitilizeReTriggerCount(data, data.getTriggeredPrice());
											data.setRemarks("Reinitialized looping with Triggered Prices in Advance Trading since ReTriggerCount is less than Maximum level. Low price may decrease further So wait for some time");
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Prices in Advance Trading since ReTriggerCount is less than Maximum level. Low price may decrease further So wait for some time"));
										}
										else 
										{
											//place buy with triggered Price here
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											data.setRemarks("Triggering Buy order with Triggered Price in Advanced Trading after reaching maximum Re- trigger count");
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering Buy order with Triggered Price in Advanced Trading after reaching maximum Re- trigger count"));
										}

									}
								}
								//End of advance Trading
								else
								{
									//Basic Trade 
									//Buy order with triggered price here
									//Since price is moving towards downwards direction wait for maximum re-trigger count to place the order
									if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
									{											
										reinitilizeReTriggerCount(data, data.getTriggeredPrice());
										data.setRemarks("Reinitialized looping with Triggered Prices in Basic Trading since ReTriggerCount is less than Maximum level. Low price may decrease further So wait for some time");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Prices in Basic Trading since ReTriggerCount is less than Maximum level. Low price may decrease further So wait for some time"));
									}
									else 
									{
									TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
									placeOrder=generateOrderDetails(TraderConstants.BUY_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
									data.setRemarks("Triggering Buy order with Triggered Price in Basic Trading after reaching maximum Re- trigger count");
									data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering Buy order with Triggered Price in Basic Trading after reaching maximum Re- trigger count"));
									}
								}
							
								
							}
						}
						//indicating price is either moving to downwards or stay there
						else if(lastCompare == TraderConstants.COMPARE_LOWER || lastCompare == TraderConstants.COMPARE_EQUAL)
						{
							int triggerCompare = lastPrice.compareTo(data.getTriggeredPrice());
							//rare scenari
							if(triggerCompare == TraderConstants.COMPARE_LOWER) 
							{
							TradeLogger.LOGGER.finest("Fresh initailzing the triggered price with current last Price.");
							//Found New target for me.							
							initializeFreshTrade(data, lastPrice);
							data.setRemarks(" last price is lower than Re-triggered reached so initialize New Trade with Last Price");
							data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"last price is lower than Re-triggered reached so initialize New Trade with Last Price"));
							}
							else
							{
								//buy
								if(data.getWaitCount() <= MAXIMUM_BUY_WAIT_COUNT)
								{
								data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Decrease in Price so increasing Low count"));								
								data.increaseWaitCount();
								data.increaseLowCount();
								
								}
								else  
								{
									//TODO alternate options with timing condition
									//Right now reinitializing the triggeredPrice again
									//Make sure that you wait for triggered price
									//Check for alternate option 
									//data.getTriggerTime() + getPermissibleDayInMilliSecond() <= System.currentTimeMillis()
									
									if(data.getReTriggerCount() <  MAX_RETRIGGER_COUNT ) 
									{
										
										// Making old trigger price is new trade trigger price
										reinitilizeReTriggerCount(data,data.getTriggeredPrice());
										data.setRemarks("Increase Re-trigger Count and baseline count values of low,high,wait");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Increase Re-trigger Count and baseline count values of low,high,wait"));
									}
									else 
									{
										//Initital fresh trade here
										//It reaches maximum day limit
										data.setTriggerTime(System.currentTimeMillis());
										initializeFreshTrade(data,lowPrice);
										data.setRemarks("Maximum Re-triggered reached so initialize New Trade with Low Price");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Maximum Re-triggered reached so initialize New Trade with Low Price"));
									}
								}
								
							}
						}
						
					}
					
				}
				//Trade transaction Type is Sell call
				else if(data.getTransactionType() == TraderConstants.SELL_CALL && data.isLastSellCall())
				{
					
					int highCompare = -2;
					if(isValidMarketData(data.getTriggeredPrice()) && isValidMarketData(highPrice)){
						
						highCompare = highPrice.compareTo(data.getTriggeredPrice());
						
					}
					TradeLogger.LOGGER.finest("Comparing the last Triggered Price with Current High Price "+ getCompareResultType(highCompare));
					TradeLogger.LOGGER.finest("Comparing the Current last Price  with  previous last Price "+ getCompareResultType(lastCompare));
					if(highCompare == TraderConstants.COMPARE_GREATER)
					{
						//then Reinitialize Trigger Price count
						TradeLogger.LOGGER.finest("New triggered price with current High Price.");
						//Initial fresh Trade here
						//Making new Target here for me
						initializeFreshTrade(data,highPrice);
						data.setRemarks(" Recent High price is maximum than triggered Price,so initialize New Trade with High Price");
						data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size()," Recent High price is maximum than triggered Price,so initialize New Trade with High Price"));
						
					}
					else
					{
						//last price is greater or equal than Previous last price
						if(lastCompare == TraderConstants.COMPARE_GREATER || lastCompare == TraderConstants.COMPARE_EQUAL)
						{
							int triggerCompare = lastPrice.compareTo(data.getTriggeredPrice());
							//rare scenario
							if(triggerCompare == TraderConstants.COMPARE_GREATER) 
							{
								TradeLogger.LOGGER.finest("Fresh initailzing the triggered price with current last Price.");
								//Found New target for me.							
								initializeFreshTrade(data, lastPrice);
								data.setRemarks(" last price is maximum than triggered Price so initialize New Trade with Last Price");
								data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"last price is maximum than triggered Price so initialize New Trade with Last Price"));
							}
							else 
							{
								//make sure that looping wait is less than maximum looping wait count							
								if(data.getWaitCount() <= MAXIMUM_SELL_WAIT_COUNT)
								{
								data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Increase in Price so increasing High count"));
								data.increaseHigherCount();
								data.increaseWaitCount();
								
								}
								else
								{
									//TODO alternate options
									//Right now reinitializing the triggeredPrice again
									//Make sure that you wait for triggered price
									//Check for alternate option 
									//Greater 
									//&& data.getTriggerTime() + getPermissibleDayInMilliSecond() <= System.currentTimeMillis()
									if(data.getReTriggerCount() <  MAX_RETRIGGER_COUNT) 
									{
										//Initital fresh trade here
										// Making old trigger price is new trade trigger price
										reinitilizeReTriggerCount(data, data.getTriggeredPrice());
										data.setRemarks("Increase Re-trigger Count and baseline count values of low,high,wait");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Increase Re-trigger Count and baseline count values of low,high,wait"));
										
									}
									else 
									{
										//It reaches maximum day limit or maximum Re-triggered reached so initialize New Trade with high Price
										data.setTriggerTime(System.currentTimeMillis());
										initializeFreshTrade(data,highPrice);										
										data.setRemarks("Maximum Re-triggered reached so initialize New Trade with current high Price");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Maximum Re-triggered reached so initialize New Trade with high Price"));
									}
								}
							}
							
						}						
						else if(lastCompare == TraderConstants.COMPARE_LOWER)
						{
							//checking for maximum retry processing 
							if(data.getWaitCount() <= MAXIMUM_SELL_WAIT_COUNT)
							{
								data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Decrease in Price so increasing Low count"));
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

									//low count is more than high count, price is moving towards down direction very fastly
									if(data.getHighCount() < data.getLowCount())
									{									
										//List of last Price 
										List<MarketStaticsVO> lastPriceList = data.getPriceHistory();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										TradeLogger.LOGGER.finest("Differential Percentage is "+percentageDiffer);
										boolean sellTrigger = percentagePermissible(percentageDiffer, getMinSellPermissibleLimit(), getMaxSellPermissibleLimit());
										if(sellTrigger)
										{
											//sell order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											data.setRemarks("Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer);
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer));
										}
										else
										{
											//place sell with avg price or Reinitailize the count

										/*	if(data.isPlaceAvgPriceOrder()) 
											{
												//place sell with avg price
												TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
												placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
												data.setRemarks("Triggered in Advance well with Non differential Percentage as "+percentageDiffer+" and Make order data placeAvgPriceOrder as True");
											}
											else 
											{*/

												if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
												{
													reinitilizeReTriggerCount(data, data.getTriggeredPrice());
													data.setRemarks("Reinitialized looping with Triggered Price in Advance Mode since ReTriggerCount is less than Maximum level");
													data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Price in Advance Mode since ReTriggerCount is less than Maximum level"));
												}
												else 
												{
													//place sell with avg price
													//TODO Confusing that initializeFresh Trade with avgLastPrice  or ReTriggercount increase with avgLastPrice
													if(data.isPlaceAvgPriceOrder()) 
													{
													TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
													placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
													data.setRemarks("Triggering sell order with Avg Last Price in Advance mode Trading after reaching maximum Re- trigger count");
													data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering sell order with Avg Last Price in Advance mode Trading after reaching maximum Re- trigger count"));
													}
													else
													{
														initializeFreshTrade(data,avgLastPrice);	
														data.setRemarks("Initialize the new trade with avg Last price ");
														data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Initialize the new trade with avg Last price "));
													}
												}
											//}
										}
									}
									
									else 
									{
										//Since price is moving towards upwards direction wait for maximum re-trigger count to place the order
										if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
										{											
											reinitilizeReTriggerCount(data, data.getTriggeredPrice());
											data.setRemarks("Reinitialized looping with Triggered Prices in Advance Trading since ReTriggerCount is less than Maximum level. High price may increase further So wait for some time");
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Prices in Advance Trading since ReTriggerCount is less than Maximum level. High price may increase further So wait for some time"));
										}
										else 
										{
											//place buy with triggered Price here, 
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											data.setRemarks("Triggering sell order with Triggered Price in Advance mode Trading after reaching maximum Re- trigger count");
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering sell order with Triggered Price in Advance mode Trading after reaching maximum Re- trigger count"));
										}

									}
								}
								//End of advance Trading
								else
								{
									/*//Basic Trade 
									//sell order with triggered price here
									TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
									placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);*/
									
									if(data.getReTriggerCount() < MAX_RETRIGGER_COUNT) 
									{											
										reinitilizeReTriggerCount(data, data.getTriggeredPrice());
										data.setRemarks("Reinitialized looping with Triggered Prices in Basic Trading since ReTriggerCount is less than Maximum level");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Reinitialized looping with Triggered Prices in Basic Trading since ReTriggerCount is less than Maximum level"));
									}
									else 
									{
										//place buy with triggered Price here
										TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
										placeOrder=generateOrderDetails(TraderConstants.SELL_CALL,TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
										data.setRemarks("Triggering sell order with Triggered Price in Basic Trading after reaching maximum Re- trigger count");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering sell order with Triggered Price in Basic Trading after reaching maximum Re- trigger count"));
									}
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
				
				if(data.getTriggerEvent() == TraderConstants.PLACE_ORDER_TRIGGER && placeOrder!= null) {
					placeOrderToExchange(placeOrder);
					updateTradeData(data, placeOrder);
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
	private void updateTradeData(TradeDataVO data, ATOrderDetailsVO placeOrder) {
		if(placeOrder.isSuccess() && !TradeClient.isNullorEmpty(placeOrder.getOrderId())) {
			TradeLogger.LOGGER.info("Order placed to Exchange was successful");
			data.setExchangeOrderId(placeOrder.getOrderId());
			data.setTriggerEvent(TraderConstants.ORDER_TRIGGERED);
			data.setRemarks("Order placed to Exchange was successful");
		}
		else if(placeOrder.isSuccess() && TradeClient.isNullorEmpty(placeOrder.getOrderId()))
		{
			data.setRemarks("Invalid Scenario . Please contact Administrator for more info");
		}
		else if (!placeOrder.isSuccess())
		{
			data.setRemarks(placeOrder.getErrorMsg());
		}
		else {
			data.setRemarks("Invalid Scenario . Please contact Administrator for more info");
		}
	}
	private long getPermissibleDayInMilliSecond() {
		
		//86400 seconds in day
		long permissibleMS = NO_OF_PERMISSIBLE * 86400000;
		return permissibleMS;
	}
	private void placeOrderToExchange(ATOrderDetailsVO placeOrder) 
	{
		ITrade trade = ExchangeFactory.getInstance(placeOrder.getExchange());
		
		if(trade!= null)
		{
			trade.placeOrder(placeOrder);
		}
		else
		{
			TradeLogger.LOGGER.severe("NO AUTOTRADE  EXCHANGE TRADE IMPLEMENTATION FOUND");
		}
		
	}
	private ATOrderDetailsVO generateOrderDetails(int transactionType, int orderSubType, BigDecimal orderPrice,
			BigDecimal stopPrice,TradeDataVO data)
	{
		
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
		orderDetailsVO.setQuantity(data.getTradeCurrencyVolume().divide(orderPrice,8,RoundingMode.HALF_UP));
		}
		else 
		{
			orderDetailsVO.setQuantity(data.getCoinVolume());
		}
		data.setOrderTriggeredPrice(orderPrice);
		data.setTriggerEvent(TraderConstants.PLACE_ORDER_TRIGGER);
		return orderDetailsVO;
		
	}
	
	private ATOrderDetailsVO generateOrderDetails(int orderSubType, BigDecimal orderPrice,
			BigDecimal stopPrice,TradeDataVO data)
	{
		
		if(data.getProfitType() == 1) {
			
		}
		ATOrderDetailsVO orderDetailsVO = generateATOrderForBSTransaction(orderSubType, orderPrice,stopPrice, data);
		data.setOrderTriggeredPrice(orderPrice);
		data.setTriggerEvent(TraderConstants.PLACE_ORDER_TRIGGER);
		return orderDetailsVO;
		
	}
	private ATOrderDetailsVO generateATOrderForBSTransaction(int orderSubType, BigDecimal orderPrice,BigDecimal stopPrice, TradeDataVO data) {
		ATOrderDetailsVO orderDetailsVO = new ATOrderDetailsVO();
		orderDetailsVO.setCoin(data.getCoin());
		orderDetailsVO.setCurrency(data.getCurrency());
		orderDetailsVO.setExchange(data.getExchange());
		orderDetailsVO.setOrderPrice(orderPrice);
		orderDetailsVO.setTransactionTime(System.currentTimeMillis());
		
		orderDetailsVO.setOrderSubType(orderSubType);
		if(stopPrice!= null ) {
			orderDetailsVO.setStopPrice(stopPrice);
		}
		//TODO based lastBuycall /lastSellCall value
		if(data.isLastBuyCall()) {
			//TODO decimal points needs to be taken in both case
		orderDetailsVO.setQuantity(data.getTradeCurrencyVolume().divide(orderPrice,8,RoundingMode.HALF_UP));
		orderDetailsVO.setOrderType(TraderConstants.BUY_CALL);
		}
		else 
		{
			orderDetailsVO.setOrderType(TraderConstants.SELL_CALL);
			orderDetailsVO.setQuantity(data.getCoinVolume());
		}
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
	private MarketStaticsVO getMarketStaticsVO(Map<String, Object> values,int referenceId,String remarks) {
		MarketStaticsVO staticsVO = new MarketStaticsVO();
		staticsVO.setReferenceId(referenceId);
		staticsVO.setTransactTime(System.currentTimeMillis());
		staticsVO.setHighPrice(getValidBigDecimal(values,TraderConstants.HIGH_PRICE));
		staticsVO.setLastPrice(getValidBigDecimal(values,TraderConstants.LAST_PRICE));
		staticsVO.setLowPrice(getValidBigDecimal(values,TraderConstants.LOW_PRICE));		
		staticsVO.setRemarks(remarks);
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
	
	public boolean isValidMarketData(BigDecimal value){
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
			BigDecimal percent  = null;
			if(data.getTransactionType() ==TraderConstants.BUY_CALL  && isValidMarketData(lastPrice) && isValidMarketData(lowPrice)){
				int buyCompare = lastPrice.compareTo(lowPrice) ;
				//last price can be equal or more than low price , 
				//but could not be less than low price
				// so buyCompare cannot be -1, it should be either 0 or 1								
				if(buyCompare == TraderConstants.COMPARE_EQUAL){
					buyTrigger= true;
					TradeLogger.LOGGER.finest("Buy Order @Last Price : "+lastPrice);
					data.setRemarks("Triggered Event to buy");
				}
				else if(buyCompare == TraderConstants.COMPARE_GREATER)
				{
					percent = getDifferInPercentage(lastPrice,lowPrice);
					buyTrigger = percentagePermissible(percent,getMinBuyPermissibleLimit(),getMaxBuyPermissibleLimit());
					TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
					if (buyTrigger) {
						TradeLogger.LOGGER.finest("Differential Buy Order @Last Price : " + lastPrice  +" with Percent "+percent);
						data.setRemarks("Triggered Event to sell with Differential price percentage");
					}
					
				}
				else{
					TradeLogger.LOGGER.warning("Invalid Scenario Raised in buy compare ");
					data.setRemarks("Invalid Scenario Raised in Buy compare");
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
						data.setRemarks("Triggered Event to sell");
					}
					else if(sellCompare == TraderConstants.COMPARE_LOWER){
						percent = getDifferInPercentage(highPrice, lastPrice);
						sellTrigger = percentagePermissible(percent,getMinSellPermissibleLimit(),getMaxSellPermissibleLimit());
						TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
						if (sellTrigger) {
							TradeLogger.LOGGER.finest("Differential Sell Order @Last Price : " + lastPrice +" with Percent"+percent);
							data.setRemarks("Triggered Event to sell with Differential price percentage");
						}
						
					}
					else{
						TradeLogger.LOGGER.warning("Invalid Scenario Raised in sell compare ");
						data.setRemarks("Invalid Scenario Raised in sell compare");
					}
				
				}
			data.setLastPrice(lastPrice);
			data.setLowPrice(lowPrice);
			data.setHighPrice(highPrice);
			if(buyTrigger || sellTrigger){
			
			
			data.setLastBuyCall(buyTrigger);
			data.setLastSellCall(sellTrigger);
			data.setTriggeredPrice(lastPrice);
			
			data.setTriggerTime(System.currentTimeMillis());
			data.setAtOrderId(System.currentTimeMillis()+"");
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
				data.setRemarks("No Scenario TRIGGERED since Percentage difference is "+ percent);
			}
			
			
		}
		
	}

	private BigDecimal getMaxSellPermissibleLimit() {
		try {
			
			if(AutoTradeConfigReader.getConfigValues().containsKey(MAXIMUM_SELL_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Maximum Sell Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_SELL_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Maximum Sell Permissible Limit " + MAX_SELL_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return MAX_SELL_PERMISSIBLE_PERCENT;
	}
	private BigDecimal getMinSellPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MINIMUM_SELL_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Minimum Sell Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MINIMUM_SELL_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Minimum Sell Permissible Limit " + MIN_SELL_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MIN_SELL_PERMISSIBLE_PERCENT;
	}
	
	private BigDecimal getMaxBuyPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MAXIMUM_BUY_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Maximum Buy Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MAXIMUM_BUY_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Maximum buy Permissible Limit " + MAX_BUY_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MAX_BUY_PERMISSIBLE_PERCENT;
	}
	private BigDecimal getMinBuyPermissibleLimit() {
		try {
			if(AutoTradeConfigReader.getConfigValues().containsKey(MINIMUM_BUY_PERMISSIBLE_LIMT)) {
				TradeLogger.LOGGER.finest("Using  Minimun Buy Permissible Limit from properties file ");
				return	AutoTradeUtilities.getBigDecimalValue(AutoTradeConfigReader.getConfigValues().get(MINIMUM_BUY_PERMISSIBLE_LIMT));
			}
			else {
				TradeLogger.LOGGER.finest("Using default Minimum buy Permissible Limit " + MIN_BUY_PERMISSIBLE_PERCENT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MIN_BUY_PERMISSIBLE_PERCENT;
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
