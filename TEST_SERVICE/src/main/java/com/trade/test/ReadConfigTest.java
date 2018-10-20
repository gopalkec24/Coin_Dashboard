package com.trade.test;

import com.trade.BinanceTrade;
import com.trade.BitsoTrade;

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
		trade.getMarketStatics();
		
		BitsoTrade trade2 = new BitsoTrade();
		trade2.sellCoin();

	}

}
