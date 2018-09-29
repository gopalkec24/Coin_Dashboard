package com.portfolio.utilis;

public class LossMinimiser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		float buyPrice =1.93193f ;
		float buVol =13.54f;
		float currentPrice = 0.6511f;
		
		lossFunction1(buyPrice, buVol, currentPrice);
		float profit=(currentPrice*buVol)-(buyPrice*buVol);
		
		System.out.println("Prot is "+profit);
		
		float price=220.00f;
		 
		

	}

	private static void lossFunction1(float buyPrice, float buVol, float currentPrice) {
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
				 diffAmt = amt-(amt*0.05);
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

}