package com.portfolio.transaction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.portfolio.dao.CurrencyMean;
import com.portfolio.dao.ExchangeCurrencyVO;
import com.portfolio.dao.ExchangeVO;
import com.portfolio.dao.InvestmentConversion;
import com.portfolio.dao.SummaryVO;
import com.portfolio.dao.TransactionCurrency;
import com.portfolio.dao.TransactionDetailsVO;
import com.portfolio.utilis.GetCurrentRate;
import com.portfolio.utilis.ReadTradeConfig;
import com.portfolio.utilis.ReadTransactionDetails;

public class ReportGeneration {

	private static final BigDecimal BIG_DECIMAL_100 = new BigDecimal(100);
	private static final String CSV_DELIMITER = ",";
	public static final BigDecimal ZERO_BIGDECIMAL = new BigDecimal(0);
	private static final int AMOUNT_BASED = 2;
	private static final int VOLUME_BASED = 1;
	
	ReadTradeConfig tradeConfiguration= new ReadTradeConfig();
	
	
	/*Hashtable<String,List<String>> tradeConfig = new Hashtable<String,List<String>>();*/
	List<String> tradedSymbol= new ArrayList<String>();
	
	public void loadTradeConfig(String configurationFile) throws Exception{
		tradeConfiguration.getConfigurationDetails(configurationFile);
		//tradeConfiguration.setUseProxy(true);
		tradeConfiguration.setProxyDetails("10.121.11.32", "8080", "hcltech\\natarajan_g", "Gopalfx@54");
		ReadTradeConfig.excludedCoinList.add("VEN");
		
	}
	
