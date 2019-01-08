package com.trade;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.io.FileUtils;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.utils.TradeLogger;

public class BinanceTradeTest {

	public static void main(String[] args) {
		
		BinanceTrade trade = new BinanceTrade();
		//Test to place the order
		ATOrderDetailsVO placeOrderDetails = new ATOrderDetailsVO();
		placeOrderDetails.setCoin("BTC");		
		placeOrderDetails.setCurrency("USDT");
		placeOrderDetails.setExchange("BINANCE");
		BigDecimal orderPrice = new BigDecimal("5250.05");
		placeOrderDetails.setOrderPrice(orderPrice);
		placeOrderDetails.setOrderType(TraderConstants.BUY_CALL);
		placeOrderDetails.setOrderSubType(TraderConstants.LIMIT_ORDER);
		BigDecimal quantity = new BigDecimal("10.1").divide(orderPrice,8,RoundingMode.HALF_UP);
		System.out.println("Quantitu " + quantity);		
		placeOrderDetails.setQuantity(quantity);
		System.out.println(quantity.toPlainString());
		
		//trade.placeOrder(placeOrderDetails );
		System.out.println("Is order placed "+ placeOrderDetails.isSuccess());
		System.out.println("Result Coede : "+ placeOrderDetails.getResultCode());
		System.out.println("ERror code :"+placeOrderDetails.getErrorMsg());
		 System.out.println("ORder id "+ placeOrderDetails.getOrderId());
		 System.out.println("ORder status "+ placeOrderDetails.getClientStatus());
		 
		 //Test to delete the order
	ATOrderDetailsVO deleteOrderDetails= new ATOrderDetailsVO();
		 
		 deleteOrderDetails.setCoin("ETH");
		 deleteOrderDetails.setCurrency("USDT");
		 deleteOrderDetails.setOrderType(TraderConstants.DELETE_CALL);
		// deleteOrderDetails.setOrderId(placeOrderDetails.getOrderId());
		// deleteOrderDetails.setOrderId("134500060");
		// trade.deleteOrder(deleteOrderDetails);
		 System.out.println("Is order placed "+ deleteOrderDetails.isSuccess());
			System.out.println("Result Coede : "+ deleteOrderDetails.getResultCode());
			System.out.println("ERror code :"+deleteOrderDetails.getErrorMsg());
			 System.out.println("ORder id "+ deleteOrderDetails.getOrderId());
			 System.out.println("ORder status "+ deleteOrderDetails.getClientStatus());
			 
		 //110607222
			 
			 ATOrderDetailsVO getOrderDetailsVO= new ATOrderDetailsVO();
			 
			 getOrderDetailsVO.setCoin("ETH");
			 getOrderDetailsVO.setCurrency("USDT");
			 getOrderDetailsVO.setOrderId("110607222");
			 getOrderDetailsVO.setOrderType(TraderConstants.GET_CALL);
			
			// trade.getOrderStatus(getOrderDetailsVO);
			 
			 System.out.println("Is order placed "+ getOrderDetailsVO.isSuccess());
				System.out.println("Result Coede : "+ getOrderDetailsVO.getResultCode());
				System.out.println("ERror code :"+getOrderDetailsVO.getErrorMsg());
				 System.out.println("ORder id "+ getOrderDetailsVO.getOrderId());
				 System.out.println("ORder status "+ getOrderDetailsVO.getClientStatus());
				 System.out.println("AT order status "+ getOrderDetailsVO.getStatus());
			 
				 String jsonFilePath="C:/Documents/binance_openOrder.json";
				try {
					FileUtils.write(new File(jsonFilePath),trade.getOrderDetails(null));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				trade.getOrderDetails(null);
			//Test to get the open order only 
			// trade.getOrderDetails("ETHUSDT");
			 
		//Test to get all order details for given symbol	 
		//TradeLogger.LOGGER.severe(trade.getAllOrderDetails("ETHUSDT"));

	}

}
