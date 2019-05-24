package com.trade.dao.bitso;

public class BitsoPriceStatusPayload {
	
	String book;
	String volume;
	String high;
	String low;
	String vwap;
	String ask;
	String bid;
	String created_at;
	String last;
	String change_24;
	
	
	public String getChange_24() {
		return change_24;
	}
	public void setChange_24(String change_24) {
		this.change_24 = change_24;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getVwap() {
		return vwap;
	}
	public void setVwap(String vwap) {
		this.vwap = vwap;
	}
	public String getAsk() {
		return ask;
	}
	public void setAsk(String ask) {
		this.ask = ask;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}
