package com.trade.dao.bitso;

public class BitsoResponse {
	
	boolean success;
	
	BitsoError error;
	
	public BitsoError getError() {
		return error;
	}
	public void setError(BitsoError error) {
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	

}
