package com.trade.test;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;

import com.trade.BinanceTrade;
import com.trade.BitsoTrade;
import com.trade.utils.TradeClient;

public class ReadConfigTest {

	public static void main(String[] args) {
		
		
		/*BaseTrade basTrade = new BaseTrade() {
			
			@Override
			public void sellCoin() {
				// TODO Auto-generated method stub
				System.out.println("Testing sellcoin");
			}
			
			@Override
			public void getMarketStatics() {
				// TODO Auto-generated method stub
				System.out.println("Testing getMArketStatics");
			}
			
			@Override
			public void buyCoin() {
				// TODO Auto-generated method stub
				System.out.println("Testing nuycoint");
				
			}
		};*/
		
		
		BinanceTrade trade = new BinanceTrade();
		//trade.getOrderDetails("ETHUSDT");
		System.out.println(trade.deleteOrder("ETHUSDT", "134499982"));
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

}
