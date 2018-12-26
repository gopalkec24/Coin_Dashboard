package com.trade.constants;

import java.math.BigDecimal;

public class TraderConstants {

	
	public static final int COUNTER_NOINIT = -1;
	
	
	public static final int INTITAL_TRIGGER= 0;
	public static final int PLACE_ORDER_TRIGGER= 1;
	
	
	public static final int BUY_CALL = 1;
	public static final int SELL_CALL= 2;
	public static final int DELETE_CALL = 3;
	public static final int GET_CALL=4;
	
	public static final int MARKET_ORDER=1;
	public static final int LIMIT_ORDER =2;
	
	public static final int INVALID_CALL = -1;
	public static final int TRIGGER_CALLED = 1;
	public static final int ZERO_COUNT=0;
	
	public static final BigDecimal NEGATIVE_ONE= new BigDecimal("-1");
	public static final BigDecimal BIGDECIMAL_ZERO= new BigDecimal("0");


	public static final BigDecimal HUNDRED = new BigDecimal("100");
	public static final int COMPARE_LOWER = -1;
	public static final int COMPARE_GREATER = 1;
	public static final int COMPARE_EQUAL = 0;
	
	public static final String LAST_PRICE = "lastPrice";
	public static final String LOW_PRICE = "lowPrice";
	public static final String HIGH_PRICE = "highPrice";


	public static final int ORDER_TRIGGERED = 2;


	
	public static  BigDecimal MIN_PERMISSIBLE_PERCENT = BIGDECIMAL_ZERO;
	public static  BigDecimal MAX_PERMISSIBLE_PERCENT = new BigDecimal("0.10");
	
	public static final int EXPIRED = 5;
	public static final int DELETED = 4;
	public static final int EXECUTED = 3;
	public static final int PARTIALLY_EXECUTED = 2;
	public static final int NEW = 1;


	public static final int DELETE_FOR_NEWTRADE = 5;


	public static final int MARK_FOR_DELETE_CREATE_NEW = 4;


	public static final int NEWTRADE_CREATED_FOR_DELETE = 6;


	public static final int TAKE_AMOUNT = 1;


	public static final int KEEP_VOLUME = 2;
	
}
