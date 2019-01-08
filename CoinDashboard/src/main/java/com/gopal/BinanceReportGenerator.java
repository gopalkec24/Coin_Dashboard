package com.gopal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

import com.gopal.currency.CalculatedValueVO;
import com.gopal.currency.CoinMeanVO;
import com.gopal.currency.CurrencyVO;
import com.gopal.currency.SummaryVO;

public class BinanceReportGenerator extends ReportGeneratorLive {
	
	private final static int TRANS_DATE_INDEX=0;
	private final static int COIN_NAME_INDEX=1;
	private final static int TRANS_VOLUME_INDEX=2;
	private final static int TRANS_TYPE_INDEX=4;
	private final static int TRANS_PRICE_INDEX=3;
	private final static int TRANS_COMM_INDEX=5;
	private final static int TRANS_CURRENCY=6;
	private final static int COIN_TRANS_CURR_RATE_INDEX=7;
	private final static int TRANS_LIVERATE_INDEX=8;
	private final static int TRANS_LIVERATE_CURRTYPE_INDEX=9;
	private final static int TRANS_INR_RATE_INDEX=10;
	private static final boolean getLiveRate = false;
	
	public final String NON_CYRPTO_CURRENCY = "USD|INR";
	public static void main(String[] args)
	{
		String transactionFile = "C:/Documents/binance_transaction.csv";
		try {
			Map<String,List<TransactionVO>> transactionMap = readTransaction(transactionFile);
			String exchangeURL = "https://coinmarketcap.com/exchanges/binance/";
			Map<String,CoinSettingVO> coinSettingMap = ReadWebPage.readMultiCoinSetting(exchangeURL);
			BinanceReportGenerator biregen = new BinanceReportGenerator();
			biregen.processTransactionVO(transactionMap,coinSettingMap,ReadWebPage.getLiveCurrency());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private  void processTransactionVO(Map<String, List<TransactionVO>> transactionMap,Map<String,CoinSettingVO> coinSettingMap,CurrencyVO currrentRateVO) throws FileNotFoundException {

		PrintWriter pw = new PrintWriter("C:\\Documents\\report_binance.csv"); 
		//pw.println("Coin Name, Total volume ,Buy Price,Sell Price,Current Investment Amount, Average Price ,Project Sell value,Current value, Current Portifilo Value,Profit Realised,Profit in Portfolio,Net Profit/loss,Profit Realised Percentage,Profit in Portfolio Percentage,Profit Percentage,StablizeCount,StablizeRate"); 
		pw.println("Coin, Tot. vol,Curr Invs Amt, Avg Price,Avg 4 Price ,Tgt %,Tgt Sell amt,Tgt Last Sell amt,Curr Mkt Price, Curr Port. Amt,Realised Gain,Gain in Port.,Net Profit/loss,Gain Realised %,Gain in Port. %,Profit %,StablizeCount,StablizeRate"); 

		double totalBuyPrice = 0; 
		double totalSellPrice = 0; 
		double totalInvAmt = 0; 
		double initialInv = 34800; 
		double totalProfitRealized =0; 
		double totalProfitInPortfolio =0; 
		double commissionAmt = 0;
		for(String coinName  : transactionMap.keySet()) { 
			
			List<TransactionVO> coinVOList= transactionMap.get(coinName); 

			

			//Stage 2 : Input for calculating the mean,profit and % 

			CoinMeanVO meanVO = calculateMeanFromTransaction(coinVOList,2); 

			double buyVolume = meanVO.getBuyVolume();
			double sellVolume = meanVO.getSellVolume();
			double getnBuyVolume = meanVO.getnBuyVolume();
			
			double buyPrice = meanVO.getBuyPrice();
			double sellPrice = meanVO.getSellPrice();						
			double getnBuyPrice = meanVO.getnBuyPrice();
			
			
			double buyPrInINR = meanVO.getBuyPriceInINR();
			double sellPrInINR = meanVO.getSellPriceInINR();
			double getnBuyPriceInINR = meanVO.getnBuyPriceInINR();
			
			
			
			double buyPrInUSD = meanVO.getBuyPriceInUSD();			
			
			double sellPrInUSD = meanVO.getSellPriceInUSD();
			double getnBuyPriceInUSD = meanVO.getnBuyPriceInUSD();
			
			
			CoinSettingVO settingVO = coinSettingMap.get(coinName); 
			
			
			
			/*profitRealised = (meanVO.getSellVolume() * (sellmean*coinSettingMap.get("ETH").getMultiCurrencyCurrentRate().get("BTC")) ) - soldInvestement; 
			currentMarketValue = totalVolume * currentMarketRate; 
			double actual_eth_price = buymean * coinSettingMap.get("ETH").getMultiCurrencyCurrentRate().get("BTC");
			currentInvestedAmount = totalVolume * actual_eth_price; 
			profitInPortfoilo = currentMarketValue - currentInvestedAmount; 
			profit = profitInPortfoilo + profitRealised; 
			profitPercent = (profit/(meanVO.getBuyPrice()*coinSettingMap.get("ETH").getMultiCurrencyCurrentRate().get("BTC")))*100; 
			profitRealisedPercent = (profitRealised/(meanVO.getBuyPrice()*coinSettingMap.get("ETH").getMultiCurrencyCurrentRate().get("BTC")))*100; 
			profitInPortfolioPercent = (profitInPortfoilo/(meanVO.getBuyPrice()*coinSettingMap.get("ETH").getMultiCurrencyCurrentRate().get("BTC")))*100; */
			
           SummaryVO summary= getSummary(pw,settingVO,coinName, buyPrInUSD, sellPrInUSD, buyVolume, sellVolume, getnBuyPriceInUSD, getnBuyVolume,false,currrentRateVO);
		//	SummaryVO summary= getSummary(pw,settingVO,coinName, buyPrInINR, sellPrInINR, buyVolume, sellVolume, getnBuyPriceInINR, getnBuyVolume);
			// SummaryVO summary= getSummary(pw,settingVO,coinName, buyPrice, sellPrice, buyVolume, sellVolume, getnBuyPrice, getnBuyVolume);
           // getSummary(coinName, buyPrInINR, sellPrInINR, buyVolume, sellVolume, getnBuyPriceInINR, getnBuyVolume, currentMarketRate, targetPercentage);
          //  getSummary(coinName, buyPrInUSD, sellPrInUSD, buyVolume, sellVolume, getnBuyPriceInUSD, getnBuyVolume, currentMarketRate, targetPercentage);



			System.out.println(); 


			totalInvAmt +=summary.getCurrentInvestedAmount(); 

			totalProfitInPortfolio += summary.getProfitInPortfoilo(); 
			totalProfitRealized += summary.getProfitRealised(); 
			totalBuyPrice += buyPrice; 
			totalSellPrice += sellPrice; 
			commissionAmt += meanVO.getCommission();

			//pw.println(coinName+ ","+totalVolume+","+buyPrice+","+sellPrice+","+currentInvestmentAmt+","+buymean+","+projectedSellValue+","+currentMarketRate+","+currentValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate); 
			//pw.println(coinName+ ","+totalVolume+","+currentInvestmentAmtInUSD+","+buymeanInUSD+","+nbuyMeanInUSD+","+targetPercentage+","+projectedSellValueInUSD+","+projectedLastSellValueInUSD+","+currentMarketRate+","+currentMarketValueInUSD+","+profitRealisedInUSD+","+profitInPortfoiloInUSD+"," +profitInUSD+","+profitRealisedPercentInUSD+","+ profitInPortfolioPercentInUSD +","+profitPercentInUSD+","+stablizeCount+","+stablizeRate);  



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
	
	
	private SummaryVO getSummary(PrintWriter pw,CoinSettingVO settingVO,String coinName,double buyPrice,double sellPrice,double buyVolume,double sellVolume,double getnBuyPrice,double getnBuyVolume,boolean inrMode,CurrencyVO currentRateVO){
		
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
		double targetPercentage = 0;
		
		double currentMarketRate =0;
		
		totalVolume = buyVolume - sellVolume;
		if(settingVO != null) { 
			System.out.println(settingVO.getMultiCurrencyCurrentRate());
			currentMarketRate = settingVO.getMultiCurrencyCurrentRate().get("ETH") ; 
			
			//TODO get conversion Rate online
			if(inrMode){
				currentMarketRate = currentMarketRate * currentRateVO.getUSDINR();
			}
			
			if (basicMode) {
				targetPercentage = settingVO.getPercentage();
			}
			else
			{
                targetPercentage = getPercentageFromRobot(nbuyMean);

			} 
		}
		//buyPRice,sell PRice,buy mean,sell mean will be in Dollars
		buymean = buyPrice/buyVolume; 
		System.out.println("buy Mean ("+ buyPrice+"/"+buyVolume+") : "+buymean); 
		nbuyMean= getnBuyPrice/getnBuyVolume;
		System.out.println("N buy Mean ("+ getnBuyPrice+"/"+getnBuyVolume+") : "+nbuyMean); 

		sellmean = sellPrice > 0 ? sellPrice /sellVolume : 0; 
		System.out.println("Sell Mean ("+ sellPrice+"/"+sellVolume+") : "+sellmean); 
		
		soldInvestement = buymean * sellVolume; 
		System.out.println("Sold Investment"+soldInvestement); 
		
		profitRealised = (sellVolume * sellmean ) - soldInvestement; 
		
		//calculated currentMarketValue will be in dollar
		currentMarketValue = totalVolume * currentMarketRate; 
		currentInvestedAmount = totalVolume * buymean; 
		profitInPortfoilo = currentMarketValue - currentInvestedAmount; 
		profit = profitInPortfoilo + profitRealised; 
		profitPercent = (profit/buyPrice)*100; 
		profitRealisedPercent = (profitRealised/buyPrice)*100; 
		profitInPortfolioPercent = (profitInPortfoilo/buyPrice)*100; 
		
		
		projectedSellValue = (buymean * targetPercentage)/100 + buymean; 
		projectedLastSellValue = (nbuyMean * targetPercentage)/100 + nbuyMean;
		
		currentInvestmentAmt = buyPrice-soldInvestement; 
		
		System.out.println("Current value in Portfolio"+currentInvestmentAmt); 
		//stablizeRate = findStablizeRate(buymean,currentMarketRate); 
		//stablizeCount = findStablizeCount(buymean,totalVolume,currentMarketRate,stablizeRate); 

		
		System.out.println("N buy Mean ("+ getnBuyPrice+"/"+getnBuyVolume+") : "+nbuyMean); 
		System.out.println("Sell Mean ("+ sellPrice+"/"+sellVolume+") : "+sellmean); 
		System.out.println("Buy Price : "+ buyPrice); 
		System.out.println("Sell Price: "+ sellPrice); 
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
        
		SummaryVO summary =new  SummaryVO();
		
		summary.setBuymean(buymean);
		summary.setNbuyMean(nbuyMean);
		summary.setProfit(profit);
		summary.setProfitInPortfoilo(profitInPortfoilo);
		summary.setProfitPercent(profitPercent);
		summary.setProfitInPortfolioPercent(profitInPortfolioPercent);
		summary.setProfitRealised(profitRealised);
		summary.setProfitRealisedPercent(profitRealisedPercent);
		summary.setCurrentInvestedAmount(currentInvestedAmount);
		summary.setProjectedSellValue(projectedSellValue);
		summary.setProjectedLastSellValue(projectedLastSellValue);
		summary.setStablizeCount(stablizeCount);
		summary.setStablizeRate(stablizeRate);
		summary.setCoinName(coinName);
		summary.setTotalVolume(totalVolume);
		summary.setCurrentMarketValue(currentMarketValue);
		

		

		//pw.println(coinName+ ","+totalVolume+","+buyPrice+","+sellPrice+","+currentInvestmentAmt+","+buymean+","+projectedSellValue+","+currentMarketRate+","+currentValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate); 
		pw.println(coinName+ ","+totalVolume+","+currentInvestmentAmt+","+buymean+","+nbuyMean+","+targetPercentage+","+projectedSellValue+","+projectedLastSellValue+","+currentMarketRate+","+currentMarketValue+","+profitRealised+","+profitInPortfoilo+"," +profit+","+profitRealisedPercent+","+ profitInPortfolioPercent +","+profitPercent+","+stablizeCount+","+stablizeRate);
		return summary;
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
				coinName=transactionDetails[COIN_NAME_INDEX];
				coin.setName(coinName);
				coin.setPrice(Double.parseDouble(transactionDetails[TRANS_PRICE_INDEX]));
				coin.setVolume(Double.parseDouble(transactionDetails[TRANS_VOLUME_INDEX]));
				coin.setTransactionType(Integer.parseInt(transactionDetails[TRANS_TYPE_INDEX]));
				coin.setDate(transactionDetails[TRANS_DATE_INDEX]);
				coin.setTransactionCurrency(transactionDetails[TRANS_CURRENCY]);
				coin.setTransactionCoinLiveRate(Double.parseDouble(transactionDetails[COIN_TRANS_CURR_RATE_INDEX]));
				coin.setTransactionLiveRate(Double.parseDouble(transactionDetails[TRANS_LIVERATE_INDEX]));
				coin.setTransactionLiveRateCurrType(transactionDetails[TRANS_LIVERATE_CURRTYPE_INDEX]);
				coin.setTargetINRValueForUSD(Double.parseDouble(transactionDetails[TRANS_INR_RATE_INDEX]));
				

				if(transactionDetails.length > TRANS_COMM_INDEX)
				{
					coin.setCommission(Double.parseDouble(transactionDetails[TRANS_COMM_INDEX]));

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
	public  CoinMeanVO calculateMeanFromTransaction(List<TransactionVO> coinVOList,int lastNTransact){ 
		CoinMeanVO meanVO = new CoinMeanVO(); 
		//Stage 2 : Input for calculating the mean,profit and % 

		double buyPrice = 0; 
		double sellPrice = 0; 
		double buyVolume =0; 
		double sellVolume = 0; 
		double costPrice=0; 
		
		double costPriceInUSD = 0;
		double buyPriceInUSD =0;
		double sellPriceInUSD = 0;
		
		double costPriceInINR = 0;
		double buyPriceInINR =0;
		double sellPriceInINR =0;
		
		double totalNetPrice =0; 
		//Total commission Rate
		double commission = 0;
		//Transaction commission amount Calculation
		double commissionRate =0;
		
		//Buy commissionRate 
		/*double buyCommission =0;
		double sellCommission = 0;*/
		
		
		
		
		//stage 3 : Queue for calculating the last n transaction amount for buy alone
		// This is calculate the latest buy average to calculate exact amount in market
		
		ArrayBlockingQueue<Integer> lastNBuyItem = new ArrayBlockingQueue<Integer>(lastNTransact);
		int position =0 ;
		Map<Integer,CalculatedValueVO> meanValueMap = new HashMap<Integer, CalculatedValueVO>();
		for(TransactionVO coinVO : coinVOList) 
		{ 
			//total amt in tranasaction currency says ex: ETH
			costPrice = coinVO.getPrice() * coinVO.getVolume(); 
			// Says if price of ETH is 650, we need to value 
			//Either in run time and static value from transaction history
			double transCurrAmt= 0;
					
			if (basicMode) {
				transCurrAmt=coinVO.getTransactionCurrencyLiveRate();
				
			}
			else{
				transCurrAmt = getUSDForCrypto(coinVO.getTransactionCurrency());
			}
			double commissionInUSD= 0;
 			double effectiveVolume =0;
			costPriceInUSD = costPrice * transCurrAmt;
			costPriceInINR = costPriceInUSD * coinVO.getTargetINRValueForUSD();
			System.out.println("Cost PRicec "+coinVO.getPrice()+"*"+coinVO.getVolume()+"="+costPrice+coinVO.getTransactionCurrency());
			System.out.println("USD Rate in CP :"+costPriceInUSD);
			System.out.println("INR Rate in CP :"+costPriceInINR);
			System.out.println("INR rate " +coinVO.getTargetINRValueForUSD());
			
			//costPriceInINR = costPriceInUSD * coinVO.getTargetINRValueForUSD();

			
            // 1 indicates it is buy transaction,other are sell transaction
			if(coinVO.getTransactionType() == 1) 
			{ 
				
				commissionRate = coinVO.getVolume() * coinVO.getCommission();
				
				System.out.println("This should not work earlier transaction"+ coinVO.getCommission());
				totalNetPrice = costPrice;
				// System.out.println("Cost Price : "+totalNetPrice); 
				buyPrice += totalNetPrice; 
				buyPriceInINR += costPriceInINR;
				buyPriceInUSD += costPriceInUSD;
				effectiveVolume = coinVO.getVolume()-commissionRate;
				buyVolume+=effectiveVolume; 
				//TODO Need to pass the purchasing coin currency value in Dollar.
				//TODO commission Rate needs to be converted to Dollar
				//buyCommission+=commissionRate;
				if(getLiveRate){
					commissionInUSD = getCommissionInUSD(coinVO.getName());
					commissionRate = commissionRate * commissionInUSD;
					System.out.println("USD commission " +commissionInUSD);
				}
				else{
				commissionRate=commissionRate * coinVO.getTransactionCoinLiveRate();
				}
				CalculatedValueVO calcVO = new CalculatedValueVO();
				calcVO.setCostPrice(costPrice);
				calcVO.setCostPriceInINR(costPriceInINR);
				calcVO.setCostPriceInUSD(costPriceInUSD);
				calcVO.setEffectiveVolume(effectiveVolume);
				
				
				meanValueMap.put(position,calcVO);
				addQueueItem(lastNBuyItem, lastNTransact, position);

			} 
			else 
			{ 
			
				commissionRate = costPrice * coinVO.getCommission();
				
				totalNetPrice = costPrice - commissionRate;
				sellPrice += totalNetPrice; 
				sellPriceInINR += costPriceInINR;
				sellPriceInUSD += costPriceInUSD;
				sellVolume += (coinVO.getVolume()); 
				//TODO Need to pass the outcome currency  value in Dollar.
				//TODO commission Rate needs to be converted to Dollar
				commissionRate=commissionRate * transCurrAmt;
				System.out.println("sell commission rate :"+ commissionRate);
				
			} 
			System.out.println("Commission Amount " +commissionRate);
		commission+=commissionRate;
        position++;
		} 
        //calculate the n transaction amount and volume
		position=-1;
		 double totalNBuyPrice =0;
		 double totalNBuyPriceInUSD =0;
		 double totalNBuyPriceInINR = 0;
		 double totalNBuyVolume = 0;
		 int size = lastNBuyItem.size();
		meanVO.setnBuyCount(size);
		for(int i=0; i<size; i++){
			Integer poll = lastNBuyItem.poll();
			CalculatedValueVO coinVO = meanValueMap.get(poll);
			System.out.println("Polled value :"+poll);




			totalNBuyPrice+=(coinVO.getCostPrice());
			totalNBuyPriceInINR += coinVO.getCostPriceInINR();
			totalNBuyPriceInUSD += coinVO.getCostPriceInUSD();
			totalNBuyVolume += coinVO.getEffectiveVolume();
			System.out.println(totalNBuyPrice);
			System.out.println(totalNBuyVolume);
		}
		
		meanVO.setnBuyPrice(totalNBuyPrice);
		meanVO.setnBuyVolume(totalNBuyVolume);
		
		meanVO.setnBuyPriceInINR(totalNBuyPriceInINR);
		meanVO.setnBuyPriceInUSD(totalNBuyPriceInUSD);
		
		meanVO.setBuyPriceInINR(buyPriceInINR);
		meanVO.setSellPriceInINR(sellPriceInINR);
		
		meanVO.setBuyPriceInUSD(buyPriceInUSD);
		meanVO.setSellPriceInUSD(sellPriceInUSD);
		
		meanVO.setBuyPrice(buyPrice); 
		meanVO.setSellVolume(sellVolume); 
		meanVO.setBuyVolume(buyVolume); 
		meanVO.setSellPrice(sellPrice); 
		meanVO.setCommission(commission);

		return meanVO; 
	}
	private double getCommissionInUSD(String currencyName) {
		double usd_value;
		if(isCryptoCurrency(currencyName))
		{
				usd_value=getUSDForCrypto(currencyName);
		}
		else
		{
				usd_value = getUSDForForeignCurrency("INR");
		}
		return usd_value;
	}
	private double getUSDForForeignCurrency(String foreignCurrency) {
		double value =0;
		// right now coding the value, no way dynamic webservice found till now
 		if(foreignCurrency.equalsIgnoreCase("INR")){
			value = 66.50;
		}
		
		return value;
	}
	private double getUSDForCrypto(String transactionLiveRateCurrType) {
		double value =0;
		//we n
		if(transactionLiveRateCurrType.equalsIgnoreCase("ETH")){
			value = 680;
		}
		else if(transactionLiveRateCurrType.equalsIgnoreCase("BTC")){
			value = 9340;
		}
		return value;
	}
	public boolean isCryptoCurrency(String transactionLiveRateCurrType) {
		
		
		
		return !(NON_CYRPTO_CURRENCY.contains(transactionLiveRateCurrType));
	}

}
