package com.portfolio.utilis;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.portfolio.dao.CurrencyMean;

public class LossMinimiser {

	private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		float buyPrice =1.93193f ;
		float buVol =13.54f;
		float currentPrice = 0.6511f;
		double deviation = 0.05;
		lossFunction1(buyPrice, buVol, currentPrice,deviation);
		float profit=(currentPrice*buVol)-(buyPrice*buVol);
		
		System.out.println("Prot is "+profit);
		
		float price=220.00f;
		 
		

	}

	private static void lossFunction1(float buyPrice, float buVol, float currentPrice,double deviation) {
		float profit= currentPrice - buyPrice;
		if(profit< 0){
			
			float currentAmt = buyPrice* buVol;
			System.out.println("Loss Amount: "+profit);
			int effectiveVolume=0;
			float total_amt=0;
			double diffAmt=0;
			float amt =0;
			for(int i=1;;i++){
				
				total_amt= currentAmt+(currentPrice*i);				
				amt =total_amt/(buVol+i);
				 
				diffAmt = amt-(amt*deviation);
				if(diffAmt <= currentPrice){					
					effectiveVolume=i;
					break;
				}
				
			}
			System.out.println("Total amt spend for this item : "+total_amt);
			System.out.println("Volume Required to buy now : "+effectiveVolume);
			System.out.println("Amt to be invested :  "+currentPrice*effectiveVolume);
			float currentMarketValue = currentPrice*(effectiveVolume+buVol);
			System.out.println("Effective Loss % :"+ ((currentMarketValue-total_amt)/total_amt)*100);
			
		}
	}
	
	public static BigDecimal lossFunction1(BigDecimal buyPrice, BigDecimal buVol, BigDecimal currentPrice,BigDecimal deviation) {
		BigDecimal profit= currentPrice.subtract(buyPrice);
		BigDecimal effectiveVolume = BIG_DECIMAL_ZERO;
		if(profit.compareTo(BIG_DECIMAL_ZERO) == -1){
			
			BigDecimal currentMarketPrice = buyPrice.multiply(buVol);
			System.out.println("Loss Amount: "+profit);
			
			BigDecimal total_amt=BIG_DECIMAL_ZERO;
			BigDecimal diffAmt=BIG_DECIMAL_ZERO;
			BigDecimal amt = BIG_DECIMAL_ZERO;
			for(int i=1;;i++){
				
				BigDecimal count = new BigDecimal(i);
				total_amt= currentMarketPrice.add(currentPrice.multiply(count));				
				amt =total_amt.divide(buVol.add(count),8,RoundingMode.HALF_UP);
				 
				diffAmt = amt.subtract(amt.multiply(deviation));
				if(diffAmt.compareTo(currentPrice)== -1){					
					effectiveVolume=count;
					break;
				}
				
			}
			System.out.println("Total amt spend for this item : "+total_amt);
			System.out.println("Volume Required to buy now : "+effectiveVolume);
			System.out.println("Amt to be invested :  "+currentPrice.multiply(effectiveVolume));
			BigDecimal currentMarketValue = currentPrice.multiply(effectiveVolume.add(buVol));
			System.out.println("Effective Loss % :"+ ((currentMarketValue.subtract(total_amt)).divide(total_amt,2,RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
			
		}
		return effectiveVolume;
	}

	
	public static void lossFunction1(CurrencyMean currMean,BigDecimal deviation) {
		BigDecimal profit= currMean.getLastPrice().subtract(currMean.getBuyMean());
		if(profit.compareTo(BIG_DECIMAL_ZERO) == -1){
			
			BigDecimal currentMarketPrice = currMean.getBuyMean().multiply(currMean.getBuyVolume());
			System.out.println("Loss Amount: "+profit);
			BigDecimal effectiveVolume = BIG_DECIMAL_ZERO;
			BigDecimal total_amt=BIG_DECIMAL_ZERO;
			BigDecimal diffAmt=BIG_DECIMAL_ZERO;
			BigDecimal amt = BIG_DECIMAL_ZERO;
			for(int i=1;;i++){
				
				BigDecimal count = new BigDecimal(i);
				total_amt= currentMarketPrice.add(currMean.getLastPrice().multiply(count));				
				amt =total_amt.divide(currMean.getBuyVolume().add(count),8,RoundingMode.HALF_UP);
				 
				diffAmt = amt.subtract(amt.multiply(deviation));
				if(diffAmt.compareTo(currMean.getLastPrice())== -1){					
					effectiveVolume=count;
					break;
				}
				
			}
			System.out.println("Total amt spend for this item : "+total_amt);
			System.out.println("Volume Required to buy now : "+effectiveVolume);
			System.out.println("Amt to be invested :  "+currMean.getLastPrice().multiply(effectiveVolume));
			BigDecimal currentMarketValue = currMean.getLastPrice().multiply(effectiveVolume.add(currMean.getBuyVolume()));
			System.out.println("Effective Loss % :"+ ((currentMarketValue.subtract(total_amt)).divide(total_amt,2,RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
			
		}
	}
	
}