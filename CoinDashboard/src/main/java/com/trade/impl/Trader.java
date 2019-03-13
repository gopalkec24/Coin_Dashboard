package com.trade.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import com.portfolio.utilis.GetCurrentMarketPrice;
import com.trade.ExchangeFactory;
import com.trade.ITrade;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.MarketStaticsVO;
import com.trade.dao.TradeDataVO;
import com.trade.utils.AutoTradeConfigReader;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class Trader {
	
	public static  int MAX_RETRIGGER_COUNT = 3;
	public  List<TradeDataVO> newOrderList = new CopyOnWriteArrayList<TradeDataVO>();
	public void initializeFreshTrade(TradeDataVO data, BigDecimal lowPrice) {
		
		data.addTriggerPriceHistory(data.getTriggeredPrice());
		data.setTriggeredPrice(lowPrice);
		initializeWLHCountToZero(data);
		data.setReTriggerCount(TraderConstants.ZERO_COUNT);
		
	}
	public void initializeWLHCountToZero(TradeDataVO data) {
		data.setHighCount(TraderConstants.ZERO_COUNT);
		data.setLowCount(TraderConstants.ZERO_COUNT);
		data.setWaitCount(TraderConstants.ZERO_COUNT);
	}
	public void reinitilizeReTriggerCount(TradeDataVO data,BigDecimal price) {
		
		data.addTriggerPriceHistory(price);
		initializeWLHCountToZero(data);
		data.increaseReTriggerCount();
		
		
	}
	public MarketStaticsVO getMarketStaticsVO(Map<String, Object> values,int referenceId,String remarks) {
		MarketStaticsVO staticsVO = new MarketStaticsVO();
		staticsVO.setReferenceId(referenceId);
		staticsVO.setTransactTime(System.currentTimeMillis());
		staticsVO.setHighPrice(AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.HIGH_PRICE));
		staticsVO.setLastPrice(AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LAST_PRICE));
		staticsVO.setLowPrice(AutoTradeUtilities.getValidBigDecimal(values,TraderConstants.LOW_PRICE));		
		staticsVO.setRemarks(remarks);
		return staticsVO;
		
	}
	
	public MarketStaticsVO getMarketStaticsVO(ATMarketStaticsVO marketstaticsVO,int referenceId,String remarks) {
		MarketStaticsVO staticsVO = new MarketStaticsVO();
		staticsVO.setReferenceId(referenceId);
		staticsVO.setTransactTime(System.currentTimeMillis());
		staticsVO.setHighPrice(marketstaticsVO.getHighPrice());
		staticsVO.setLastPrice(marketstaticsVO.getLastPrice());
		staticsVO.setLowPrice(marketstaticsVO.getLowPrice());		
		staticsVO.setRemarks(remarks);
		return staticsVO;
		
	}
	
	
	public ATOrderDetailsVO generateOrderDetails(int orderSubType, BigDecimal orderPrice,
			BigDecimal stopPrice,TradeDataVO data)
	{
		
		ATOrderDetailsVO orderDetailsVO = generateATOrderForBSTransaction(orderSubType,orderPrice,stopPrice,data);
		data.setOrderTriggeredPrice(orderPrice);
		data.setTriggerEventForHistory(TraderConstants.PLACE_ORDER_TRIGGER);
		return orderDetailsVO;
		
	}
	
	
	public ATOrderDetailsVO generateATOrderForBSTransaction(int orderSubType, BigDecimal orderPrice,BigDecimal stopPrice, TradeDataVO data) {
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
		
		if(data.getTransactionType() == TraderConstants.BUY_CALL) {
			//TODO decimal points needs to be taken in both case
		if(AutoTradeUtilities.isZero(data.getTradeCurrencyVolume())) {	
		orderDetailsVO.setQuantity(data.getCoinVolume());
		}
		else
		{
		orderDetailsVO.setQuantity(data.getTradeCurrencyVolume().divide(orderPrice,8,RoundingMode.HALF_UP));
		}
		orderDetailsVO.setOrderType(TraderConstants.BUY_CALL);
		}
		else if(data.getTransactionType() == TraderConstants.SELL_CALL)
		{
			orderDetailsVO.setOrderType(TraderConstants.SELL_CALL);
			orderDetailsVO.setQuantity(data.getCoinVolume());
		}
		else {
			TradeLogger.LOGGER.severe("Unknown Transaction Type Encounted here : "+ data.getTransactionType());
		}
		return orderDetailsVO;
	}
	
	public void placeOrderToExchange(ATOrderDetailsVO placeOrder) 
	{
		ITrade trade = ExchangeFactory.getInstance(placeOrder.getExchange());
		
		if(trade!= null && AutoTradeConfigReader.isPlaceTradeEnabled())
		{
			trade.placeOrder(placeOrder);
		}
		else
		{
			TradeLogger.LOGGER.severe("NO AUTOTRADE  EXCHANGE TRADE IMPLEMENTATION FOUND");
		}
		
	}
	
	
	public void updateTradeData(TradeDataVO data, ATOrderDetailsVO placeOrder) {
		if(placeOrder.isSuccess() && !TradeClient.isNullorEmpty(placeOrder.getOrderId())) {
			TradeLogger.LOGGER.info("Order placed to Exchange was successful");
			data.setExchangeOrderId(placeOrder.getOrderId());
			data.setTriggerEventForHistory(TraderConstants.ORDER_TRIGGERED);
			data.setOrderTriggeredPrice(placeOrder.getOrderPrice());
			data.setRemarks("Order placed to Exchange was successful");
		}
		else if(placeOrder.isSuccess() && TradeClient.isNullorEmpty(placeOrder.getOrderId()))
		{
			data.setRemarks("Invalid Scenario . Please contact Administrator for more info");
		}
		else if (!placeOrder.isSuccess())
		{
			data.setRemarks(placeOrder.getErrorMsg());
			data.setTriggerEvent(TraderConstants.ERROR_CALL);
		}
		else {
			data.setRemarks("Invalid Scenario . Please contact Administrator for more info");
		}
	}
	
	public boolean cancelOrder(TradeDataVO data,int nextTriggerEvent) {
		ITrade trade = ExchangeFactory.getInstance(data.getExchange());
		if(trade != null)
		{
		ATOrderDetailsVO deleteOrderDetails;
		try {
			deleteOrderDetails = generateOrderToDelete(data);
			//Place deleteOrder to exchange
			trade.deleteOrder(deleteOrderDetails);
			if (deleteOrderDetails.isSuccess()) {
				data.setTriggerEventForHistory(nextTriggerEvent);
				return true;
			} 
		} 
		catch (Exception e) {
			TradeLogger.LOGGER.log(Level.SEVERE, "Error in Cancelling order please contact the Administrator ", e);
			return false;
		}
		
		}
		return false;
	}
	
	public ATOrderDetailsVO generateOrderToDelete(TradeDataVO data) {
		ATOrderDetailsVO deleteOrderDetails  = new ATOrderDetailsVO();
		deleteOrderDetails.setExchange(data.getExchange());
		deleteOrderDetails.setOrderId(data.getExchangeOrderId());
		deleteOrderDetails.setCoin(data.getCoin());
		deleteOrderDetails.setCurrency(data.getCurrency());
		deleteOrderDetails.setOrderType(TraderConstants.DELETE_CALL);
		return deleteOrderDetails;
	}
	
	public TradeDataVO createNewTradeForDelete(TradeDataVO data,BigDecimal orderPrice) {
		TradeDataVO tradeData = new TradeDataVO(data.getExchange(), data.getCoin(), data.getCurrency(), data.getCoinVolume(), data.getTradeCurrencyVolume(),data.getTransactionType());
		tradeData.setTriggerEventForHistory(TraderConstants.INTITAL_TRIGGER);
		tradeData.setProfitType(data.getProfitType());
		tradeData.setLastBuyCall(data.isLastBuyCall());
		tradeData.setLastSellCall(data.isLastSellCall());
		tradeData.setRemarks("New order created from old ID "+ data.getAtOrderId());
		tradeData.setTriggeredPrice(orderPrice);
		tradeData.setPlaceAvgPriceOrder(data.isPlaceAvgPriceOrder());
		tradeData.setAtOrderId(System.currentTimeMillis()+"");
		initializeWLHCountToZero(tradeData);
		tradeData.setReTriggerCount(0);
		newOrderList.add(tradeData);
		data.setTriggerEventForHistory(TraderConstants.NEWTRADE_CREATED_FOR_DELETE);
		data.setRemarks("New counter order was created for this order");
		return tradeData;
	}
	
