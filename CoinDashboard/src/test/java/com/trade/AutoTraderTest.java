package com.trade;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;
import com.trade.dao.TradeDataVO;

public class AutoTraderTest {

	public static void main(String[] args) throws Exception 
	{
		System.out.println("Testing class");
		
		AutoTrader trader = new AutoTrader();
		
		TradeDataVO data = new TradeDataVO("BITSO", "XRP", "MXN",new BigDecimal(0),new BigDecimal(960), TraderConstants.BUY_CALL);
		System.out.println(trader.getNewTradePriceForDeletedOrder(data));
	}
	
}
