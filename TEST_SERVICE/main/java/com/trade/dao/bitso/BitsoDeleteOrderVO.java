package com.trade.dao.bitso;

import java.util.List;

public class BitsoDeleteOrderVO extends BitsoResponse{

	List<String> payload;

	public List<String> getPayload() {
		return this.payload;
	}

	public void setPayload(List<String> payload) {
		this.payload = payload;
	}
	
}
