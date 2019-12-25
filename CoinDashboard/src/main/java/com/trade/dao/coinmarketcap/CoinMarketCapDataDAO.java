package com.trade.dao.coinmarketcap;

import java.util.List;

public class CoinMarketCapDataDAO {
	
	public List<CMCData> data;
	public CMCStatus status;
	int statusCode ;
	String error;
	String message;
	
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public CMCStatus getStatus() {
		return status;
	}
	public void setStatus(CMCStatus status) {
		this.status = status;
	}
	public List<CMCData> getData() {
		return data;
	}
	public void setData(List<CMCData> data) {
		this.data = data;
	}
	
	

}
