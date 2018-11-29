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
import java.util.concurrent.ArrayBlockingQueue;

import com.gopal.currency.CoinMeanVO;

public class ReportGeneratorLive {

	public static final boolean basicMode = false;

	public static void main(String[] args) {

		String csvFile = "D:\\Documents\\transaction_1.csv";
		String exchangeURL = "https://coinmarketcap.com/exchanges/koinex/";
		double initialInv = 34800.00;
		String reportFile = "D:\\Documents\\report.csv";

		if(args.length > 0){
			csvFile = args[0];
			exchangeURL=args[1];
			initialInv=Double.parseDouble(args[2]);
			reportFile=args[3];
			
		}
		// String coinSettingFile ="D:\\Documents\\transactiom\\coin_setting.csv";   
		try 
		{
			Map<String, List<TransactionVO>> coinMap = readTransaction(csvFile);
			//ReadWebPage.setProxyDetails("10.121.9.125", "8080", "hcltech\natarajan_g", "dummpy@123");
			
			Map<String,CoinSettingVO> coinSettingMap = ReadWebPage.readCoinSetting(exchangeURL);
			ReportGeneratorLive reportGenerator = new ReportGeneratorLive();
			
			
			reportGenerator.processCoinVO(coinMap,coinSettingMap,initialInv,reportFile);
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

	public static Map<String,CoinSettingVO> readCoinSetting(String coinSettingFile) {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Map<String,CoinSettingVO> coinMap = new TreeMap<String,CoinSettingVO>();
		try {

			br = new BufferedReader(new FileReader(coinSettingFile));
			String coinName = null;
			
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] transactionDetails = line.split(cvsSplitBy);
				CoinSettingVO coin = new CoinSettingVO();
				coinName=transactionDetails[0];
				coin.setCoinName(coinName);
				coin.setPercentage(Double.parseDouble(transactionDetails[1]));
				coin.setCurrentRate(Double.parseDouble(transactionDetails[2]));	               
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

	private static Map<String, List<TransactionVO>> readTransaction(String csvFile) throws Exception 
	{

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Map<String,List<TransactionVO>> coinMap = new TreeMap<String,List<TransactionVO>>();

		try
		{
			br = new BufferedReader(new FileReader(csvFile));
			String coinName = null;
			while ((line = br.readLine()) != null) 
			{
				//line is empty and startswith # means it is comment in transaction
				if(line.length()<0&& line.startsWith("#"))
				{
					continue;
				}
				// use comma as separator
				String[] transactionDetails = line.split(cvsSplitBy);
				TransactionVO coin = new TransactionVO();
				coinName=transactionDetails[0];
				coin.setName(coinName);
				coin.setPrice(Double.parseDouble(transactionDetails[2]));
				coin.setVolume(Double.parseDouble(transactionDetails[1]));
				coin.setTransactionType(Integer.parseInt(transactionDetails[3]));

				if(transactionDetails.length > 4)
				{
					coin.setCommission(Double.parseDouble(transactionDetails[4]));

				}
				else{
					coin.setCommission(Double.NaN);
				}

				List<TransactionVO> list =null;
				if(coinMap.containsKey(coinName)) 
				{
					list = coinMap.get(coinName);
				}
				else 
				{
					list = new ArrayList<TransactionVO>();
				}
				list.add(coin);                	
				coinMap.put(coinName,list );
				//  System.out.println(" [Coin= " + country[0] + " , Transaction Type=" + country[3] + "]");
			}
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

	/*private static void processCoinVO1(Map<String, List<CoinVO>> coinMap, Map<String,CoinSettingVO> coinSettingMap) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("D:\\Documents\\report.csv");
		pw.println("Coin Name, Total volume , TotalPrice,Average Price ,Project Sell value,Current value, Current Portifilo Value, Profit/loss,Profit Percentage,StablizeCount,StablizeRate");
		for(String coinName  : coinMap.keySet()) {
			List<CoinVO> coinVOList= coinMap.get(coinName);
			   double totalPrice = 0;
			   double totalVolume = 0;
			   double buyPrice = 0;
			   //Total sellPrice
			   double sellPrice = 0;
			   //Total commission Rate
			   double commission = 0;
			 for(CoinVO coinVO : coinVOList) {
				 double costPrice = coinVO.getPrice() * coinVO.getVolume();

				 double totalNetPrice = 0.0 ;


				 if(coinVO.getTransactionType() == 1) {

					buyPrice += totalNetPrice;
				   totalPrice += totalNetPrice;
				   totalVolume += coinVO.getVolume();

				 }
				 else {

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

	/*public static double getMeanValue(double totalPrice, double totalVolume,double sellPrice) {
		double mean=0.0;
		if(totalVolume > 0 )
		{
			  mean = totalPrice / totalVolume ;
		}		
		return mean;
	}*/

	private static double findStablizeCount(double mean, double totalVolume, double currentRate, double stablizeRate) {

		return ((mean*totalVolume) - (currentRate*totalVolume))/currentRate;
	}

	private static double findStablizeRate(double mean,double currentValue) {
		System.out.println(currentValue+" " + mean);
		return (currentValue*2)-mean;
	}


	private void processCoinVO(Map<String, List<TransactionVO>> coinMap, Map<String,CoinSettingVO> coinSettingMap,double initialInv,String reportFile) throws FileNotFoundException { 
		PrintWriter pw = new PrintWriter(reportFile); 
		//pw.println("Coin Name, Total volume ,Buy Price,Sell Price,Current Investment Amount, Average Price ,Project Sell value,Current value, Current Portifilo Value,Profit Realised,Profit in Portfolio,Net Profit/loss,Profit Realised Percentage,Profit in Portfolio Percentage,Profit Percentage,StablizeCount,StablizeRate"); 
		pw.println("Coin, Tot. vol,Curr Invs Amt, Avg Price,Avg 4 Price ,Tgt %,Tgt Sell amt,Tgt Last Sell amt,Curr Mkt Price, Curr Port. Amt,Realised Gain,Gain in Port.,Net Profit/loss,Gain Realised %,Gain in Port. %,Profit %,StablizeCount,StablizeRate"); 

		double totalBuyPrice = 0; 
		double totalSellPrice = 0; 
		double totalInvAmt = 0; 

		double totalProfitRealized =0; 
		double totalProfitInPortfolio =0; 
		double commissionAmt = 0;
		for(String coinName  : coinMap.keySet()) { 
			List<TransactionVO> coinVOList= coinMap.get(coinName); 

			// Stage 1 :Input for processing 
			double currentMarketRate = 0; 
			double targetPercentage=0; 

			//Stage 2 : Input for calculating the mean,profit and % 



			//Processed values 
			double buymean = 0; 
			double nbuyMean=0;
			double sellmean = 0; 
			double totalVolume = 0; 
			double projectedSellValue = 0; 
			double projectedLastSellValue =0;
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



			CoinMeanVO meanVO = calculateMeanFromTransaction(coinVOList,2); 

			totalVolume = meanVO.getBuyVolume() - meanVO.getSellVolume(); 
			System.out.println("Coin name: "+coinName); 
			CoinSettingVO settingVO = coinSettingMap.get(coinName); 
			
			
			

			buymean = meanVO.getBuyPrice()/meanVO.getBuyVolume(); 
			nbuyMean= meanVO.getnBuyPrice()/meanVO.getnBuyVolume();
			sellmean = meanVO.getSellPrice() > 0 ? meanVO.getSellPrice() /meanVO.getSellVolume() : 0; 
			soldInvestement = buymean * meanVO.getSellVolume(); 
			System.out.println("Sold Investment"+soldInvestement); 
			
			if(settingVO != null) { 
				currentMarketRate = settingVO.getCurrentRate() ; 

				if (basicMode) {
					targetPercentage = settingVO.getPercentage();
				}
				else
				{
                    targetPercentage = getPercentageFromRobot(nbuyMean);

				} 
			}
			
			
			profitRealised = (meanVO.getSellVolume() * sellmean ) - soldInvestement; 
			currentMarketValue = totalVolume * currentMarketRate; 
			currentInvestedAmount = totalVolume * buymean; 
			profitInPortfoilo = currentMarketValue - currentInvestedAmount; 
			profit = profitInPortfoilo + profitRealised; 
			profitPercent = (profit/meanVO.getBuyPrice())*100; 
			profitRealisedPercent = (profitRealised/meanVO.getBuyPrice())*100; 
			profitInPortfolioPercent = (profitInPortfoilo/meanVO.getBuyPrice())*100; 
			
			projectedSellValue = (buymean * targetPercentage)/100 + buymean; 
			projectedLastSellValue = (nbuyMean * targetPercentage)/100 + nbuyMean;
			
			
			
			currentInvestmentAmt = meanVO.getBuyPrice()-soldInvestement; 
			System.out.println("Current value in Portfolio"+currentInvestmentAmt); 
			stablizeRate = findStablizeRate(buymean,currentMarketRate); 
			stablizeCount = findStablizeCount(buymean,totalVolume,currentMarketRate,stablizeRate); 

			System.out.println("buy Mean ("+ meanVO.getBuyPrice()+"/"+meanVO.getBuyVolume()+") : "+buymean); 
			System.out.println("N buy Mean ("+ meanVO.getnBuyPrice()+"/"+meanVO.getnBuyVolume()+") : "+nbuyMean); 
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
			commissionAmt += meanVO.getCommission();

			//pw.println(coinName+ ","+totalVolume+","+buyPrice+","+sellPrice+","+currentInvestmentAmt+","+buymean+","+projectedSellValue+","+currentMarketRate+","+currentValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate); 
			pw.println(coinName+ ","+totalVolume+","+currentInvestmentAmt+","+buymean+","+nbuyMean+","+targetPercentage+","+projectedSellValue+","+projectedLastSellValue+","+currentMarketRate+","+currentMarketValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate);  

		} 

		pw.println(); 
		pw.println(",,Total Buy Amt,Total Sell amt,Tot. Comm amt,Tot. Invs amt,Tot Gain Port.,Tot Gain Realized,Tot. Net Gain %,Gain amt in Mkt"); 

		double netcashflow = totalBuyPrice - totalSellPrice; 
		System.out.println("Net Cash flow differencen " +netcashflow); 
		System.out.println("Net Current Invers amt : "+totalInvAmt); 
		System.out.println("Total differ "+ (totalInvAmt -netcashflow)); 
		pw.println(",,"+totalBuyPrice+","+totalSellPrice+","+commissionAmt+","+totalInvAmt+ ","+ totalProfitInPortfolio+ ","+totalProfitRealized +","+((totalProfitInPortfolio+totalProfitRealized-commissionAmt)/initialInv)*100+","+(totalInvAmt-initialInv)); 
		pw.close(); 

	}

	public double getPercentageFromRobot(Double nbuyMean) {
        
		double returnPercent = 10;
		
		if(nbuyMean < 10){
		     returnPercent = 25;
		}
		else if(nbuyMean <  50){
			returnPercent = 20;
		}
		else if(nbuyMean <  100){
			returnPercent = 15;
		}
		else if(nbuyMean <  1000){
			returnPercent = 10;
		}
		else
		{
			returnPercent =5;
		}
		
		

		return returnPercent;
	}

	public  CoinMeanVO calculateMeanFromTransaction(List<TransactionVO> coinVOList,int lastNTransact){ 
		CoinMeanVO meanVO = new CoinMeanVO(); 
		//Stage 2 : Input for calculating the mean,profit and % 

		double buyPrice = 0; 
		double sellPrice = 0; 
		double buyVolume =0; 
		double sellVolume = 0; 
		double costPrice=0; 
		double totalNetPrice =0; 
		//Total commission Rate
		double commission = 0;
		//Transaction commission amount Calculation
		double commissionRate =0;
		
		//stage 3 : Queue for calculating the last n transaction amount for buy alone
		// This is calculate the latest buy average to calculate exact amount in market
		
		ArrayBlockingQueue<Integer> lastNBuyItem = new ArrayBlockingQueue<Integer>(lastNTransact);
		int position =0 ;
		for(TransactionVO coinVO : coinVOList) 
		{ 
			costPrice = coinVO.getPrice()* coinVO.getVolume(); 

			System.out.println(coinVO.getPrice()+"*"+coinVO.getVolume()+"="+costPrice);
            // 1 indicates it is buy transaction,other are sell transaction
			if(coinVO.getTransactionType() == 1) 
			{ 
				//old transaction does not have commission value loaded in transaction file
				//so this is check made 
				if (Double.isNaN(coinVO.getCommission())) {
					commissionRate = costPrice * 0.0025;
					System.out.println("This work....");
					
				}
				else
				{
					commissionRate = costPrice * coinVO.getCommission();
					
					System.out.println("This should not work earlier transaction"+ coinVO.getCommission());
				}
				totalNetPrice = costPrice + (commissionRate);
				// System.out.println("Cost Price : "+totalNetPrice); 
				buyPrice += totalNetPrice; 
				buyVolume+=coinVO.getVolume(); 
				addQueueItem(lastNBuyItem, lastNTransact, position);

			} 
			else 
			{ 
				if (Double.isNaN(coinVO.getCommission())) {
					commissionRate =0;
				}
				else{
					commissionRate = costPrice * coinVO.getCommission();
				}
				totalNetPrice = costPrice - commissionRate;
				sellPrice += totalNetPrice; 
				sellVolume += coinVO.getVolume(); 
			} 
			System.out.println("Commission Amount " +commissionRate);
			commission+=commissionRate;
        position++;
		} 
        //calculate the n transaction amount and volume
		position=-1;
		 double totalNBuyPrice =0;
		 double totalNBuyVolume = 0;
		 int size = lastNBuyItem.size();
		meanVO.setnBuyCount(size);
		for(int i=0; i<size; i++){
			Integer poll = lastNBuyItem.poll();
			TransactionVO coinVO = coinVOList.get(poll);
			System.out.println("Polled value :"+poll);
			totalNBuyPrice+=(coinVO.getPrice()*coinVO.getVolume());
			totalNBuyVolume += coinVO.getVolume();
			System.out.println(totalNBuyPrice);
			System.out.println(totalNBuyVolume);
		}
		
		meanVO.setnBuyPrice(totalNBuyPrice);
		meanVO.setnBuyVolume(totalNBuyVolume);
		
		meanVO.setBuyPrice(buyPrice); 
		meanVO.setSellVolume(sellVolume); 
		meanVO.setBuyVolume(buyVolume); 
		meanVO.setSellPrice(sellPrice); 
		meanVO.setCommission(commission);

		return meanVO; 
	}

public  void addQueueItem(ArrayBlockingQueue<Integer> arrayQueue, int queueLength, int queueitem) {
		
		if(arrayQueue.size() < queueLength){
			System.out.println("Well It enters here"+queueitem);
			arrayQueue.add(queueitem);
		}
		else{
			System.out.println("What error it says it is full ");
			System.out.println("It is remvoed from queue is " +arrayQueue.poll());
			arrayQueue.add(queueitem);
			
		}
	}
}
