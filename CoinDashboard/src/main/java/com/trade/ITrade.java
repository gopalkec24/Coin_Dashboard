package com.trade;

import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.exception.AutoTradeException;

public interface ITrade {

	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails);
	
	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails);
	
	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails);
	
	public String getSymbolForExchange(String coin,String currency);
	
	 public ATMarketStaticsVO getExchangePriceStatics(String symbol,String currency) throws AutoTradeException ; 
}
