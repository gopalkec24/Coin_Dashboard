package com.trade.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Base64;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;

import com.trade.BinanceTrade;
import com.trade.BitsoTrade;
import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.binance.BinanceExchangeInfo;
import com.trade.dao.binance.SymbolDetails;
import com.trade.utils.TradeClient;

public class ReadConfigTest {
	private static final String PROXY_AUTH_USERNAME = "hcltech\natarajan_g";
	private static final String PROXY_AUTH_PASSWORD = "@54";
	public static void main(String[] args) {
		
		
		
	BinanceTrade trade = new BinanceTrade();
	BitsoTrade bitSoTrade = new BitsoTrade();
	ATOrderDetailsVO orderDetails = new ATOrderDetailsVO();
	
	//trade.getOrderDetails("STORMETH");
	
	ATOrderDetailsVO placeOrderDetailsforBITSO = new ATOrderDetailsVO();
	/*placeOrderDetailsforBITSO.setCoin("ETH");		
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
	
	bitSoTrade.placeOrder(placeOrderDetailsforBITSO );*/
	//IWSZe1R0ett0cn9z
	System.out.println("Order id placed " + placeOrderDetailsforBITSO.getOrderId());
	
	/*ATOrderDetailsVO deleteOrderDetailsForBitso= new ATOrderDetailsVO();
	 
	 deleteOrderDetailsForBitso.setCoin("ETH");
	 deleteOrderDetailsForBitso.setCurrency("MXN");
	 deleteOrderDetailsForBitso.setOrderType(TraderConstants.DELETE_CALL);
//	deleteOrderDetailsForBitso.setOrderId(placeOrderDetailsforBITSO.getOrderId());
	deleteOrderDetailsForBitso.setOrderId("IWSZe1R0ett0cn9z");
	bitSoTrade.deleteOrder(deleteOrderDetailsForBitso);
	 
	System.out.println(deleteOrderDetailsForBitso.getClientStatus());
	 */
	 
		//trade.getAllOrderDetails("XZCBNB");
	/*	ATOrderDetailsVO placeOrderDetails = new ATOrderDetailsVO();
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
		
		trade.placeOrder(placeOrderDetails );
		System.out.println("Is order placed "+ placeOrderDetails.isSuccess());
		System.out.println("Result Coede : "+ placeOrderDetails.getResultCode());
		System.out.println("ERror code :"+placeOrderDetails.getErrorMsg());
		 System.out.println("ORder id "+ placeOrderDetails.getOrderId());
		 System.out.println("ORder status "+ placeOrderDetails.getClientStatus());*/
		/* ATOrderDetailsVO deleteOrderDetails= new ATOrderDetailsVO();
		 
		 deleteOrderDetails.setCoin("ETH");
		 deleteOrderDetails.setCurrency("USDT");
		 deleteOrderDetails.setOrderType(TraderConstants.DELETE_CALL);
		// deleteOrderDetails.setOrderId(placeOrderDetails.getOrderId());
		 deleteOrderDetails.setOrderId("134500060");
		 trade.deleteOrder(deleteOrderDetails);
		 System.out.println("Is order placed "+ deleteOrderDetails.isSuccess());
			System.out.println("Result Coede : "+ deleteOrderDetails.getResultCode());
			System.out.println("ERror code :"+deleteOrderDetails.getErrorMsg());
			 System.out.println("ORder id "+ deleteOrderDetails.getOrderId());
			 System.out.println("ORder status "+ deleteOrderDetails.getClientStatus());
			 
		 
			 
			 trade.getOrderDetails("ETHUSDT");*/
	//trade.getExchangeInfo();
		
		
		/*
		WebTarget target = TradeClient.getAdvancedClient("https://www.google.co.in", true);
		//Response res = target.register(new BasicAuthentication(PROXY_AUTH_USERNAME, PROXY_AUTH_PASSWORD)).request().get();
		String encoded = new String(Base64.getEncoder()
				.encode((new String(PROXY_AUTH_USERNAME + ":" + PROXY_AUTH_PASSWORD).getBytes())));
		//con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
		Response res =target.request().header("Proxy-Authorization", "Basic " + encoded).get();
		System.out.println(res.getStatus());*/
		//System.out.println(trade.deleteOrder("ETHUSDT", "134499982"));
		//new BigDecimal("240.44")
	//	trade.sellCoin("ETHUSDT",2, new BigDecimal("0.1"),new BigDecimal("240.44") , new BigDecimal("235.50"));

//134493950
		
		/*BigDecimal value = new BigDecimal("0.00000001");
		System.out.println("BigInteger " + value.toBigInteger());
		System.out.println("Plani String"+value.toPlainString());
		System.out.println("Engginerre String"+value.toEngineeringString());
		System.out.println("Integer Extract  : "+ value.toBigIntegerExact());*/
	
	/*	BitsoTrade trade2 = new BitsoTrade();
		trade2.sellCoin();*/
		//jsonToObject("C:/Documents/binance_exchange_symbol.json", SymbolDetails.class);
	//	jsonToObject("C:/Documents/exchangeInfo.json", BinanceExchangeInfo.class);
			System.out.println("I am here ");
			/*Response response =	TradeClient.getAdvancedClient("https://api.binance.com",true).path("/api/v1/exchangeInfo").request().get();
			
			if(response!= null && response.getStatus() == 200) 
			{
				System.out.println("I am success here");
				System.out.println(response.readEntity(String.class));
			}
			else {
				System.out.println("Iam failure");
			}*/

	}

	@SuppressWarnings("unchecked")
	public static void jsonToObject(String filepath,Class target) {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.readValue(new File(filepath), target));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
