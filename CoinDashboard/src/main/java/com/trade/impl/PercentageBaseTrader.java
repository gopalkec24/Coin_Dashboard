package com.trade.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.trade.ExchangeFactory;
import com.trade.ITrade;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.TradeDataVO;
import com.trade.dao.TriggerEventHistory;
import com.trade.exception.AutoTradeException;
import com.trade.inf.IAutoTrader;
import com.trade.notification.NotificationSender;
import com.trade.utils.AutoTradeConfigReader;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class PercentageBaseTrader extends Trader implements IAutoTrader {

	private static final String CLASS_NAME = PercentageBaseTrader.class.getName();
	private static  List<String> recipients = new ArrayList<String>();
	BigDecimal mIN_PERMISSIBLE_PERCENT= new BigDecimal("1.2");
	BigDecimal mAX_PERMISSIBLE_PERCENT = new BigDecimal("1000");
	
	public static void main(String[] args) {
		

	}

	public void processTradeList(List<TradeDataVO> listData) {
		
		
	}

	public void processData(TradeDataVO data)
	{

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
		else if(data.getTriggerEvent() == TraderConstants.NEWTRADE_CREATED_FOR_DELETE) {
			data.setRemarks("Order was deleted and new order was created for same");
		}
		else if(data.getTriggerEvent() == TraderConstants.COMPLETED) {
			data.setRemarks("Order Got completed ");
		}
	}

	public void initializeLastPrice(TradeDataVO data) 
	{

		ITrade trade = ExchangeFactory.getInstance(data.getExchange());
		boolean buyTrigger=false;
		boolean sellTrigger = false;
		BigDecimal lastPrice = TraderConstants.BIGDECIMAL_ZERO;
		try
		{
			TradeLogger.LOGGER.finest("Start to get the price from Exchange ");
			//Get the price from Exchange
			ATMarketStaticsVO marketStaticsVO = trade.getExchangePriceStatics(data.getCoin(), data.getCurrency());
			TradeLogger.LOGGER.finest("End to get the price from Exchange ");
			
			if (marketStaticsVO.isSuccess()) 
			{
				TradeLogger.LOGGER.finest("Get the value from Exchange was successful");
				lastPrice = marketStaticsVO.getLastPrice();
			} 
			else
			{
				//check the current price from CoinMarketCap site 
				throw new AutoTradeException(marketStaticsVO.getErrorMsg());
			} 
			StringBuffer remarks=new StringBuffer();
		
				if(data.getTransactionType() == TraderConstants.BUY_CALL) 
				{
					// Transaction is buy call
						//Initializing the buy order trigger here
						if(AutoTradeUtilities.isValidMarketData(data.getBasePrice()) ) 
						{
							int compareBasePrice = lastPrice.compareTo(data.getBasePrice());
							if(compareBasePrice == TraderConstants.COMPARE_LOWER) 
							{
								BigDecimal percent = AutoTradeUtilities.getDifferInPercentage(data.getBasePrice(),lastPrice);
								buyTrigger= AutoTradeUtilities.percentagePermissible(percent,(data.getMinPercentage().compareTo(TraderConstants.NEGATIVE_ONE))== TraderConstants.COMPARE_EQUAL?mIN_PERMISSIBLE_PERCENT:data.getMinPercentage(), (data.getMaxPercentage().compareTo(TraderConstants.NEGATIVE_ONE))== TraderConstants.COMPARE_EQUAL?mAX_PERMISSIBLE_PERCENT:data.getMaxPercentage());
								TradeLogger.LOGGER.fine("last price is lower than Base price. and Price differ Percentage " + percent +" Buy trigger value is "+buyTrigger);
								remarks.append("last price is lower than Base price. and Price differ Percentage " + percent +" Buy trigger value is "+buyTrigger);
							}
							else 
							{
								TradeLogger.LOGGER.fine("last price is Greater than Base price,So not Triggering Buy trigger");
								remarks.append("last Price is Greater than Base Price");
							}
						}
						else
						{
							
							TradeLogger.LOGGER.fine(" Base price is not valid price");
							remarks.append(" Base price is not valid price. Kindly set the base price in Trade Object configuration");
						}
				}
				else if(data.getTransactionType() == TraderConstants.SELL_CALL) 
				{
						if(AutoTradeUtilities.isValidMarketData(data.getBasePrice()) ) 
						{
							int compareBasePrice = lastPrice.compareTo(data.getBasePrice());
							if(compareBasePrice == TraderConstants.COMPARE_GREATER) 
							{
								BigDecimal percent = AutoTradeUtilities.getDifferInPercentage(lastPrice, data.getBasePrice());
								sellTrigger=AutoTradeUtilities.percentagePermissible(percent,(data.getMinPercentage().compareTo(TraderConstants.NEGATIVE_ONE))== TraderConstants.COMPARE_EQUAL?mIN_PERMISSIBLE_PERCENT:data.getMinPercentage(), (data.getMaxPercentage().compareTo(TraderConstants.NEGATIVE_ONE))== TraderConstants.COMPARE_EQUAL?mAX_PERMISSIBLE_PERCENT:data.getMaxPercentage());
								TradeLogger.LOGGER.fine("last price is Greater than Base price and Price differ Percentage " + percent +" sell trigger value is "+sellTrigger);
								//TradeLogger.LOGGER.finest("Under PP is Greater .last price is Greater than Base price. so Triggering sell trigger");
								remarks.append("Last price is Greater than Base price and Price differ Percentage " + percent +" sell trigger value is "+sellTrigger);
							}
							else 
							{
								TradeLogger.LOGGER.fine("last price is lesser than Base price");
								remarks.append("Last price is Lesser than Base price ");
							}
						}
						else
						{
							TradeLogger.LOGGER.fine(" Base price is not valid price");
							remarks.append(" Base price is not valid price. Kindly set the base price in Trade Object configuration");
						}
				}
			data.setLastPrice(lastPrice);
			data.setLowPrice(marketStaticsVO.getLowPrice());
			data.setHighPrice(marketStaticsVO.getHighPrice());
			
			
			if(buyTrigger || sellTrigger)
			{
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
			data.addPriceHistory(getMarketStaticsVO(marketStaticsVO, data.getPriceHistory().size(), remarks.toString()+"Triggered Scenario"));
			TradeLogger.LOGGER.warning("TRIGGERED SCENARIO");
			data.setRemarks(remarks.toString()+" Scenario TRIGGERED");
			}
			else
			{
				TradeLogger.LOGGER.warning("NO Scenario TRIGGERED ");
				data.setRemarks(remarks.toString()+" No Scenario TRIGGERED ");
			}
			
		} catch (AutoTradeException e) {
			TradeLogger.LOGGER.logp(Level.SEVERE, CLASS_NAME,"initializeLastPrice" , "Failed to get the Exchange price from server",e);
		}



	}

	public void checkConditionTriggerTransaction(TradeDataVO data) 
	{
		ITrade trade = ExchangeFactory.getInstance(data.getExchange());
		//get the price from exchange 
		ATOrderDetailsVO placeOrder = null;
			try 
			{
				ATMarketStaticsVO marketStaticsVO = trade.getExchangePriceStatics(data.getCoin(), data.getCurrency());
				
				if(marketStaticsVO.isSuccess()) 
				{
					
					if(data.getWaitCount() > AutoTradeConfigReader.getWaitCount()) 
					{
						
						if(data.getTransactionType() == TraderConstants.BUY_CALL && data.getLowCount() > data.getHighCount()) {
							data.setWaitCount(0);
							data.setLowCount(0);
							data.setHighCount(0);
						}
						else if(data.getTransactionType() == TraderConstants.SELL_CALL && data.getHighCount() > data.getLowCount()) {
							data.setWaitCount(0);
							data.setLowCount(0);
							data.setHighCount(0);
						}
						else 
						{
							// place order with triggered price
						TradeLogger.LOGGER.info("Condition Met to transact changeing the state alone");
						checkAndChangeTriggerPrice(data, marketStaticsVO);
						placeOrder=generateOrderDetails(TraderConstants.LIMIT_ORDER,data.getTriggeredPrice(),null,data);
						data.setRemarks("Triggered order with Triggered Price as "+data.getTriggeredPrice());
						data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(),"Triggered order with Triggered Price as "+data.getTriggeredPrice()));
						}
					}
					else 
					{
						checkAndChangeTriggerPrice(data, marketStaticsVO);

					}
					if(data.getTriggerEvent() == TraderConstants.PLACE_ORDER_TRIGGER && placeOrder!= null) 
					{
						placeOrderToExchange(placeOrder);
						updateTradeData(data, placeOrder);
					}
					data.setPreviousLastPrice(data.getLastPrice());				
					data.setLowPrice(marketStaticsVO.getLowPrice());
					data.setHighPrice(marketStaticsVO.getHighPrice());
					data.setLastPrice(marketStaticsVO.getLastPrice());

				}
			} 
			catch (AutoTradeException e) 
			{
				TradeLogger.LOGGER.logp(Level.SEVERE, CLASS_NAME,"checkConditionTriggerTransaction" , "Failed to get the Exchange price from server",e);
			}
		
		
		
	}

	private boolean checkAndChangeTriggerPrice(TradeDataVO data, ATMarketStaticsVO marketStaticsVO) 
	{
		boolean placeOrder = false;
		int compareLastCurrent =marketStaticsVO.getLastPrice().compareTo(data.getTriggeredPrice());
		if(data.getTransactionType() == TraderConstants.BUY_CALL)
		{

			if(compareLastCurrent == TraderConstants.COMPARE_LOWER)
			{
				TradeLogger.LOGGER.finest("Updating the triggered Price as "+ marketStaticsVO.getLastPrice());
				//change the trigger price to current last price
				data.addTriggerPriceHistory(data.getTriggeredPrice());
				data.setTriggeredPrice(marketStaticsVO.getLastPrice());
				//increase the lower count and overall count here
				data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(),"Updating the triggered Price as "+ marketStaticsVO.getLastPrice() + " . Resetting the count to 0"));
				/*data.increaseLowCount();
				data.increaseWaitCount();*/
				
				data.setWaitCount(0);
				data.setLowCount(0);
				data.setHighCount(0);
				
			}
			else if(compareLastCurrent == TraderConstants.COMPARE_GREATER || compareLastCurrent ==TraderConstants.COMPARE_EQUAL)
			{
				//increase the High count and overall count here
				TradeLogger.LOGGER.finest("Increasing the wait count with higher Count , adding history as well");
				data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(),"Increase in Price so increasing High count"));
				data.increaseHigherCount();
				data.increaseWaitCount();
			}


		}
		else if(data.getTransactionType() == TraderConstants.SELL_CALL) 
		{
			if(compareLastCurrent == TraderConstants.COMPARE_LOWER || compareLastCurrent ==TraderConstants.COMPARE_EQUAL)
			{

				//increase the lower count and overall count here
				data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(),"Decrease in Price so increasing Low count"));
				data.increaseLowCount();
				data.increaseWaitCount();
			}
			else if(compareLastCurrent == TraderConstants.COMPARE_GREATER )
			{
				TradeLogger.LOGGER.finest("Updating the triggered Price also here");
				//change the trigger price to current last price
				data.addTriggerPriceHistory(data.getTriggeredPrice());
				data.setTriggeredPrice(marketStaticsVO.getLastPrice());
				//increase the High count and overall count here
				TradeLogger.LOGGER.finest("Updating the triggered Price as "+ marketStaticsVO.getLastPrice()+"Increase in Price so increasing High count");
				data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(), "Updating the triggered Price as "+ marketStaticsVO.getLastPrice()+" .  Resetting the count to 0"));
				/*data.increaseHigherCount();
				data.increaseWaitCount();*/
				data.setWaitCount(0);
				data.setLowCount(0);
				data.setHighCount(0);
			}
		}
		return placeOrder;
	}

	@SuppressWarnings("unchecked")
	public void orderToExchange(TradeDataVO data) {
		
		ITrade trade = ExchangeFactory.getInstance(data.getExchange());
		ATMarketStaticsVO marketStaticsVO;
		try {
			marketStaticsVO = trade.getExchangePriceStatics(data.getCoin(), data.getCurrency());
			checkAndChangeTriggerPrice(data, marketStaticsVO);
			ATOrderDetailsVO orderDetailsVO = generateATOrderForBSTransaction(TraderConstants.LIMIT_ORDER, data.getOrderTriggeredPrice(), null,data);
			placeOrderToExchange(orderDetailsVO);
			updateTradeData(data, orderDetailsVO);
			recipients= (List<String>) AutoTradeConfigReader.getNotifier();
			if(recipients.size() >0)
			NotificationSender.sendNotificationMessage("Order placed : "+ data.getExchange() + "|"+data.getCoin()+"|"+data.getCurrency()+"|"+data.getTransactionType()+"|"+data.getOrderTriggeredPrice()+"|"+data.getCoinVolume()+"|"+data.getTradeCurrencyVolume(), recipients);
		} catch (AutoTradeException e) {
			TradeLogger.LOGGER.logp(Level.SEVERE, CLASS_NAME,"orderToExchange" ,"FAILED to fetch the values from Exchange : " + data.getExchange(),e);
		}
		
		
	}

	public void checkOrderExecution(TradeDataVO data)
	{
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
						TradeLogger.LOGGER.info("Order placed time "+new Date(getDetailVO.getTransactionTime())+" "+ getDetailVO.getTransactionTime());
						TradeLogger.LOGGER.info("Client Status " + getDetailVO.getClientStatus());
						
						//Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
							ATMarketStaticsVO marketStaticsVO = trade.getExchangePriceStatics(data.getCoin(), data.getCurrency());
							//updatedCacheTime + defaultCacheTimeoutInMS < System.currentTimeMillis()
							if(getDetailVO.getTransactionTime()!=-1 && (getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut()) < System.currentTimeMillis()) 
							{
								TradeLogger.LOGGER.info("Making  order to delete Since  "+ (getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut() )+ " milliseconds . Current Time"+System.currentTimeMillis());
								data.setTriggerEventForHistory(TraderConstants.MARK_FOR_DELETE_CREATE_NEW);
								data.setRemarks("Marking order to delete ");
								cancelOrderReTriggerForNewTradeCondition(data);
								recipients= (List<String>) AutoTradeConfigReader.getNotifier();
								if(recipients.size() >0)
								NotificationSender.sendNotificationMessage("Order Cancelled due to timeout : "+ data.getExchange() + "|"+data.getCoin()+"|"+data.getCurrency()+"|"+data.getTransactionType()+"|"+data.getOrderTriggeredPrice()+"|"+data.getCoinVolume()+"|"+data.getTradeCurrencyVolume(), recipients);
								
							}
							else if(getDetailVO.getTransactionTime()==-1) {
								for(TriggerEventHistory event : data.getEventHistory()) 
								{
									if(event!= null && event.getEvent() == TraderConstants.ORDER_TRIGGERED &&  event.getBeforeEvent() ==TraderConstants.PLACE_ORDER_TRIGGER) 
									{
										if(event.getTransactTime() + AutoTradeConfigReader.getTransactionTimeOut()  < System.currentTimeMillis())
										{
											TradeLogger.LOGGER.info("Making  order to delete Since  "+ event.getTransactTime() + AutoTradeConfigReader.getTransactionTimeOut() + " milliseconds . Current Time"+System.currentTimeMillis());
											data.setTriggerEventForHistory(TraderConstants.MARK_FOR_DELETE_CREATE_NEW);
											data.setRemarks("Marking order to delete ");
											cancelOrderReTriggerForNewTradeCondition(data);
											recipients= (List<String>) AutoTradeConfigReader.getNotifier();
											if(recipients.size() >0)
											NotificationSender.sendNotificationMessage("Order Cancelled due to timeout : "+ data.getExchange() + "|"+data.getCoin()+"|"+data.getCurrency()+"|"+data.getTransactionType()+"|"+data.getOrderTriggeredPrice()+"|"+data.getCoinVolume()+"|"+data.getTradeCurrencyVolume(), recipients);
										}
										else {
											TradeLogger.LOGGER.info("Waiting for order to execute in event hisstory scenario for   "+ event.getTransactTime() + AutoTradeConfigReader.getTransactionTimeOut()+ " milliseconds . Current Time"+System.currentTimeMillis());
										}
									}
								}
							}
							else 
							{
								TradeLogger.LOGGER.info("Waiting for order to execute  for   "+ (getDetailVO.getTransactionTime() + AutoTradeConfigReader.getTransactionTimeOut() )+ " milliseconds . Current Time"+System.currentTimeMillis());
								
								
							}
						
						data.addPriceHistory(getMarketStaticsVO(marketStaticsVO,data.getPriceHistory().size(),"Checking the order to be executed"));
						
					}
					//Cancelled / Expired
					else if(getDetailVO.getStatus() == TraderConstants.DELETED || getDetailVO.getStatus() == TraderConstants.EXPIRED)
					{
						data.setTriggerEventForHistory(TraderConstants.DELETED);
						data.setRemarks("Order got Deleted");
					}
					else if(getDetailVO.getStatus() == TraderConstants.EXECUTED)
					{
						
							BigDecimal quantity = TraderConstants.BIGDECIMAL_ZERO;
							BigDecimal tradeCurrency = TraderConstants.BIGDECIMAL_ZERO;					
							//5 coin sold/bought , 5 coin is bought/sold
							if(data.getProfitType() == TraderConstants.TAKE_AMOUNT)
							{
								quantity = getDetailVO.getExecutedQuantity();
							}
							else if(data.getProfitType() == TraderConstants.KEEP_VOLUME)
							{
								//Calculate the total transaction volume , we could buy/sold the coin based on
								//total transactin volume
								tradeCurrency = getDetailVO.getExecutedQuantity().multiply(getDetailVO.getOrderPrice());
							}
							TradeDataVO newTradeData = new TradeDataVO(data.getExchange(), data.getCoin(), data.getCurrency(), quantity, tradeCurrency,AutoTradeUtilities.reverseTransaction(data.getTransactionType()));
							newTradeData.setBasePrice(getDetailVO.getOrderPrice());
							newTradeData.setProfitType(data.getProfitType());
							newTradeData.setMinPercentage(data.getMinPercentage());
							newTradeData.setMaxPercentage(data.getMaxPercentage());
							TradeLogger.LOGGER.info("New Trade Data" + newTradeData);
							newOrderList.add(newTradeData);
							data.setTriggerEventForHistory(TraderConstants.COMPLETED);
							data.setRemarks("Order Got completed. Counter Order created");
							recipients= (List<String>) AutoTradeConfigReader.getNotifier();
							if(recipients.size() >0)
							NotificationSender.sendNotificationMessage("Order Got completed. Counter Order created"+ data.getExchange() + "|"+data.getCoin()+"|"+data.getCurrency()+"|"+data.getTransactionType()+"|"+data.getOrderTriggeredPrice()+"|"+data.getCoinVolume()+"|"+data.getTradeCurrencyVolume(), recipients);
					}
				}
			}
			}
		   catch (Exception e) {
				
				TradeLogger.LOGGER.log(Level.SEVERE,"FAILED to fetch the values from Exchange : " + data.getExchange(),e);
			}
			
		
		
	
		
	}

	public void cancelOrderReTriggerForNewTradeCondition(TradeDataVO data) {
		try {
			boolean cancelOrder =  cancelOrder(data,TraderConstants.DELETE_FOR_NEWTRADE);
			if(cancelOrder) {
				createNewTradeForDelete(data,getNewTradePriceForDeletedOrder2(data));
			}
			
		} catch (Exception e) {
			TradeLogger.LOGGER.log(Level.SEVERE, "Error in calculating amount order please contact the Administrator ", e);
		}
		
	}

	public void createNewTradeForDelete(TradeDataVO data) {
		try {
			createNewTradeForDelete(data,getNewTradePriceForDeletedOrder(data));
		} catch (Exception e) {
			TradeLogger.LOGGER.log(Level.SEVERE, "Error in calculating amount order please contact the Administrator ", e);
		}
		
	}

	public List<TradeDataVO> getNewTradeOrderList() {
		
		return newOrderList;
	}

}
