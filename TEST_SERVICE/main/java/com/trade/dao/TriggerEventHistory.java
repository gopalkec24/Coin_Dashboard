package com.trade.dao;

public class TriggerEventHistory {

	long transactTime;
	int event;
	
	public TriggerEventHistory(int event) {
		this.event =event;
		transactTime =System.currentTimeMillis();
	}
	
	
}
