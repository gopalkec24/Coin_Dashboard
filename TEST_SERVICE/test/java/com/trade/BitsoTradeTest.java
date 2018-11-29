package com.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;

public class BitsoTradeTest {

	public static void main(String[] args) {
		
		BitsoTrade bitSoTrade = new BitsoTrade();
		ATOrderDetailsVO orderDetails = new ATOrderDetailsVO();
		orderDetails.setOrderId("IWSZe1R0ett0cn9z");
		orderDetails.setOrderType(TraderConstants.GET_CALL);
		
		bitSoTrade.getOrderStatus(orderDetails );
		
		//Test to Place order
		ATOrderDetailsVO placeOrderDetailsforBITSO = new ATOrderDetailsVO();
		placeOrderDetailsforBITSO.setCoin("ETH");		
		placeOrderDetailsforBITSO.setCurrency("MXN");
		placeOrderDetailsforBITSO.setExchange("BITSO");
		BigDecimal orderPrice = new BigDecimal("2800.00");
		placeOrderDetailsforBITSO.setOrderPrice(orderPrice);
		placeOrderDetailsforBITSO.setOrderType(TraderConstants.SELL_CALL);
		placeOrderDetailsforBITSO.setOrderSubType(TraderConstants.LIMIT_ORDER);
		BigDecimal quantity = new BigDecimal("1000.1").divide(orderPrice,8,RoundingMode.HALF_UP);
		System.out.println("Quantitu " + quantity);		
		placeOrderDetailsforBITSO.setQuantity(quantity);
		System.out.println(quantity.toPlainString());
		
	//	bitSoTrade.placeOrder(placeOrderDetailsforBITSO );
		
		// sample order Id : IWSZe1R0ett0cn9z
		System.out.println("Order id placed " + placeOrderDetailsforBITSO.getOrderId());
		
		//Test to delete order
		ATOrderDetailsVO deleteOrderDetailsForBitso= new ATOrderDetailsVO();
		 
		 deleteOrderDetailsForBitso.setCoin("ETH");
		 deleteOrderDetailsForBitso.setCurrency("MXN");
		 deleteOrderDetailsForBitso.setOrderType(TraderConstants.DELETE_CALL);
		 //place and delete the order
		 	//deleteOrderDetailsForBitso.setOrderId(placeOrderDetailsforBITSO.getOrderId());
		deleteOrderDetailsForBitso.setOrderId("IWSZe1R0ett0cn9z");
		
		//bitSoTrade.deleteOrder(deleteOrderDetailsForBitso);
		 
		System.out.println(deleteOrderDetailsForBitso.getClientStatus());

	}

}
