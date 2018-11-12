package com.trade;

import com.trade.dao.ATOrderDetailsVO;

public interface ITrade {

	public void getMarketStatics();
	public void buyCoin();
	public void sellCoin();
	
	
	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails);
	
	
}
