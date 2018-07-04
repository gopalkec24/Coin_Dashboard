package com.gopal.test;

import java.math.BigDecimal;

public class TestPercentage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String rate = "0.1";
		BigDecimal investAmt = new BigDecimal("34000.00");
		BigDecimal rateAmt = new BigDecimal(rate);
		BigDecimal compoundAmt = new BigDecimal("34000.00");
		int times = 3;
		System.out.println(investAmt);
		for(int i=0; i< times ;i++){
			
			investAmt = (investAmt.multiply(rateAmt)).add(investAmt);
			System.out.println(investAmt);
		}
		System.out.println(" Percentage  "+investAmt.subtract(compoundAmt).divide(compoundAmt).multiply(new BigDecimal("100")));

	}

}
