package com.trade.auto;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;
import com.trade.dao.TradeDataVO;
import com.trade.utils.TradeLogger;

public class ProfitAnalyser {

	public static final int TAKE_AMOUNT = 1;
	public static final int KEEP_VOLUME = 2;

	public static void main(String[] args)
	{
		TradeDataVO tradeVO= new TradeDataVO("BINANCE", "BTC", "USDT", new BigDecimal("0.00"), new BigDecimal("100.00"),TraderConstants.BUY_CALL);
		getQuantityValue(tradeVO);
	}

	private static void getQuantityValue(TradeDataVO tradeVO)
	{
		BigDecimal quantity ;
		if(tradeVO.getProfitType() == TAKE_AMOUNT) 
		{
			BigDecimal orderExecuted  = new BigDecimal("0.001");
			BigDecimal orderPrice = new BigDecimal("4200");
			
			quantity = orderExecuted;
			
		}
		else if(tradeVO.getProfitType() == KEEP_VOLUME){
			
			
			
		}
		else
		{
			TradeLogger.LOGGER.severe("In valid profit Type");
		}
		
	}
	
}
