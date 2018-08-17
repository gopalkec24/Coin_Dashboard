package com.portfolio.utilis;

public class LossMinimiser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		float buyPrice = 10;
		float buVol =280;
		float currentPrice = 5;
		
		lossFunction1(buyPrice, buVol, currentPrice);
		float profit=(currentPrice*buVol)-(buyPrice*buVol);
		
		System.out.println("Prot is "+profit);
		

	}

	private static void lossFunction1(float buyPrice, float buVol, float currentPrice) {
		float profit= currentPrice - buyPrice;
		if(profit< 0){
			
			float currentAmt = buVol* buVol;
			System.out.println("Loss Amount:"+profit);
			for(int i=1;;i++){
				
				float total_amt = currentAmt+(currentPrice*i);
				
				float amt =total_amt/(buVol+i);
				if(amt<= currentPrice){
					System.out.println("Total amt"+total_amt);
					System.out.println("Amt "+amt);
					System.out.println("current PRice "+currentPrice);
					System.out.println(i);break;
				}
				
			}
			
		}
	}

}
