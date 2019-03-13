package com.trade.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.trade.ExchangeFactory;
import com.trade.ITrade;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.MarketStaticsVO;
import com.trade.dao.TradeDataVO;
import com.trade.inf.IAutoTrader;
import com.trade.utils.AutoTradeConfigReader;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class AdvanceTrader extends Trader implements IAutoTrader {

	private static final int MAXIMUM_BUY_WAIT_COUNT = 10;
	private static final int MAXIMUM_SELL_WAIT_COUNT = 10;
	
	
	public void processData(TradeDataVO data) {
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
		else if(data.getTriggerEvent() == TraderConstants.MARK_FOR_DELETE_CREATE_NEW) {
			cancelOrderReTriggerForNewTradeCondition(data);
		}
		else if(data.getTriggerEvent() == TraderConstants.DELETE_FOR_NEWTRADE) {
			createNewTradeForDelete(data);
		}
		
	}

	public void initializeLastPrice(TradeDataVO data) {
		Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) {			
			BigDecimal lastPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LAST_PRICE);
			
			BigDecimal lowPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LOW_PRICE);
			
			BigDecimal highPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.HIGH_PRICE);
			
			
			boolean buyTrigger=false;
			boolean sellTrigger = false;
			BigDecimal percent  = null;
			if(data.getTransactionType() ==TraderConstants.BUY_CALL  && AutoTradeUtilities.isValidMarketData(lastPrice) && AutoTradeUtilities.isValidMarketData(lowPrice)){
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
					percent = AutoTradeUtilities.getDifferInPercentage(lastPrice,lowPrice);
					buyTrigger = AutoTradeUtilities.percentagePermissible(percent,AutoTradeConfigReader.getMinBuyPermissibleLimit(),AutoTradeConfigReader.getMaxBuyPermissibleLimit());
					TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
					if (buyTrigger) {
						TradeLogger.LOGGER.finest("Differential Buy Order @Last Price : " + lastPrice  +" with Percent "+percent);
						data.setRemarks("Differential Buy Order @Last Price : " + lastPrice  +" with Percent "+percent);
					}
					
				}
				else{
					TradeLogger.LOGGER.warning("Invalid Scenario Raised in buy compare ");
					data.setRemarks("Invalid Scenario Raised in Buy compare");
				}
			}
			else if(data.getTransactionType()==TraderConstants.SELL_CALL && AutoTradeUtilities.isValidMarketData(lastPrice) && AutoTradeUtilities.isValidMarketData(highPrice)){
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
						percent = AutoTradeUtilities.getDifferInPercentage(highPrice, lastPrice);
						sellTrigger = AutoTradeUtilities.percentagePermissible(percent,AutoTradeConfigReader.getMinSellPermissibleLimit(),AutoTradeConfigReader.getMaxSellPermissibleLimit());
						TradeLogger.LOGGER.finest("Differential Percentage is "+percent);
						if (sellTrigger) {
							TradeLogger.LOGGER.finest("Differential Sell Order @Last Price : " + lastPrice +" with Percent"+percent);
							data.setRemarks("Differential Sell Order @Last Price : " + lastPrice +" with Percent"+percent);
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
			data.setTriggerEventForHistory(TraderConstants.INTITAL_TRIGGER);
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

	public void checkConditionTriggerTransaction(TradeDataVO data) {

		Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		if (values != null) 
		{	
			BigDecimal lastPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LAST_PRICE);

			BigDecimal lowPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LOW_PRICE);

			BigDecimal highPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.HIGH_PRICE);
			
			ATOrderDetailsVO placeOrder = null;
			if(AutoTradeUtilities.isValidMarketData(data.getLastPrice()) && AutoTradeUtilities.isValidMarketData(lastPrice))
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
					if(AutoTradeUtilities.isValidMarketData(data.getTriggeredPrice()) && AutoTradeUtilities.isValidMarketData(lowPrice))
					{
						lowCompare = data.getTriggeredPrice().compareTo(lowPrice);
						TradeLogger.LOGGER.finest("Comparing the last Triggered Price with Current Low Price "+ AutoTradeUtilities.getCompareResultType(lowCompare));
					}
					TradeLogger.LOGGER.finest("Comparing the Current last Price  with  previous last Price "+ AutoTradeUtilities.getCompareResultType(lastCompare));
					//low price is lower than triggered Price
					if(lowCompare == TraderConstants.COMPARE_GREATER)
					{
						
						//then Reinitialize Trigger Price count
						TradeLogger.LOGGER.finest("New triggered price with current low Price.");
						//Initial fresh Trade here
						//Making new Target here for me
						//TODO statergy has to be framed here to avoid long waiting time
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
								if(data.isAdvanceTrade() || AutoTradeConfigReader.isAdvanceTradeEnabled())
								{
									//higher count is more than Low count, price is moving towards up direction very fastly
									//better to trigger the buy order here based on percentage differs 									
									if(data.getLowCount() < data.getHighCount())
									{									
										//List of last Price 
										List<MarketStaticsVO> lastPriceList = data.getPriceHistory();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = AutoTradeUtilities.getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										TradeLogger.LOGGER.finest("Differential Percentage is "+percentageDiffer);
										boolean buyTrigger = AutoTradeUtilities.percentagePermissible(percentageDiffer, AutoTradeConfigReader.getMinBuyPermissibleLimit(), AutoTradeConfigReader.getMaxBuyPermissibleLimit());
										if(buyTrigger)
										{
											//buy order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone with TriggerPrice");
											placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
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
													placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
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
											placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
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
									placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
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
					if(AutoTradeUtilities.isValidMarketData(data.getTriggeredPrice()) && AutoTradeUtilities.isValidMarketData(highPrice)){
						
						highCompare = highPrice.compareTo(data.getTriggeredPrice());
						
					}
					TradeLogger.LOGGER.finest("Comparing the last Triggered Price with Current High Price "+ AutoTradeUtilities.getCompareResultType(highCompare));
					TradeLogger.LOGGER.finest("Comparing the Current last Price  with  previous last Price "+ AutoTradeUtilities.getCompareResultType(lastCompare));
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
						//indicating that Price is increasing here
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
						//Indicating that Price is decreasing
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
								if(data.isAdvanceTrade() || AutoTradeConfigReader.isAdvanceTradeEnabled())
								{

									//low count is more than high count, price is moving towards down direction very fastly
									if(data.getHighCount() < data.getLowCount())
									{									
										//List of last Price 
										List<MarketStaticsVO> lastPriceList = data.getPriceHistory();
										//take the average the last price
										BigDecimal avgLastPrice = getAvgPrice(lastPriceList);
										BigDecimal percentageDiffer = AutoTradeUtilities.getDifferInPercentage(avgLastPrice, data.getTriggeredPrice());
										TradeLogger.LOGGER.finest("Differential Percentage is "+percentageDiffer);
										boolean sellTrigger = AutoTradeUtilities.percentagePermissible(percentageDiffer, AutoTradeConfigReader.getMinSellPermissibleLimit(),AutoTradeConfigReader.getMaxSellPermissibleLimit());
										if(sellTrigger)
										{
											//sell order with trigger price
											TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
											placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
											data.setRemarks("Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer);
											data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggered order with Triggered Price in Advance well with differential Percentage as "+percentageDiffer));
										}
										else
										{
											//place sell with avg price or Reinitailize the count

										

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
													placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,avgLastPrice,null,data);
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
											placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
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
										//place Sell order with triggered Price here
										TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
										placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
										data.setRemarks("Triggering sell order with Triggered Price in Basic Trading after reaching maximum Re- trigger count");
										data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Triggering sell order with Triggered Price in Basic Trading after reaching maximum Re- trigger count"));
									}
								}
								
								
							}

						}
						
					}
					
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
			else {
				data.setPreviousLastPrice(lastPrice);				
				data.setLowPrice(lowPrice);
				data.setHighPrice(highPrice);
				data.setLastPrice(lastPrice);
			}

		}
		else {
			TradeLogger.LOGGER.severe("Get Exchange Live Data is null. Please check with administrator");
		}
		
	
		
	}

	public void orderToExchange(TradeDataVO data) {
		
		ATOrderDetailsVO orderDetailsVO = generateATOrderForBSTransaction(TraderConstants.LIMIT_ORDER, data.getOrderTriggeredPrice(), null,data);
		placeOrderToExchange(orderDetailsVO);
		updateTradeData(data, orderDetailsVO);	
		
	}

	public void checkOrderExecution(TradeDataVO data) {

		
		   try{
			//check whether exchange order id is available 										  
			if(!TradeClient.isNullorEmpty(data.getExchangeOrderId())) 
			{
				ITrade trade = ExchangeFactory.getInstance(data.getExchange());
				if(trade!= null) 
				{
					ATOrderDetailsVO getDetailVO= generateOrderDetailsForGet(data);
					trade.getOrderStatus(getDetailVO);
					//incase order is new or partially executed
					if(getDetailVO.getStatus() == TraderConstants.NEW || getDetailVO.getStatus() == TraderConstants.PARTIALLY_EXECUTED)
					{
						TradeLogger.LOGGER.info("Order placed time "+new Date(getDetailVO.getTransactionTime())+""+ getDetailVO.getTransactionTime());
						TradeLogger.LOGGER.info("Client Status " + getDetailVO.getClientStatus());
						Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
						
						if(data.isAdvanceTrade() || AutoTradeConfigReader.isAdvanceTradeEnabled()) 
						{
							
							if (values != null) 
							{	
								BigDecimal lastPrice = AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LAST_PRICE);										
								BigDecimal percentage= AutoTradeUtilities.getDifferInPercentage(lastPrice, data.getOrderTriggeredPrice());
								TradeLogger.LOGGER.finest("Percentage value before Absoulte :"+percentage);
								percentage = percentage.abs();
								TradeLogger.LOGGER.finest("Percentage value After Absoulte :"+percentage);
								boolean cancelOrder =AutoTradeUtilities.percentagePermissible(percentage, AutoTradeConfigReader.getMinCancelLimit(), AutoTradeConfigReader.getMaxCancelLimit());
								if(cancelOrder) {
									try
									{
										cancelOrderReTriggerForNewTradeCondition(data,getNewTradePriceForDeletedOrder(data,values));
									} 
									catch (Exception e)
									{
										TradeLogger.LOGGER.log(Level.SEVERE, "Error in caluculating amount order please contact the Administrator ", e);
									}
								}
								else 
								{
									TradeLogger.LOGGER.info("Waiting for More times " );
								}
								
							}
							else 
							{
								TradeLogger.LOGGER.severe("FAILED to fetch the values from Exchange : " + data.getExchange());
							}
						}
						else
						{
							
							//updatedCacheTime + defaultCacheTimeoutInMS < System.currentTimeMillis()
							if((getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut()) < System.currentTimeMillis()) {
								TradeLogger.LOGGER.info("Making  order to delete Since  "+ (getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut() )+ " milliseconds . Current Time"+System.currentTimeMillis());
								data.setTriggerEventForHistory(TraderConstants.MARK_FOR_DELETE_CREATE_NEW);
								cancelOrderReTriggerForNewTradeCondition(data);
								
							}
							else
							{
								TradeLogger.LOGGER.info("Waiting for order to execute  for   "+ (getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut() )+ " milliseconds . Current Time"+System.currentTimeMillis());
								
							}
						}
						data.addPriceHistory(getMarketStaticsVO(values,data.getPriceHistory().size(),"Checking the order to be executed"));
						
					}
					//Cancelled / Expired
					else if(getDetailVO.getStatus() == TraderConstants.DELETED || getDetailVO.getStatus() == TraderConstants.EXPIRED)
					{
						data.setTriggerEventForHistory(TraderConstants.DELETED);
					}
					else if(getDetailVO.getStatus() == TraderConstants.EXECUTED)
					{
						
							BigDecimal quantity = TraderConstants.BIGDECIMAL_ZERO;
							BigDecimal tradeCurrency = TraderConstants.BIGDECIMAL_ZERO;					
							
							if(data.getProfitType() == TraderConstants.TAKE_AMOUNT)
							{
								quantity = getDetailVO.getExecutedQuantity();
							}
							else if(data.getProfitType() == TraderConstants.KEEP_VOLUME)
							{
								tradeCurrency = getDetailVO.getExecutedQuantity().multiply(getDetailVO.getOrderPrice());
							}
							TradeDataVO newTradeData = new TradeDataVO(data.getExchange(), data.getCoin(), data.getCurrency(), quantity, tradeCurrency,AutoTradeUtilities.reverseTransaction(data.getTransactionType()));
							newOrderList.add(newTradeData);
							data.setTriggerEventForHistory(TraderConstants.COMPLETED);
					}
				}
			}
			} catch (Exception e) {
				
				TradeLogger.LOGGER.severe("FAILED to fetch the values from Exchange : " + data.getExchange());
			}
			
		
		
	}

	

	public void cancelOrderReTriggerForNewTradeCondition(TradeDataVO data) {		
		
		try 
		{
			cancelOrderReTriggerForNewTradeCondition(data, getNewTradePriceForDeletedOrder(data));
		}
		catch (Exception e) 
		{
			TradeLogger.LOGGER.log(Level.SEVERE, "Error in caluculating amount order please contact the Administrator ", e);
		}
		
		
	
		
	}

	
	protected BigDecimal getNewTradePriceForDeletedOrder(TradeDataVO data) throws Exception {		
		Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
		return getNewTradePriceForDeletedOrder(data, values);
		
	}
	public void createNewTradeForDelete(TradeDataVO data) {

		try {
			createNewTradeForDelete(data,getNewTradePriceForDeletedOrder(data));
		} catch (Exception e) {
			TradeLogger.LOGGER.log(Level.SEVERE, "Error in caluculating amount order please contact the Administrator ", e);
		}


	
		
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
	
	
	

	public void processTradeList(List<TradeDataVO> listData) {
		
		
	}

	public List<TradeDataVO> getNewTradeOrderList() {
		return newOrderList;
		
	}
	
	
}
