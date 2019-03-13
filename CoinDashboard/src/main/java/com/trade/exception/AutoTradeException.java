package com.trade.exception;

public class AutoTradeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AutoTradeException(String msg,Throwable e) {
		super(msg,e);
	}

	public AutoTradeException(Throwable e) {
		super(e);		
	}
	
	public AutoTradeException(String msg) {
		super(msg);		
	}
	
	public AutoTradeException() {
		super();
	}
}
