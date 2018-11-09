package com.trade.constants;

import java.math.BigDecimal;

public class TraderConstants {

	
	public static final int COUNTER_NOINIT = -1;
	public static final int BUY_CALL = 1;
	public static final int SELL_CALL= 2;
	public static final int INVALID_CALL = -1;
	public static final int TRIGGER_CALLED = 1;
	public static final int ZERO_COUNT=0;
	
	public static final BigDecimal NEGATIVE_ONE= new BigDecimal("-1");
	public static final BigDecimal BIGDECIMAL_ZERO= new BigDecimal("0");
	public static final int INTITAL_TRIGGER= 0;
	public static final int LIMITORDER_TRIGGER = 1;
	public static final int MARKETORDER_TRIGGER = 2;
	public static final BigDecimal HUNDRED = new BigDecimal("100");
	public static  BigDecimal MIN_PERMISSIBLE_PERCENT = BIGDECIMAL_ZERO;
	public static  BigDecimal MAX_PERMISSIBLE_PERCENT = new BigDecimal("1.5");
	
}
