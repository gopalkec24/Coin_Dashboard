package com.trade.dao.bitso;

public class BitsoPriceStatusVO extends BitsoError {

	boolean success;
	BitsoPriceStatusPayload payload;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public BitsoPriceStatusPayload getPayload() {
		return payload;
	}
	public void setPayload(BitsoPriceStatusPayload payload) {
		this.payload = payload;
	}
}
