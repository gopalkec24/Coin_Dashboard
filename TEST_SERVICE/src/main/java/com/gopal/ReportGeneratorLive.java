package com.gopal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gopal.currency.CoinMeanVO;

public class ReportGeneratorLive {

	public static void main(String[] args) {
		
		String csvFile = "D:\\Documents\\transactiom\\transaction_1.csv";
		// String coinSettingFile ="D:\\Documents\\transactiom\\coin_setting.csv";   
	        try 
	        {
	        	 Map<String, List<CoinVO>> coinMap = readTransaction(csvFile);
	        	 Map<String,CoinSettingVO> coinSettingMap = ReadWebPage.readCoinSetting();
				processCoinVO(coinMap,coinSettingMap);
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}

	private static Map<String,CoinSettingVO> readCoinSetting(String coinSettingFile) {
	
		 BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
	        Map<String,CoinSettingVO> coinMap = new TreeMap<String,CoinSettingVO>();
	        try {

	            br = new BufferedReader(new FileReader(coinSettingFile));
	            String coinName = null;
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] country = line.split(cvsSplitBy);
	                CoinSettingVO coin = new CoinSettingVO();
	                coinName=country[0];
	                coin.setCoinName(coinName);
	                coin.setPercentage(Double.parseDouble(country[1]));
	                coin.setCurrentRate(Double.parseDouble(country[2]));	               
	                coinMap.put(coinName, coin);               
	               //  System.out.println(" [Coin= " + country[0] + " , Transaction Type=" + country[3] + "]");
	            }
	        } 
	        catch (FileNotFoundException e)
	        {
	            e.printStackTrace();
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        finally 
	        {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	           
	        }
		return coinMap;
	}

	private static Map<String, List<CoinVO>> readTransaction(String csvFile) 
	{
		
	        BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
	        Map<String,List<CoinVO>> coinMap = new TreeMap<String,List<CoinVO>>();
	        try
	        {
	            br = new BufferedReader(new FileReader(csvFile));
	            String coinName = null;
	            while ((line = br.readLine()) != null) 
	            {
	                // use comma as separator
	                String[] country = line.split(cvsSplitBy);
	                CoinVO coin = new CoinVO();
	                coinName=country[0];
	                coin.setName(coinName);
	                coin.setPrice(Double.parseDouble(country[2]));
	                coin.setVolume(Double.parseDouble(country[1]));
	                coin.setTransactionType(Integer.parseInt(country[3]));
	                List<CoinVO> list =null;
	                if(coinMap.containsKey(coinName)) 
	                {
	                	list = coinMap.get(coinName);
	                }
	                else 
	                {
	                	list = new ArrayList<CoinVO>();
	                }
	                list.add(coin);                	
                	coinMap.put(coinName,list );
	               //  System.out.println(" [Coin= " + country[0] + " , Transaction Type=" + country[3] + "]");
	            }
	        } 
	        catch (FileNotFoundException e) 
	        {
	            e.printStackTrace();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        } 
	        finally 
	        {
	            if (br != null) 
	            {
	                try 
	                {
	                    br.close();
	                } 
	                catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	           
	        }
		return coinMap;
	}

	private static void processCoinVO1(Map<String, List<CoinVO>> coinMap, Map<String,CoinSettingVO> coinSettingMap) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("D:\\Documents\\transactiom\\report.csv");
		pw.println("Coin Name, Total volume , TotalPrice,Average Price ,Project Sell value,Current value, Current Portifilo Value, Profit/loss,Profit Percentage,StablizeCount,StablizeRate");
		for(String coinName  : coinMap.keySet()) {
			List<CoinVO> coinVOList= coinMap.get(coinName);
			   double totalPrice = 0;
			   double totalVolume = 0;
			   double buyPrice = 0;
			   double sellPrice = 0;
			  
			 for(CoinVO coinVO : coinVOList) {
				 double costPrice = coinVO.getPrice()* coinVO.getVolume();
				 double totalNetPrice = 0.0 ;
				 if(coinVO.getTransactionType() == 1) {
					totalNetPrice = costPrice + (costPrice*0.0025);
					buyPrice += totalNetPrice;
				   totalPrice += totalNetPrice;
				   totalVolume += coinVO.getVolume();
				 }
				 else {
					 totalNetPrice = costPrice;
					 sellPrice += totalNetPrice;
					   totalPrice -= totalNetPrice;
					   totalVolume -= coinVO.getVolume();
				 }
						   
				 
			 }
			 double mean = getMeanValue(totalPrice, totalVolume,sellPrice);			 
			 double profit = ((sellPrice > 0)?(sellPrice-buyPrice): 0);
			 
			 CoinSettingVO settingVO = coinSettingMap.get(coinName);
			 double projectedSellValue = 0 ;
			 double currentValue = 0;
			 double stablizeRate = 0;
			 double stablizeCount = 0;
			 if(settingVO != null) 
			 {
			 projectedSellValue = (mean * settingVO.getPercentage())/100 + mean ; 
			 currentValue = totalVolume * settingVO.getCurrentRate();
			 stablizeRate = findStablizeRate(mean,settingVO.getCurrentRate());
			 stablizeCount = findStablizeCount(mean,totalVolume,settingVO.getCurrentRate(),stablizeRate);
			 }
			 pw.println(coinName+ ","+totalVolume+","+ ((totalPrice>0 ) ?totalPrice : 0)+","+mean+","+projectedSellValue+","+settingVO.getCurrentRate()+","+currentValue+","+(currentValue-totalPrice)+","+(profit>0 ? ((profit/buyPrice)*100):0)+","+stablizeCount+","+stablizeRate);
			  
			
		}
		pw.close();
		
	}