	public static void main(String[] args) throws Exception{
		
		
		//System.out.println(transactionList);
		
		
		try {
			String transactionFile = "C:/Documents/MultiTransaction_1.csv";
			String configurationFile = "C:/Documents/config.txt";
			
			ReportGeneration reGeneration = new ReportGeneration();
			reGeneration.loadTradeConfig(configurationFile);
			ReadTransactionDetails readTransaction = new ReadTransactionDetails();
			String filterExchange = null;
			String coinName = null;
			String tradeCurrency = null;
			List<TransactionDetailsVO> transactionList = readTransaction.getTransactionDetails(transactionFile,
					filterExchange, coinName, tradeCurrency);
			Map<String, ExchangeVO> exchangeList = reGeneration.processTransactions(transactionList);
			//System.out.println(exchangeList);
			SummaryVO summaryVO= reGeneration.calculateMeanValue(exchangeList);
			//reGeneration.calculateCurrentValue(exchangeList);
			File summaryJson = new File("C:/Documents/summaryJson.json");
			FileUtils.write(summaryJson,reGeneration.summaryDataToJson(summaryVO));
			PrintWriter pw = new PrintWriter("C:/Documents/newReport.csv");
			reGeneration.printSummary(pw, summaryVO);
			
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	private String summaryDataToJson(SummaryVO summaryVO) {
		
		JSONObject summaryJson = new JSONObject(summaryVO);
		
		
		return summaryJson.toString(4);
		
	}
	

	private  void printSummary(PrintWriter pw, SummaryVO summaryVO) throws Exception{
		
		
		
		for(ExchangeVO exchangeVO : summaryVO.getExchangeList().values()) {
			
			System.out.println("Traded Pair : "+exchangeVO.getTradeCoinList());
		 
			pw.println("Coin/TradeCurrency,Buy Price,Buy Volume,Buy Mean,Sell Price,Sell Volume,Sell Mean,Commission Rate, Profit Realised on Vol, Profit %,Deposit Vol,Withdrawal Vol,Commission Rate,Total Volume,Current Market Value");
			for (ExchangeCurrencyVO exVO : exchangeVO.getCoinList()) {
				
				for (CurrencyMean currMean : exVO.getCurrency()) {
					pw.println(exVO.getCoinName() + "/" + currMean.getCurrencyName() + CSV_DELIMITER + currMean.getBuyPrice()+
							CSV_DELIMITER + currMean.getBuyVolume()+ CSV_DELIMITER + currMean.getBuyMean() + CSV_DELIMITER  +  currMean.getSellPrice()+CSV_DELIMITER + currMean.getSellVolume()+ CSV_DELIMITER + currMean.getSellMean() + CSV_DELIMITER+currMean.getCommissionRate() + " ,"+ currMean.getProfitRealised() + CSV_DELIMITER
							+ currMean.getProfitRealPercentage() + "%");
				}
				
				pw.println(exVO.getCoinName()+",,,,,,,,,," + exVO.getDepositAmt()+CSV_DELIMITER+exVO.getWithdrawalAmt()+CSV_DELIMITER+exVO.getXchangeTransferCommission()+CSV_DELIMITER +exVO.getTotalAmt()+CSV_DELIMITER+exVO.getCurrentMarketValue());
				pw.println(",,,,,");
			}
			pw.println(",,,,,,,,,,,,,TotalCurrent Market Value,"+exchangeVO.getCurrentMarketValue());
			pw.println("Coin Transaction Summary,,,,Crypto,Coin Name,Total Buy,Total sell,Total Commission Amt,Total Current Investment,Deposit Amt,Withdrawl Amt,Overall Percentage");
			for(TransactionCurrency transVO: exchangeVO.getTransCurrency()) {	
				
				pw.println(",,,,"+tradeConfiguration.isCryptoCurrency(transVO.getName())+CSV_DELIMITER+transVO.getName()+CSV_DELIMITER+transVO.getBuyTransactionAmount()+CSV_DELIMITER+transVO.getSellTransactionAmount()+CSV_DELIMITER+transVO.getCommissionAmount()+CSV_DELIMITER+transVO.getCurrentInvestAmount()+CSV_DELIMITER+transVO.getDepositVolume()+CSV_DELIMITER +transVO.getWithdrawalVolume() +CSV_DELIMITER+transVO.getOverallGainPercent());
			}
			pw.println(",,,,,");
			
			pw.println(",,,,,Non cryptoInvest Value ,"+exchangeVO.getNonCryptoInvestValue());
		
			
		}
		pw.println(",,,,,");
		pw.println("Summary");
		pw.println(",,,,,Currency,Total Market value,Total Investment value");
		for (InvestmentConversion currency : summaryVO.getConversionList()) {
			pw.println(",,,,, "+currency.getCurrency()+CSV_DELIMITER + currency.getCurrentMarketValue() + CSV_DELIMITER+ currency.getInvestmentAmt());
		}
		pw.println(",,,,,Profit %, " +summaryVO.getProfit());
	}

	private static TransactionCurrency getTransactionCurrency(List<TransactionCurrency> transactionCurrList, String currencyName) {
		for(TransactionCurrency currencyVO: transactionCurrList){
			if(currencyVO!= null){
				if(currencyVO.getName().equalsIgnoreCase(currencyName)){
					return currencyVO;
				}
			}
		}
		return null;
	}

	private  SummaryVO calculateMeanValue(Map<String, ExchangeVO> exchangeList) {
		Map<String,BigDecimal> currentMap= null;
		Map<String,BigDecimal> nonCryptoCurrencyValue = null;
		//get the current MarketValue from CoinMarketCap site
		try 
		{
			currentMap = GetCurrentRate.getCurrentPriceFromCoinMarketCap(tradedSymbol);
			nonCryptoCurrencyValue = GetCurrentRate.getNonCryptoCurrencyValue("USD","MXN,INR");
			
		} 
		catch (Exception e)
		{
			System.out.println("No Internet Problem" + e.getMessage());
		}
		int i=0;
		   
		SummaryVO summaryVO = new SummaryVO();
			for (ExchangeVO exchangeVO : exchangeList.values())
			{
				BigDecimal nonCryptoInvAmt=ZERO_BIGDECIMAL;
				BigDecimal totalAmt = ZERO_BIGDECIMAL;
				for (ExchangeCurrencyVO exVO : exchangeVO.getCoinList()) 
				{

					BigDecimal totalBuyVolume = ZERO_BIGDECIMAL;
					BigDecimal totalSellVolume = ZERO_BIGDECIMAL;
					for (CurrencyMean currMean : exVO.getCurrency()) 
					{
						totalBuyVolume = totalBuyVolume.add(currMean.getBuyVolume());
						totalSellVolume = totalSellVolume.add(currMean.getSellVolume());
						
						 TransactionCurrency transVO = getTransactionCurrency(exchangeVO.getTransCurrency(),currMean.getCurrencyName());
						 
						 if(transVO== null) {
							 transVO = new TransactionCurrency();
							 transVO.setExchange(exVO.getExchangeName());
							 transVO.setName(currMean.getCurrencyName());
							 transVO.setCrypto(tradeConfiguration.isCryptoCurrency(currMean.getCurrencyName()));	
							 exchangeVO.addTransactionCurrencyVO(transVO);
						 }
						 
						int compare = currMean.getBuyPrice().compareTo(ZERO_BIGDECIMAL);
						int compare2 = currMean.getBuyVolume().compareTo(ZERO_BIGDECIMAL);
						int compare3 = currMean.getSellPrice().compareTo(ZERO_BIGDECIMAL);

						BigDecimal buyMean = (compare == 1 && compare2 == 1)
								? currMean.getBuyPrice().divide(currMean.getBuyVolume(), 8, RoundingMode.HALF_UP)
								: ZERO_BIGDECIMAL;
						BigDecimal sellMean = (compare3 == 1)
								? currMean.getSellPrice().divide(currMean.getSellVolume(), 8, RoundingMode.HALF_UP)
								: ZERO_BIGDECIMAL;
                        //Amount sold volume from investment, Buy avg price * sold volume
						BigDecimal soldInvestement = buyMean.multiply(currMean.getSellVolume());
						//Current invested Amount
						BigDecimal currentInvestmentAmt = currMean.getBuyPrice().subtract(soldInvestement);
						// Profit Realised 
						BigDecimal profitRealised = (currMean.getSellVolume().multiply(sellMean))
								.subtract(soldInvestement);

						BigDecimal profitPercentage = (compare == 1) ? profitRealised
								.divide(currMean.getBuyPrice(), 4, RoundingMode.HALF_UP).multiply(BIG_DECIMAL_100)
								: ZERO_BIGDECIMAL;

						transVO.setBuyTransactionAmount(transVO.getBuyTransactionAmount().add(currMean.getBuyPrice()));
						transVO.setSellTransactionAmount(transVO.getSellTransactionAmount().add(currMean.getSellPrice()));
						transVO.setCommissionAmount(transVO.getCommissionAmount().add(currMean.getCommissionRate()));
						transVO.setCurrentInvestAmount(transVO.getCurrentInvestAmount().add(currentInvestmentAmt));
						transVO.setSoldInvestment(transVO.getSoldInvestment().add(soldInvestement));
						currMean.setBuyMean(buyMean.setScale(8, RoundingMode.HALF_UP));
						currMean.setSellMean(sellMean.setScale(8, RoundingMode.HALF_UP));
						currMean.setSoldInvestment(soldInvestement.setScale(8, RoundingMode.HALF_UP));
						currMean.setCurrentInvestment(currentInvestmentAmt.setScale(8, RoundingMode.HALF_UP));
						currMean.setProfitRealised(profitRealised.setScale(8, RoundingMode.HALF_UP));
						currMean.setProfitRealPercentage(profitPercentage);
						System.out.println();
					}
					
					BigDecimal currentValue=ZERO_BIGDECIMAL;
					if (currentMap!= null) 
					{
						BigDecimal marketValue = currentMap.get(exVO.getCoinName());
						if (tradeConfiguration.isCryptoCurrency(exVO.getCoinName()) && marketValue != null)
						{
							currentValue = exVO.getTotalAmt().multiply(marketValue);
							totalAmt= totalAmt.add(currentValue);
							exVO.setCurrentMarketValue(currentValue.setScale(2, RoundingMode.HALF_UP));
						} 
						else
						{
							
							if(nonCryptoCurrencyValue!= null)
							{
							currentValue= nonCryptoCurrencyValue.get(exVO.getCoinName()) ;
							if(currentValue!= null) {
							System.out.println(exVO.getCoinName()+" Current Value : "+ currentValue);
							nonCryptoInvAmt= nonCryptoInvAmt.add(exVO.getDepositAmt().divide(currentValue,2, RoundingMode.HALF_UP));
							}
						}
						}
						
					}
					/*System.out.println("\n" + i + " exchange " + exVO.getExchangeName() + " => " + exVO.getCoinName()
							+ " Total vol ===> " + totalVolume + " Transaction Amt ======>" + exVO.getTotalAmt()
							+ " Deposit Amt=======>" + exVO.getDepositAmt()+"======>" +exVO.getCurrentMarketValue());*/
					System.out.println("\n" + i + " exchange " + exVO.getExchangeName() + " => " + exVO.getCoinName()
					+ " Transaction Amt ======>" + exVO.getTotalAmt()
					+ " Deposit Amt=======>" + exVO.getDepositAmt()+"======>" +exVO.getCurrentMarketValue());
					i++;
					//System.out.println();
				} 
				for(TransactionCurrency transVO: exchangeVO.getTransCurrency()) {
					int compare1 =transVO.getBuyTransactionAmount().compareTo(ZERO_BIGDECIMAL);
					int compare2 =transVO.getSellTransactionAmount().compareTo(ZERO_BIGDECIMAL);
					//BigDecimal overall= (compare1 == 1 && compare2 == 1)? (transVO.getSellTransactionAmount().add(transVO.getCommissionAmount()).subtract(transVO.getBuyTransactionAmount().subtract(transVO.getCurrentInvestAmount()))).divide(transVO.getBuyTransactionAmount().subtract(transVO.getCurrentInvestAmount()), 8, RoundingMode.HALF_UP): ZERO_BIGDECIMAL;
					BigDecimal overall= (compare1 == 1 && compare2 == 1)? (transVO.getSellTransactionAmount().add(transVO.getCommissionAmount()).subtract(transVO.getSoldInvestment())).divide(transVO.getSoldInvestment(), 8, RoundingMode.HALF_UP): ZERO_BIGDECIMAL;
					overall= overall.multiply(BIG_DECIMAL_100);
					transVO.setOverallGainPercent(overall);
					
				}
				totalAmt= totalAmt.setScale(2, RoundingMode.HALF_UP);
				exchangeVO.setNonCryptoInvestValue(nonCryptoInvAmt);
				exchangeVO.setCurrentMarketValue(totalAmt);
				
				summaryVO.setNonCryptoInvstAmt(summaryVO.getNonCryptoInvstAmt().add(nonCryptoInvAmt));
				summaryVO.setCurrentMarketValue(summaryVO.getCurrentMarketValue().add(totalAmt));
		
			}
			summaryVO.setExchangeList(exchangeList);
			int compare = summaryVO.getNonCryptoInvstAmt().compareTo(ZERO_BIGDECIMAL);
			if(compare== 1) {
			summaryVO.setProfit((summaryVO.getCurrentMarketValue().subtract(summaryVO.getNonCryptoInvstAmt())).divide(summaryVO.getNonCryptoInvstAmt(),2,RoundingMode.HALF_UP).multiply(BIG_DECIMAL_100));
			}
			else {
				summaryVO.setProfit(ZERO_BIGDECIMAL);
			}
			InvestmentConversion con= new InvestmentConversion();			
			con.setCurrentMarketValue(summaryVO.getCurrentMarketValue());
			con.setInvestmentAmt(summaryVO.getNonCryptoInvstAmt());
			con.setCurrency("USD");
			summaryVO.addInvestmentConversion(con);
			if (nonCryptoCurrencyValue!= null) {
				for (String currency : nonCryptoCurrencyValue.keySet()) {
					BigDecimal currecnyValue = nonCryptoCurrencyValue.get(currency);
					if (currecnyValue != null) {
						 con= new InvestmentConversion();
						con.setCurrentMarketValue(summaryVO.getCurrentMarketValue().multiply(currecnyValue).setScale(2, RoundingMode.HALF_UP));
						con.setInvestmentAmt(summaryVO.getNonCryptoInvstAmt().multiply(currecnyValue).setScale(2, RoundingMode.HALF_UP));
						con.setCurrency(currency);
						summaryVO.addInvestmentConversion(con);
						}

				} 
			}
			return summaryVO;
			
			
	}
	
	

	public Map<String,ExchangeVO> processTransactions(List<TransactionDetailsVO> transactionList)
	{
		Map<String,ExchangeVO> exchangeList = new TreeMap<String,ExchangeVO>();
		System.out.println("Coin \t Currency \t Trade Currency Amt \t Transaction Amt \t Transaction Type \t Total Transaction amt ");
		for(TransactionDetailsVO detailsVO : transactionList){
			
			if(detailsVO != null){
			String exchangeName=detailsVO.getExchangeName();
			String coinName = detailsVO.getCoinName();
			if(!tradedSymbol.contains(coinName)&& tradeConfiguration.isCryptoCurrency(coinName)){
				tradedSymbol.add(coinName);
			}
			BigDecimal price = detailsVO.getPrice();
			BigDecimal volume = detailsVO.getVolume();
			BigDecimal commissionRate = detailsVO.getCommissionRate();
			String currency = detailsVO.getCurrency();
			String commissionCurrency= detailsVO.getCommissionCurrency();
			CurrencyMean currencyMean = null;
			ExchangeCurrencyVO xChangeCurrency = null;
			ExchangeCurrencyVO tradeCurrency = null;
			ExchangeCurrencyVO tradeCommission = null;
			
			String coinPair = coinName+"/"+currency;
			if(exchangeList.containsKey(exchangeName))
			{
				ExchangeVO exchangeVO = exchangeList.get(exchangeName);
				List<ExchangeCurrencyVO> currencyList= exchangeVO.getCoinList();
				//checking the coin is available in the exchange
				xChangeCurrency = getXChangeCurrency(currencyList,coinName);
				//if coin is not available
				if(xChangeCurrency == null){
					 xChangeCurrency = new ExchangeCurrencyVO();
					 //System.out.println("Coin Not Found : "+ coinName);
					xChangeCurrency.setCoinName(coinName);
					xChangeCurrency.setExchangeName(exchangeName);
					//Since it is reference
					currencyList.add(xChangeCurrency);
				}
				
				//check for Transaction Currency is available 
				currencyMean = getCurrencyMeanFromExchange(currency,xChangeCurrency.getCurrency());
				if(currencyMean == null)
				{
					// in case of deposit and withdrawal Coin name and transaction currency will be same
					//so performing this check here
					if (!coinName.equalsIgnoreCase(currency)) 
					{
						currencyMean = createNewCurrecnyMean(currency);
						xChangeCurrency.addCurrencyVO(currencyMean);
						if(!exchangeVO.getTradeCoinList().contains(coinPair)) {
							exchangeVO.addTradeCoinList(coinPair);
						}
					}
					if(tradeConfiguration.isTradeCurrency(exchangeName,coinName)){
						xChangeCurrency.setTradeCurrency(true);
					}
					
					
				}
				tradeCurrency = getXChangeCurrency(currencyList, currency);
				if(tradeCurrency == null){
					tradeCurrency = buildCurrencyInXChange(exchangeName, currency, currencyList);
				}
				if(commissionCurrency != null  && !currency.equalsIgnoreCase(commissionCurrency)) {
					tradeCommission=getXChangeCurrency(currencyList, commissionCurrency );
					if(tradeCommission == null) {
						tradeCommission = buildCurrencyInXChange(exchangeName, commissionCurrency, currencyList);
					}
				}
				
				
			}
			else
			{
				
				System.out.println("\nNew Exchange Name :"+ exchangeName);
				ExchangeVO exchangeVO= new ExchangeVO();
				
				xChangeCurrency = new ExchangeCurrencyVO();
				xChangeCurrency.setCoinName(coinName);
				xChangeCurrency.setExchangeName(exchangeName);
				List<String> tradeCoinList= new ArrayList<String>();
				List<ExchangeCurrencyVO> exList = new ArrayList<ExchangeCurrencyVO>();
				List<TransactionCurrency> transList = new ArrayList<TransactionCurrency>();
				if (!coinName.equalsIgnoreCase(currency)) {
					currencyMean = createNewCurrecnyMean(currency);
					xChangeCurrency.addCurrencyVO(currencyMean);
					tradeCoinList.add(coinPair);
				}
				if(tradeConfiguration.isTradeCurrency(exchangeName,coinName)){
					xChangeCurrency.setTradeCurrency(true);
				}
				
				
				exList.add(xChangeCurrency);
				
				exchangeVO.setCoinList(exList);
				exchangeVO.setTransCurrency(transList);
				
				
				exchangeVO.setTradeCoinList(tradeCoinList);
				
				exchangeList.put(exchangeName,exchangeVO);
				
				
				tradeCurrency = getXChangeCurrency(exList, currency);
				if(tradeCurrency == null){
				tradeCurrency = buildCurrencyInXChange(exchangeName, currency, exList);
				}
				if(commissionCurrency!= null  && !currency.equalsIgnoreCase(commissionCurrency)) {
					
					if(tradeCommission == null) {
						tradeCommission = buildCurrencyInXChange(exchangeName, commissionCurrency, exList);
					}
				}
				
			}
			
			//total Amount 
			BigDecimal transactionAmt=price.multiply(volume);
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
			
			BigDecimal xChangeTransferRate = xChangeCurrency.getXchangeTransferCommission();
			
			BigDecimal commissionValue = null;
			BigDecimal effectiveVolume = null;
			System.out.println();
			System.out.print(coinName);
			System.out.print("\t"+ currency);
			/*System.out.println("Total Amt" + buyPrice);
			System.out.println("Total Volume "+buyVolume);
			System.out.println("Total CommissionSepnd"+ totalCommissionSpend);*/
			System.out.print(" Trans curr = "+ tradeCurrencyAmt+"\t");
			//System.out.println("*********************************Calculation Part*********");
			
			System.out.print(" Bef Vol = "+totalAmt+"\t");
			System.out.print(" Tra Vol = "+volume+"\t");
			// Buy Transaction and Fixed Buy Transaction
			if(detailsVO.getTransactionType() == 1 || detailsVO.getTransactionType() ==5 )
			{
				if(detailsVO.getTransactionType() == 5) {
					transactionAmt = detailsVO.getTotalTransactionAmt();
				}
				System.out.print(" Trans Amt = "+transactionAmt+"\t");
				//purchasing coin volume does not get affected in this method of transaction 
				effectiveVolume = volume;
				
				if(detailsVO.getCommissionType() == AMOUNT_BASED)
				{
					//Transaction  coin as Commission value
					commissionValue = transactionAmt.multiply(commissionRate);
					//Adding the commission value
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt.add(commissionValue));
					totalCommissionSpend = totalCommissionSpend.add(commissionValue);
					//System.out.println("effectiveVolume : "+effectiveVolume);
					System.out.print("BUY ORDER");
					System.out.print("\t"+commissionValue);
				}
				else if(detailsVO.getCommissionType() == VOLUME_BASED)
				{
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt);
					//purchase coin as commission value
					commissionValue = volume.multiply(price).multiply(commissionRate);
					//purchasing coin volume get affected in this method of transaction 
					effectiveVolume = volume.subtract(volume.multiply(commissionRate));
					System.out.print("Eff vol : "+effectiveVolume);
					totalCommissionSpend = totalCommissionSpend.add(commissionValue);
					//System.out.println("effectiveVolume : "+effectiveVolume);
					System.out.print("BUY ORDER");
					System.out.print("\t comission value ="+" ");
				}
				else if(detailsVO.getCommissionType() == 3 || detailsVO.getCommissionType() == 5) {		
					//transaction coin as commission , but commission is fixed amount
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt);
					totalCommissionSpend = totalCommissionSpend.add(commissionRate);
					System.out.print("BUY ORDER");
					System.out.print("\t"+commissionRate);
					
				}
				else if(detailsVO.getCommissionType() == 4 ) {				
					//Commission other than transaction,purchasing coin , but commission is fixed amount
					tradeCommission.setTotalAmt(tradeCommission.getTotalAmt().subtract(commissionRate));
					tradeCurrencyAmt=tradeCurrencyAmt.subtract(transactionAmt);
					System.out.print("BUY ORDER");
					System.out.print("\t"+commissionRate);
				}
				
				/*System.out.println("Actual Volume :"+volume);
				System.out.println("Effective Volume :"+effectiveVolume);*/
				
				buyVolume=buyVolume.add(effectiveVolume);
				totalAmt=totalAmt.add(effectiveVolume);
				buyPrice=buyPrice.add(transactionAmt);
				
				
			}
			//sell Transaction and fixed sell Transaction
			else if(detailsVO.getTransactionType() ==2 || detailsVO.getTransactionType() ==6  )
			{
				if(detailsVO.getTransactionType() == 6) {
					transactionAmt = detailsVO.getTotalTransactionAmt();
					//System.out.print("setting "+transactionAmt);
				}
				System.out.print(" Trans Amt = "+transactionAmt+"\t");
				effectiveVolume = volume;
				if(detailsVO.getCommissionType() == AMOUNT_BASED)
				{
					//transaction coin as commission value
					commissionValue = transactionAmt.multiply(commissionRate);					
					tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt.subtract(commissionValue));
					totalCommissionSpend = totalCommissionSpend.add(commissionValue);
					System.out.print("SELL ORDER");
					System.out.print("\t"+commissionValue);
				}
				else if(detailsVO.getCommissionType() == VOLUME_BASED)
				{
					//fix is required here
					commissionValue = transactionAmt.multiply(commissionRate);					
					tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt.subtract(commissionValue));
					totalCommissionSpend = totalCommissionSpend.add(commissionValue);
					System.out.print("SELL ORDER");
					System.out.print("\t"+commissionValue);
				}
				else if(detailsVO.getCommissionType() == 3 || detailsVO.getCommissionType() == 5) {
					if(detailsVO.getCommissionType() == 3) 
					{
					tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt.subtract(commissionRate));	
					//commission coin is transaction coin
					totalCommissionSpend = totalCommissionSpend.add(commissionRate);
					}
					else 
					{
						tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt);
						//small conversion commission is not calculated to
						
					}
					
