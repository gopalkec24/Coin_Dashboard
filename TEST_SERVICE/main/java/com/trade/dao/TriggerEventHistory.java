package com.trade.dao;

public class TriggerEventHistory {

	long transactTime;
	int event;
	int beforeEvent;
	
	public int getBeforeEvent() {
		return beforeEvent;
	}

	public void setBeforeEvent(int beforeEvent) {
		this.beforeEvent = beforeEvent;
	}

	public TriggerEventHistory(int event, int previousEvent) {
		this.event =event;
		this.beforeEvent = previousEvent;
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
