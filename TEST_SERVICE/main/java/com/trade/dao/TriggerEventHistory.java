package com.trade.dao;

public class TriggerEventHistory {

	long transactTime;
	int event;
	
	public TriggerEventHistory(int event) {
		this.event =event;
		transactTime =System.currentTimeMillis();
	}

	public long getTransactTime() {
		return transactTime;
	}

	public void setTransactTime(long transactTime) {
		this.transactTime = transactTime;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}
	
	
}
