package com.trade;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.exception.AutoTradeException;

public class BitsoTradeTest {

	public static void main(String[] args) {
		
		BitsoTrade bitSoTrade = new BitsoTrade();
		//bitSoTrade.getBalance();
		/*ATOrderDetailsVO orderDetails = new ATOrderDetailsVO();
		orderDetails.setOrderId("gcIcMYgrL3ckUOLq");
		orderDetails.setOrderType(TraderConstants.GET_CALL);
		
		bitSoTrade.getOrderStatus(orderDetails );
		System.out.println("Order ID "+orderDetails.getOrderId());
		System.out.println("Coin "+orderDetails.getCoin());
		System.out.println("Currency " +orderDetails.getCurrency());
		System.out.println("Exchange "+orderDetails.getExchange());
		System.out.println("Client Status" +orderDetails.getClientStatus());
		System.out.println("Status " +orderDetails.getStatus());
		System.out.println("Error MEssage "+ orderDetails.getErrorMsg());
		
		
		if((orderDetails.getTransactionTime()  + 86400000) < System.currentTimeMillis()) {
			System.out.println("Time reached to get out : "+ orderDetails.getTransactionTime());
			
		}*/
		
		/*//Test to Place order
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
		System.out.println("Order id placed " + placeOrderDetailsforBITSO.getOrderId());*/
		
		//Test to delete order
		/*ATOrderDetailsVO deleteOrderDetailsForBitso= new ATOrderDetailsVO();
		 
		 deleteOrderDetailsForBitso.setCoin("ETH");
		 deleteOrderDetailsForBitso.setCurrency("MXN");
		 deleteOrderDetailsForBitso.setOrderType(TraderConstants.DELETE_CALL);
		 //place and delete the order
		 	//deleteOrderDetailsForBitso.setOrderId(placeOrderDetailsforBITSO.getOrderId());
		deleteOrderDetailsForBitso.setOrderId("IWSZe1R0ett0cn9z");
		
		//bitSoTrade.deleteOrder(deleteOrderDetailsForBitso);
		 
		System.out.println(deleteOrderDetailsForBitso.getClientStatus());*/
		
		

		try {
		ATMarketStaticsVO marketstatucsVO= bitSoTrade.getExchangePriceStatics("BTC", "MXN");
		System.out.println("LAst PRice " +marketstatucsVO.getLastPrice());
		System.out.println("low price "+ marketstatucsVO.getLowPrice());
		System.out.println("high price "+ marketstatucsVO.getHighPrice());
		} catch (AutoTradeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
