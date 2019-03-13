package com.trade.inf;

import java.util.List;

import com.trade.dao.TradeDataVO;

public interface IAutoTrader {
	
	public void processTradeList(List<TradeDataVO> listData);
	public void processData(TradeDataVO tradeVO);
	abstract void initializeLastPrice(TradeDataVO data);
	abstract void checkConditionTriggerTransaction(TradeDataVO data);
	abstract void orderToExchange(TradeDataVO data);
	abstract void checkOrderExecution(TradeDataVO data);
	abstract void cancelOrderReTriggerForNewTradeCondition(TradeDataVO data);
	abstract void createNewTradeForDelete(TradeDataVO data);
	abstract List<TradeDataVO> getNewTradeOrderList();
}