	public static double getMeanValue(double totalPrice, double totalVolume,double sellPrice) {
		double mean=0.0;
		if(totalVolume > 0 )
		{
			  mean = totalPrice / totalVolume ;
		}		
		return mean;
	}

	private static double findStablizeCount(double mean, double totalVolume, double currentRate, double stablizeRate) {
		
		return ((mean*totalVolume) - (currentRate*totalVolume))/currentRate;
	}

	private static double findStablizeRate(double mean,double currentValue) {
		  System.out.println(currentValue+" " + mean);
		return (currentValue*2)-mean;
	}

	
	private static void processCoinVO(Map<String, List<CoinVO>> coinMap, Map<String,CoinSettingVO> coinSettingMap) throws FileNotFoundException { 
		PrintWriter pw = new PrintWriter("D:\\Documents\\transactiom\\report.csv"); 
		//pw.println("Coin Name, Total volume ,Buy Price,Sell Price,Current Investment Amount, Average Price ,Project Sell value,Current value, Current Portifilo Value,Profit Realised,Profit in Portfolio,Net Profit/loss,Profit Realised Percentage,Profit in Portfolio Percentage,Profit Percentage,StablizeCount,StablizeRate"); 
		pw.println("Coin, Tot. vol,Curr Invs Amt, Avg Price ,Tgt Sell amt,Curr Mkt Price, Curr Port. Amt,Realised Gain,Gain in Port.,Net Profit/loss,Gain Realised %,Gain in Port. %,Profit %,StablizeCount,StablizeRate"); 

		double totalBuyPrice = 0; 
		double totalSellPrice = 0; 
		double totalInvAmt = 0; 
		double initialInv = 34800; 
		double totalProfitRealized =0; 
		double totalProfitInPortfolio =0; 

		for(String coinName  : coinMap.keySet()) { 
		List<CoinVO> coinVOList= coinMap.get(coinName); 
		   
		// Stage 1 :Input for processing 
		double currentMarketRate = 0; 
		double targetPercentage=0; 

		//Stage 2 : Input for calculating the mean,profit and % 
		   
		   
		   
		   //Processed values 
		   double buymean = 0; 
		double sellmean = 0; 
		double totalVolume = 0; 
		    double projectedSellValue = 0; 
		double currentMarketValue = 0; 
		double stablizeRate = 0; 
		double stablizeCount =0; 
		double profitInPortfoilo=0; 
		double soldInvestement = 0; 
		double profitRealised = 0; 
		double currentInvestedAmount = 0; 
		double profit =0; 
		double profitPercent =0; 
		double profitRealisedPercent = 0; 
		double profitInPortfolioPercent = 0; 
		double currentInvestmentAmt = 0; 



		     CoinMeanVO meanVO = calculateMeanFromTransaction(coinVOList); 
		meanVO.getBuyPrice(); 
		meanVO.getSellVolume(); 
		meanVO.getSellPrice(); 
		meanVO.getBuyVolume(); 
		totalVolume = meanVO.getBuyVolume() - meanVO.getSellVolume(); 
		System.out.println("Coin name: "+coinName); 
		CoinSettingVO settingVO = coinSettingMap.get(coinName); 
		if(settingVO != null) { 
		currentMarketRate = settingVO.getCurrentRate() ; 
		targetPercentage = settingVO.getPercentage(); 
		} 


		buymean = meanVO.getBuyPrice()/meanVO.getBuyVolume(); 
		sellmean = meanVO.getSellPrice() > 0 ? meanVO.getSellPrice() /meanVO.getSellVolume() : 0; 
		soldInvestement = buymean * meanVO.getSellVolume(); 
		System.out.println("Sold Investment"+soldInvestement); 
		profitRealised = (meanVO.getSellVolume() * sellmean ) - soldInvestement; 
		currentMarketValue = totalVolume * currentMarketRate; 
		currentInvestedAmount = totalVolume * buymean; 
		profitInPortfoilo = currentMarketValue - currentInvestedAmount; 
		profit = profitInPortfoilo + profitRealised; 
		  profitPercent = (profit/meanVO.getBuyPrice())*100; 
		  profitRealisedPercent = (profitRealised/meanVO.getBuyPrice())*100; 
		  profitInPortfolioPercent = (profitInPortfoilo/meanVO.getBuyPrice())*100; 
		  projectedSellValue = (buymean * targetPercentage)/100 + buymean; 
		  currentInvestmentAmt = meanVO.getBuyPrice()-soldInvestement; 
		  System.out.println("Current value in Portfolio"+currentInvestmentAmt); 
		  stablizeRate = findStablizeRate(buymean,currentMarketRate); 
		stablizeCount = findStablizeCount(buymean,totalVolume,currentMarketRate,stablizeRate); 

		  System.out.println("buy Mean ("+ meanVO.getBuyPrice()+"/"+meanVO.getBuyVolume()+") : "+buymean); 
		System.out.println("Sell Mean ("+ meanVO.getSellPrice()+"/"+meanVO.getSellVolume()+") : "+sellmean); 
		System.out.println("Buy Price : "+ meanVO.getBuyPrice()); 
		System.out.println("Sell Price: "+ meanVO.getSellPrice()); 
		System.out.println("Current Portfolio "+currentMarketValue); 
		System.out.println("Actual amount :"+ currentInvestedAmount); 
		System.out.println("Profit Realised : "+ profitRealised); 
		System.out.println("profit In Portfolio "+ profitInPortfoilo); 
		System.out.println("Net Total Profit "+ profit); 
		System.out.println("Net Profit Realised Percentage "+profitRealisedPercent); 
		System.out.println("Net Profit Percentage "+profitPercent); 
		System.out.println("Recalculated Projected Sellvalue :"+ projectedSellValue); 
		System.out.println("After stablize Rate : "+stablizeRate); 
		System.out.println("After Stablixe count :" + stablizeCount); 


		System.out.println(); 


		totalInvAmt +=currentInvestedAmount; 

		totalProfitInPortfolio += profitInPortfoilo; 
		totalProfitRealized += profitRealised; 
		totalBuyPrice += meanVO.getBuyPrice(); 
		totalSellPrice += meanVO.getSellPrice(); 



		//pw.println(coinName+ ","+totalVolume+","+buyPrice+","+sellPrice+","+currentInvestmentAmt+","+buymean+","+projectedSellValue+","+currentMarketRate+","+currentValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate); 
		pw.println(coinName+ ","+totalVolume+","+currentInvestmentAmt+","+buymean+","+projectedSellValue+","+currentMarketRate+","+currentMarketValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate);  

		} 

		pw.println(); 
		pw.println(",,Total Buy Amt,Total Sell amt,Tot. Comm amt,Tot. Invs amt,Tot Gain Port.,Tot Gain Realized,Tot. Net Gain %,Gain amt in Mkt"); 
		double commissionAmt = totalBuyPrice*0.0025; 
		double netcashflow = totalBuyPrice - totalSellPrice; 
		System.out.println("Net Cash flow differencen " +netcashflow); 
		System.out.println("Net Current Invers amt : "+totalInvAmt); 
		System.out.println("Total differ "+ (totalInvAmt -netcashflow)); 
		pw.println(",,"+totalBuyPrice+","+totalSellPrice+","+commissionAmt+","+totalInvAmt+ ","+ totalProfitInPortfolio+ ","+totalProfitRealized +","+((totalProfitInPortfolio+totalProfitRealized-commissionAmt)/initialInv)*100+","+(totalInvAmt-initialInv)); 
		pw.close(); 

		}
	
