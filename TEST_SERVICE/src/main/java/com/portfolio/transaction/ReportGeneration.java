package com.portfolio.transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.portfolio.dao.CurrencyMean;
import com.portfolio.dao.ExchangeCurrencyVO;
import com.portfolio.dao.TransactionDetailsVO;
import com.portfolio.utilis.ReadTransactionDetails;

public class ReportGeneration {

	public static final BigDecimal ZERO_BIGDECIMAL = new BigDecimal(0);
	private static final int AMOUNT_BASED = 2;
	private static final int VOLUME_BASED = 1;
	Hashtable<String,List<String>> tradeConfig = new Hashtable<String,List<String>>();
	
	public void loadTradeConfig(){
		List<String> binanceList = new ArrayList<String>();
		binanceList.add("ETH");
		binanceList.add("BTC");
		binanceList.add("BNB");
		binanceList.add("USDT");
		tradeConfig.put("BINANCE",binanceList);
		List<String> kionexList = new ArrayList<String>();
		kionexList.add("INR");
		kionexList.add("XRP");
		kionexList.add("BTC");
		kionexList.add("ETH");
		tradeConfig.put("KIONEX", kionexList);
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException{
		
		String transactionFile = "D:/Documents/MultiTransaction_1.csv";
		
		ReadTransactionDetails readTransaction = new ReadTransactionDetails();
		
		List<TransactionDetailsVO> transactionList= readTransaction.getTransactionDetails(transactionFile);
		
		//System.out.println(transactionList);
		ReportGeneration reGeneration = new ReportGeneration();
		reGeneration.loadTradeConfig();
		Map<String,List<ExchangeCurrencyVO>> exchangeList = reGeneration.processTransactionList(transactionList);
		
		//System.out.println(exchangeList);
		int i=0;
		for(List<ExchangeCurrencyVO>  exCurr: exchangeList.values()){
			 
			 for(ExchangeCurrencyVO exVO : exCurr){
				 
				 BigDecimal totalVolume = exVO.getDepositAmt().subtract(exVO.getWithdrawalAmt());
				 BigDecimal totalBuyVolume= ZERO_BIGDECIMAL;
				 BigDecimal totalSellVolume = ZERO_BIGDECIMAL;
				 for(CurrencyMean currMean : exVO.getCurrency())
				 {
					 totalBuyVolume=totalBuyVolume.add(currMean.getBuyVolume());
					 totalSellVolume = totalSellVolume.add(currMean.getSellVolume());
					 
					 totalVolume= totalVolume.add(currMean.getBuyVolume()).subtract(currMean.getSellVolume());
					
					 int compare = currMean.getBuyPrice().compareTo(ZERO_BIGDECIMAL) ;
					 int compare2 = currMean.getBuyVolume().compareTo(ZERO_BIGDECIMAL) ;
					 int compare3 = currMean.getSellPrice().compareTo(ZERO_BIGDECIMAL);
					 
					 
					 BigDecimal buyMean = ( compare ==1 && compare2 ==1 ) ? currMean.getBuyPrice().divide(currMean.getBuyVolume(),8,RoundingMode.HALF_UP): ZERO_BIGDECIMAL;
					 BigDecimal sellMean =   (compare3 == 1 )?   currMean.getSellPrice().divide(currMean.getSellVolume(),8,RoundingMode.HALF_UP) : ZERO_BIGDECIMAL;
					
					 BigDecimal soldInvestement = buyMean.multiply(currMean.getSellVolume()); 
					 BigDecimal currentInvestmentAmt = currMean.getBuyPrice().subtract(soldInvestement);
					
					 
						
					 BigDecimal profitRealised = (currMean.getSellVolume().multiply(sellMean)).subtract(soldInvestement);
					 
					 
					 BigDecimal profitPercentage = (compare==1) ?profitRealised.divide(currMean.getBuyPrice(),2,RoundingMode.HALF_UP).multiply(new BigDecimal(100)):ZERO_BIGDECIMAL;
							 
					 /*System.out.println("Buy Price "+currMean.getBuyPrice());
					 System.out.println("Buy Volume "+currMean.getBuyVolume());
					 System.out.println("Buy Mean Value : "+ buyMean);
					 System.out.println("Sell Price : "+currMean.getSellPrice());
					 System.out.println("Sell volume : "+ currMean.getSellVolume());
					 System.out.println("Sell Mean :"+ sellMean);
					 System.out.println("Sold Investment : "+soldInvestement); 
					 System.out.println("CurrentInvestment : " +currentInvestmentAmt);
					 System.out.println("Profit Realised : "+profitRealised);*/
					 
					 System.out.println();
					 System.out.println("Coin Name/Currency,Total Volume,CurrentInvestment,Avg Price of Buy,profit Realised");
					 System.out.println(exVO.getCoinName()+"/"+currMean.getCurrencyName()+ ","+totalVolume+","+currentInvestmentAmt+","+buyMean+","+profitRealised+","+profitPercentage+"%");
					
				 }
				 
				 System.out.println(i+" exchange " + exVO.getExchangeName()+ " => "+exVO.getCoinName()+"Total vol ===> "+totalVolume+"Transaction Amt ======>"+exVO.getTotalAmt()+"Deposit Amt=======>"+exVO.getDepositAmt());
				 i++;
				 //System.out.println();
			 }
		}
		
		
	}
	
	public Map<String,List<ExchangeCurrencyVO>> processTransactionList(List<TransactionDetailsVO> transactionList)
	{
		Map<String,List<ExchangeCurrencyVO>> exchangeList = new TreeMap<String, List<ExchangeCurrencyVO>>();
		System.out.println("Coin \t Currency \t Trade Currency Amt \t Transaction Amt \t Transaction Type \t Total Transaction amt ");
		for(TransactionDetailsVO detailsVO : transactionList){
			
			if(detailsVO != null){
			String exchangeName=detailsVO.getExchangeName();
			String coinName = detailsVO.getCoinName();
			BigDecimal price = detailsVO.getPrice();
			BigDecimal volume = detailsVO.getVolume();
			BigDecimal commissionRate = detailsVO.getCommissionRate();
			String currency = detailsVO.getCurrency();
			CurrencyMean currencyMean = null;
			ExchangeCurrencyVO xChangeCurrency = null;
			ExchangeCurrencyVO tradeCurrency = null;
			if(exchangeList.containsKey(exchangeName))
			{
				List<ExchangeCurrencyVO> currencyList= exchangeList.get(exchangeName);
				xChangeCurrency = getXChangeCurrency(currencyList,coinName);
				
				if(xChangeCurrency == null){
					 xChangeCurrency = new ExchangeCurrencyVO();
					 //System.out.println("Coin Not Found : "+ coinName);
					//Since it is reference
						currencyList.add(xChangeCurrency);
				}
				
				
				currencyMean = getCurrencyMeanFromExchange(currency,xChangeCurrency.getCurrency());
				if(currencyMean == null)
				{
					
					xChangeCurrency.setCoinName(coinName);
					xChangeCurrency.setExchangeName(exchangeName);
					if (!coinName.equalsIgnoreCase(currency)) 
					{
						currencyMean = createNewCurrecnyMean(currency);
						xChangeCurrency.addCurrencyVO(currencyMean);
					}
					if(isTradeCurrency(exchangeName,coinName)){
						xChangeCurrency.setTradeCurrency(true);
					}
					
					
				}
				tradeCurrency = getXChangeCurrency(currencyList, currency);
				if(tradeCurrency == null){
					tradeCurrency = new ExchangeCurrencyVO();
					//System.out.println("Added in trade Mean");
					//tradeMean = createNewCurrecnyMean(currency);
					tradeCurrency.setCoinName(currency);
					tradeCurrency.setExchangeName(exchangeName);
					//tradeCurrency.addCurrencyVO(tradeMean);
					if(isTradeCurrency(exchangeName,coinName)){
						tradeCurrency.setTradeCurrency(true);
					}
					//Since it is reference
					currencyList.add(tradeCurrency);
				}
			}
			else
			{
				
				System.out.println("New Exchange Name :"+ exchangeName);
				xChangeCurrency = new ExchangeCurrencyVO();
				xChangeCurrency.setCoinName(coinName);
				xChangeCurrency.setExchangeName(exchangeName);
				if (!coinName.equalsIgnoreCase(currency)) {
					currencyMean = createNewCurrecnyMean(currency);
					xChangeCurrency.addCurrencyVO(currencyMean);
				}
				if(isTradeCurrency(exchangeName,coinName)){
					xChangeCurrency.setTradeCurrency(true);
				}
				
				List<ExchangeCurrencyVO> exList = new ArrayList<ExchangeCurrencyVO>();
				exList.add(xChangeCurrency);
				exchangeList.put(exchangeName,exList);
				
				
				tradeCurrency = getXChangeCurrency(exList, currency);
				if(tradeCurrency == null){
				tradeCurrency = new ExchangeCurrencyVO();
				//tradeMean = createNewCurrecnyMean(currency);
				tradeCurrency.setCoinName(currency);
				tradeCurrency.setExchangeName(exchangeName);
				//tradeCurrency.addCurrencyVO(tradeMean);
				if(isTradeCurrency(exchangeName,coinName)){
					tradeCurrency.setTradeCurrency(true);
				}
				//Since it is reference
				exList.add(tradeCurrency);
				}
				
				
			}
			
			//total Amount 
			BigDecimal transactionAmt=price.multiply(volume);;
			BigDecimal buyPrice;
			BigDecimal sellPrice;
			BigDecimal buyVolume;
			BigDecimal sellVolume;
			BigDecimal totalCommissionSpend;
			if (currencyMean != null) {
				
				buyPrice = currencyMean.getBuyPrice();
				sellPrice = currencyMean.getSellPrice();
				buyVolume = currencyMean.getBuyVolume();
				sellVolume = currencyMean.getSellVolume();
				totalCommissionSpend = currencyMean.getCommissionRate();
			}
			else{
				buyPrice = ZERO_BIGDECIMAL;
				sellPrice = ZERO_BIGDECIMAL;
				buyVolume = ZERO_BIGDECIMAL;
				sellVolume = ZERO_BIGDECIMAL;
				totalCommissionSpend = ZERO_BIGDECIMAL;
			}
			BigDecimal depositAmt = xChangeCurrency.getDepositAmt();
			BigDecimal withdrawAmt = xChangeCurrency.getWithdrawalAmt();
			BigDecimal totalAmt = xChangeCurrency.getTotalAmt();
			BigDecimal tradeCurrencyAmt = tradeCurrency.getTotalAmt();
			
			BigDecimal commissionValue = null;
			BigDecimal effectiveVolume = null;
			System.out.println();
			System.out.print(coinName);
			System.out.print("\t"+ currency);
			/*System.out.println("Total Amt" + buyPrice);
			System.out.println("Total Volume "+buyVolume);
			System.out.println("Total CommissionSepnd"+ totalCommissionSpend);*/
			System.out.print("\t"+ tradeCurrencyAmt);
			//System.out.println("*********************************Calculation Part*********");
			System.out.print("\t"+transactionAmt+"\t");
			// Buy Transaction
			if(detailsVO.getTransactionType() == 1){
				if(detailsVO.getCommissionType() == AMOUNT_BASED)
				{
					commissionValue = transactionAmt.multiply(commissionRate);
					effectiveVolume = volume;
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt.add(commissionValue));
				}
				else if(detailsVO.getCommissionType() == VOLUME_BASED)
				{
					commissionValue = volume.multiply(price).multiply(commissionRate);
					effectiveVolume = volume.subtract(volume.multiply(commissionRate));
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt);
				}
				/*System.out.println("Actual Volume :"+volume);
				System.out.println("Effective Volume :"+effectiveVolume);*/
				
				buyVolume=buyVolume.add(effectiveVolume);
				totalAmt=totalAmt.add(effectiveVolume);
				buyPrice=buyPrice.add(transactionAmt);
				totalCommissionSpend = totalCommissionSpend.add(commissionValue);
				System.out.print("BUY ORDER");
				
			}
			//sell Transaction
			else if(detailsVO.getTransactionType() ==2 ){
				if(detailsVO.getCommissionType() == AMOUNT_BASED)
				{
					commissionValue = transactionAmt.multiply(commissionRate);
					effectiveVolume = volume;
				}
				else if(detailsVO.getCommissionType() == VOLUME_BASED)
				{
					commissionValue = transactionAmt.multiply(commissionRate);
					effectiveVolume = volume;
				}
				tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt.subtract(commissionValue));
				sellVolume=sellVolume.add(effectiveVolume);
				sellPrice= transactionAmt.add(sellPrice);
				totalAmt = totalAmt.subtract(effectiveVolume);
				totalCommissionSpend = totalCommissionSpend.add(commissionValue);
				System.out.print("SELL ORDER");
			}
			//Deposit Transaction
			else if(detailsVO.getTransactionType() == 3){
				effectiveVolume = volume.subtract(commissionRate);
				depositAmt=depositAmt.add(effectiveVolume);
				totalAmt = totalAmt.add(effectiveVolume);
				tradeCurrencyAmt=tradeCurrencyAmt.add(effectiveVolume);
				System.out.print("DEPOSIT ORDER");
				//TODO commission for Deposit Rate needs to be calculated 
			}
			//withdrawal Transaction
			else if(detailsVO.getTransactionType() == 4){
				effectiveVolume = volume.subtract(commissionRate);
				withdrawAmt=withdrawAmt.add(volume);
				totalAmt = totalAmt.subtract(effectiveVolume);
				tradeCurrencyAmt=tradeCurrencyAmt.subtract(effectiveVolume);
				System.out.print("WITHDRAWAL ORDER");
				//TODO commission for Withdrawal Rate needs to be calculated
			}
			System.out.print("\t"+tradeCurrencyAmt);
			
			if (currencyMean != null) {
				/*System.out.println("Commission value :"+commissionValue);
						System.out.println("After calculation");
						System.out.println("Coin Name :"+coinName);
						System.out.println("Total Amt" + buyPrice);
				
						System.out.println("Buy Volume "+buyVolume);
						System.out.println("Sell Volume "+sellVolume);
						
						System.out.println("Deposit volume" + depositAmt);
						System.out.println("Withdrawal Volume : " + withdrawAmt);
						System.out.println("Total CommissionSepnd : "+ totalCommissionSpend);
						System.out.println("===================================================================");*/
				currencyMean.setCommissionRate(totalCommissionSpend);
				currencyMean.setBuyPrice(buyPrice);
				currencyMean.setSellPrice(sellPrice);
				currencyMean.setBuyVolume(buyVolume);
				currencyMean.setSellVolume(sellVolume);
			}
			xChangeCurrency.setDepositAmt(depositAmt);
			xChangeCurrency.setWithdrawalAmt(withdrawAmt);
			xChangeCurrency.setTotalAmt(totalAmt);
			tradeCurrency.setTotalAmt(tradeCurrencyAmt);
			//System.out.println("***********************************************************");
			}
			
		}
		
		return exchangeList;
	}

	private boolean isTradeCurrency(String exchangeName, String coinName) {
	   if(tradeConfig.containsKey(exchangeName)){
		   if(tradeConfig.get(exchangeName).contains(coinName))
		   {
			   return true;
		   }
	   }
		return false;
	}

	private CurrencyMean createNewCurrecnyMean(String currency) {
		
		CurrencyMean xChangeCurrency = new CurrencyMean();
		xChangeCurrency.setCommissionRate(new BigDecimal("0"));
		xChangeCurrency.setBuyVolume(new BigDecimal("0"));
		xChangeCurrency.setSellVolume(new BigDecimal("0"));
		xChangeCurrency.setBuyPrice(new BigDecimal("0"));
		xChangeCurrency.setSellPrice(new BigDecimal("0"));
		xChangeCurrency.setCurrencyName(currency);
		
		return xChangeCurrency;
	}

	private CurrencyMean getXChangeCurrency(List<ExchangeCurrencyVO> currencyList, String coin,String currency) {
		for(ExchangeCurrencyVO currencyVO: currencyList){
			if(currencyVO!= null){
				if(currencyVO.getCoinName().equalsIgnoreCase(coin)){
					return getCurrencyMeanFromExchange(currency, currencyVO.getCurrency());
				}
			}
		}
		return null;
	}

	private ExchangeCurrencyVO getXChangeCurrency(List<ExchangeCurrencyVO> currencyList, String coin) {
		for(ExchangeCurrencyVO currencyVO: currencyList){
			if(currencyVO!= null){
				if(currencyVO.getCoinName().equalsIgnoreCase(coin)){
					return currencyVO;
				}
			}
		}
		return null;
	}
	private CurrencyMean getCurrencyMeanFromExchange(String currency,List<CurrencyMean> currencyList) {
		for(CurrencyMean currencyMean : currencyList){
			if(currencyMean.getCurrencyName().equalsIgnoreCase(currency)){
				return currencyMean;
			}
		}
		return null;
	}
	
}
