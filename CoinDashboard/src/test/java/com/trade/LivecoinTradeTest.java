package com.trade;

import java.math.BigDecimal;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.exception.AutoTradeException;

public class LivecoinTradeTest {

	public static void main(String[] args) {
		LivecoinTrade trade = new LivecoinTrade();
		trade.getOrderDetails("DIG","ETH");

		//place order
		/*ATOrderDetailsVO placeOrderDetails = new ATOrderDetailsVO();
		placeOrderDetails.setExchange("LIVECOIN");
		placeOrderDetails.setCoin("DIG");
		placeOrderDetails.setCurrency("ETH");
		placeOrderDetails.setOrderType(TraderConstants.SELL_CALL);
		placeOrderDetails.setOrderSubType(TraderConstants.LIMIT_ORDER);
		placeOrderDetails.setOrderPrice(new BigDecimal("0.00005143"));
		placeOrderDetails.setQuantity(new BigDecimal("300"));
		
		trade.placeOrder(placeOrderDetails );
		
		
		System.out.println(placeOrderDetails.getOrderId());
		System.out.println(placeOrderDetails.isSuccess());*/
		
		//delete order 
		//45435679851
		//45437021951
	/*	ATOrderDetailsVO deleteOrderDetails = new ATOrderDetailsVO();
		deleteOrderDetails.setExchange("LIVECOIN");
		deleteOrderDetails.setCoin("DIG");
		deleteOrderDetails.setCurrency("ETH");
		deleteOrderDetails.setOrderType(TraderConstants.DELETE_CALL);
		deleteOrderDetails.setOrderId("45437021951");
		trade.deleteOrder(deleteOrderDetails);
		
		System.out.println(deleteOrderDetails.isSuccess());*/
		
		ATOrderDetailsVO fetchOrderDetails = new ATOrderDetailsVO();
		fetchOrderDetails.setExchange("LIVECOIN");
		fetchOrderDetails.setCoin("DIG");
		fetchOrderDetails.setCurrency("ETH");
		fetchOrderDetails.setOrderId("45440643401");
		fetchOrderDetails.setOrderType(TraderConstants.GET_CALL);
		
		trade.getOrderStatus(fetchOrderDetails);
		
		System.out.println(fetchOrderDetails.isSuccess());
		System.out.println(fetchOrderDetails.getResultCode());
		System.out.println(fetchOrderDetails.getErrorMsg());
		
		/*try {
			ATMarketStaticsVO marketstatucsVO= trade.getExchangePriceStatics("DIG", "ETH");
			System.out.println(marketstatucsVO.isExchangePrice());
			System.out.println(marketstatucsVO.isSuccess());
			System.out.println("LAst PRice " +marketstatucsVO.getLastPrice());
			System.out.println("low price "+ marketstatucsVO.getLowPrice());
			System.out.println("high price "+ marketstatucsVO.getHighPrice());
			} catch (AutoTradeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
	}

}