	private static  CoinMeanVO calculateMeanFromTransaction(List<CoinVO> coinVOList){ 
		CoinMeanVO meanVO = new CoinMeanVO(); 
		//Stage 2 : Input for calculating the mean,profit and % 
		   
		   double buyPrice = 0; 
		   double sellPrice = 0; 
		   double buyVolume =0; 
		   double sellVolume = 0; 
		double costPrice=0; 
		double totalNetPrice =0; 
		for(CoinVO coinVO : coinVOList) { 
		costPrice = coinVO.getPrice()* coinVO.getVolume(); 
		  System.out.println(coinVO.getPrice()+"*"+coinVO.getVolume()+"="+costPrice); 
		if(coinVO.getTransactionType() == 1) 
		{ 
		totalNetPrice = costPrice + (costPrice*0.0025); 
		// System.out.println("Cost Price : "+totalNetPrice); 
		buyPrice += totalNetPrice; 
		  
		   buyVolume+=coinVO.getVolume(); 
		   //totalVolume += coinVO.getVolume(); 
		   
		} 
		else 
		{ 
		totalNetPrice = costPrice; 
		sellPrice += totalNetPrice; 

		   sellVolume += coinVO.getVolume(); 
		  // totalVolume -= coinVO.getVolume(); 
		} 
		   

		} 

		meanVO.setBuyPrice(buyPrice); 
		meanVO.setSellVolume(sellVolume); 
		meanVO.setBuyVolume(buyVolume); 
		meanVO.setSellPrice(sellPrice); 

		return meanVO; 
		}


}