public ATOrderDetailsVO generateOrderDetailsForGet(TradeDataVO data) {
		
		ATOrderDetailsVO orderDetailsVO = new ATOrderDetailsVO();
		orderDetailsVO.setCoin(data.getCoin());
		orderDetailsVO.setCurrency(data.getCurrency());
		orderDetailsVO.setExchange(data.getExchange());
		orderDetailsVO.setOrderId(data.getExchangeOrderId());
		orderDetailsVO.setOrderType(TraderConstants.GET_CALL);
		return orderDetailsVO;
	}

public BigDecimal getNewTradePriceForDeletedOrder(TradeDataVO data, Map<String, Object> values) throws Exception {
	if(values!= null) {
		BigDecimal basePrice = null;
		if(data.getTransactionType() == TraderConstants.BUY_CALL) {
			basePrice = AutoTradeUtilities.getValidBigDecimal(values, TraderConstants.LOW_PRICE);
			TradeLogger.LOGGER.finest("base Amt in buy call : " +basePrice);
			BigDecimal amount = AutoTradeUtilities.calcuateAmountByLimit(AutoTradeConfigReader.getMaxBuyPermissibleLimit(),basePrice);
			TradeLogger.LOGGER.finest("Calculated Amt in buy fcall : " +amount);
			return basePrice.subtract(amount);
		}
		else if(data.getTransactionType() == TraderConstants.SELL_CALL) {
			basePrice = AutoTradeUtilities.getValidBigDecimal(values, TraderConstants.HIGH_PRICE);
			TradeLogger.LOGGER.info("base Amt in Sell call : " +basePrice);
			BigDecimal amount = AutoTradeUtilities.calcuateAmountByLimit(AutoTradeConfigReader.getMaxSellPermissibleLimit(),basePrice);
			TradeLogger.LOGGER.info("Calculated Amt in sell call" +amount);
			return basePrice.add(amount);
		}
		else {
			throw new Exception("INVALID TRANSACTION TYPE . SHOULD BE EITHER BUY OR SELL");
		}
	}
	else
	{
		throw new Exception("INVALID MARKET DATA");
	}
}