					System.out.print("SELL ORDER");
					System.out.print("\t"+commissionRate);
				}
				else if(detailsVO.getCommissionType() == 4 ) {
					
					
					//Commission coin is other than transaction , but commission is fixed amount
					if(coinName.equalsIgnoreCase(commissionCurrency)){
						effectiveVolume=volume.add(commissionRate);
						tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt);
					}
					else if(commissionCurrency.equalsIgnoreCase(currency)){
						
						tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt.subtract(commissionRate));	
					}
					else
					{
						tradeCommission.setTotalAmt(tradeCommission.getTotalAmt().subtract(commissionRate));
						tradeCurrencyAmt=tradeCurrencyAmt.add(transactionAmt);
					}
					System.out.print("SELL ORDER");
					System.out.print("\t"+commissionRate);
				}
				
				
				sellVolume=sellVolume.add(effectiveVolume);
				sellPrice= sellPrice.add(transactionAmt);
				totalAmt = totalAmt.subtract(effectiveVolume);
				
				
			}
			//Deposit Transaction
			//TODO Deposit/Withdrawal  commission Type deduction 
			else if(detailsVO.getTransactionType() == 3){
				effectiveVolume = volume.subtract(commissionRate);
				depositAmt=depositAmt.add(effectiveVolume);
				totalAmt = totalAmt.add(effectiveVolume);
				tradeCurrencyAmt=tradeCurrencyAmt.add(effectiveVolume);
				System.out.print("DEPOSIT ORDER");
				//TODO commission for Deposit Rate needs to be calculated 
				xChangeTransferRate=xChangeTransferRate.add(commissionRate);
				System.out.print("\t"+commissionRate);
				
			}
			//TODO withdrawal Transaction
			else if(detailsVO.getTransactionType() == 4){
				effectiveVolume = volume.subtract(commissionRate);
				withdrawAmt=withdrawAmt.add(volume);
				totalAmt = totalAmt.subtract(volume);
				tradeCurrencyAmt=tradeCurrencyAmt.subtract(volume);
				System.out.print("WITHDRAWAL ORDER");
				//TODO commission for Withdrawal Rate needs to be calculated
				xChangeTransferRate=xChangeTransferRate.add(commissionRate);
				System.out.print("\t"+effectiveVolume);
				System.out.print("\t"+commissionRate);
			}
			else if(detailsVO.getTransactionType() ==6) 
			{
				
				effectiveVolume = volume;
				sellVolume=sellVolume.add(effectiveVolume);
				sellPrice= sellPrice.add(detailsVO.getTotalTransactionAmt());
				totalAmt = totalAmt.subtract(effectiveVolume);
				
				System.out.print("SELL ORDER");
				System.out.print("\t"+commissionValue);
			}
			
			System.out.print(" Final Curr Amt \t"+tradeCurrencyAmt);
			System.out.print(" Aft Vol \t"+totalAmt);
			
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
			xChangeCurrency.setDepositAmt(depositAmt.setScale(8, RoundingMode.HALF_UP));
			xChangeCurrency.setWithdrawalAmt(withdrawAmt.setScale(8, RoundingMode.HALF_UP));
			xChangeCurrency.setTotalAmt(totalAmt.setScale(8, RoundingMode.HALF_UP));
			xChangeCurrency.setXchangeTransferCommission(xChangeTransferRate.setScale(8, RoundingMode.HALF_UP));
			tradeCurrency.setTotalAmt(tradeCurrencyAmt.setScale(8, RoundingMode.HALF_UP));
			//System.out.println("***********************************************************");
			}
			}
			
		
		
		return exchangeList;
	}
	private ExchangeCurrencyVO buildCurrencyInXChange(String exchangeName, String coinName,List<ExchangeCurrencyVO> currencyList) {
		ExchangeCurrencyVO tradeCurrency;
		tradeCurrency = new ExchangeCurrencyVO();
		//System.out.println("Added in trade Mean");
		//tradeMean = createNewCurrecnyMean(currency);
		tradeCurrency.setCoinName(coinName);
		tradeCurrency.setExchangeName(exchangeName);
		//tradeCurrency.addCurrencyVO(tradeMean);
		if(tradeConfiguration.isTradeCurrency(exchangeName,coinName)){
			tradeCurrency.setTradeCurrency(true);
		}
		//Since it is reference
		currencyList.add(tradeCurrency);
		return tradeCurrency;
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
