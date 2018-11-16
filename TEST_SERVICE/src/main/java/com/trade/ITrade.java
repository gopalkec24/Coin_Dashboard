package com.trade;

import com.trade.dao.ATOrderDetailsVO;

public interface ITrade {


	
	
	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails);
	
	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails);
}
