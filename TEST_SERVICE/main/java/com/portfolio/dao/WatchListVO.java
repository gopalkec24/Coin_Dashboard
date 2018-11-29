package com.portfolio.dao;

import java.util.List;

public class WatchListVO {
	
	String exchangeName;
	List<String> watchlist;
	
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public List<String> getWatchlist() {
		return watchlist;
	}
	public void setWatchlist(List<String> watchlist) {
		this.watchlist = watchlist;
	}
	

}