public BigDecimal getNewTradePriceForDeletedOrder(TradeDataVO data, ATMarketStaticsVO values) throws Exception {
	if(values!= null) {
		BigDecimal basePrice = null;
		if(data.getTransactionType() == TraderConstants.BUY_CALL) {
			basePrice = values.getLowPrice();
			TradeLogger.LOGGER.finest("base Amt in buy call : " +basePrice);
			BigDecimal amount = AutoTradeUtilities.calcuateAmountByLimit(AutoTradeConfigReader.getMaxBuyPermissibleLimit(),basePrice);
			TradeLogger.LOGGER.finest("Calculated Amt in buy fcall : " +amount);
			return basePrice.subtract(amount);
		}
		else if(data.getTransactionType() == TraderConstants.SELL_CALL) {
			basePrice = values.getHighPrice();
			TradeLogger.LOGGER.info("base Amt in Sell call : " +basePrice);
			BigDecimal amount = AutoTradeUtilities.calcuateAmountByLimit(AutoTradeConfigReader.getMaxSellPermissibleLimit(),basePrice);
			TradeLogger.LOGGER.info("Calculated Amt in sell call" +amount);
			return basePrice.add(amount);
		}
		else {
			throw new Exception("INVALID TRANSACTION TYPE . SHOULD BE EITHER BUY OR SELL");
		}
	}
	else
	{
		throw new Exception("INVALID MARKET DATA");
	}
}
public void cancelOrderReTriggerForNewTradeCondition(TradeDataVO data, BigDecimal price) {
	boolean cancelOrder =  cancelOrder(data,TraderConstants.DELETE_FOR_NEWTRADE);
	if(cancelOrder) {
		createNewTradeForDelete(data,price);
	}
	
}

protected BigDecimal getNewTradePriceForDeletedOrder(TradeDataVO data) throws Exception {		
	Map<String,Object> values= AutoTradeUtilities.getExchangePrice(data.getExchange(),data.getCoin(),data.getCurrency());
	return getNewTradePriceForDeletedOrder(data, values);
	
}

protected BigDecimal getNewTradePriceForDeletedOrder2(TradeDataVO data) throws Exception {		
	ITrade trade = ExchangeFactory.getInstance(data.getExchange());
	ATMarketStaticsVO marketStaticsVO = trade.getExchangePriceStatics(data.getCoin(), data.getCurrency());
	return getNewTradePriceForDeletedOrder(data, marketStaticsVO);
	
}
}
