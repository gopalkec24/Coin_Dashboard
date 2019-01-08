package com.trade.dao.bitso;

import java.util.List;

public class BitsoOrderResponse extends BitsoResponse{

	List<BitsoPayload> payload;

	public List<BitsoPayload> getPayload() {
		return payload;
	}

	public void setPayload(List<BitsoPayload> payload) {
		this.payload = payload;
	}
	
	
	
}
