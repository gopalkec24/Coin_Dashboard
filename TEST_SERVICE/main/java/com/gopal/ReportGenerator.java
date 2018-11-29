package com.gopal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportGenerator {

	public static void main(String[] args) {
		
		String csvFile = "D:\\Documents\\transactiom\\transaction_1.csv";
		 String coinSettingFile ="D:\\Documents\\transactiom\\coin_setting.csv";   
	        try {
	        	 Map<String, List<TransactionVO>> coinMap = readTransaction(csvFile);
	        	 Map<String,CoinSettingVO> coinSettingMap =readCoinSetting(coinSettingFile);
				processCoinVO(coinMap,coinSettingMap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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
	            
	            

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
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

	private static Map<String, List<TransactionVO>> readTransaction(String csvFile) {
		
	        BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
	        Map<String,List<TransactionVO>> coinMap = new TreeMap<String,List<TransactionVO>>();
	        try {

	            br = new BufferedReader(new FileReader(csvFile));
	            String coinName = null;
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] country = line.split(cvsSplitBy);
	                TransactionVO coin = new TransactionVO();
	                coinName=country[0];
	                coin.setName(coinName);
	                coin.setPrice(Double.parseDouble(country[2]));
	                coin.setVolume(Double.parseDouble(country[1]));
	                coin.setTransactionType(Integer.parseInt(country[3]));
	                List<TransactionVO> list =null;
	                if(coinMap.containsKey(coinName)) {
	                	list = coinMap.get(coinName);
	                	
	                }
	                else {
	                list = new ArrayList<TransactionVO>();
	                	
	                }
	                list.add(coin);
                	
                	coinMap.put(coinName,list );
	               //  System.out.println(" [Coin= " + country[0] + " , Transaction Type=" + country[3] + "]");

	            }
	            
	            

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
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

	private static void processCoinVO(Map<String, List<TransactionVO>> coinMap, Map<String,CoinSettingVO> coinSettingMap) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("D:\\Documents\\transactiom\\report.csv");
		pw.println("Coin Name, Total volume , TotalPrice,Average Price ,Project Sell value,Current value, Current Portifilo Value, Profit/loss,Profit Percentage,StablizeCount,StablizeRate");
		for(String coinName  : coinMap.keySet()) {
			List<TransactionVO> coinVOList= coinMap.get(coinName);
			   double totalPrice = 0;
			   double totalVolume = 0;
			   double buyPrice = 0;
			   double sellPrice = 0;
			  
			 for(TransactionVO coinVO : coinVOList) {
				 double costPrice = coinVO.getPrice()* coinVO.getVolume();
				 double totalNetPrice =0.0;
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
			 double mean = totalVolume > 0 ?  totalPrice / totalVolume : 0;
			 
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

	private static double findStablizeCount(double mean, double totalVolume, double currentRate, double stablizeRate) {
		
		return ((mean*totalVolume) - (currentRate*totalVolume))/currentRate;
	}

	private static double findStablizeRate(double mean,double currentValue) {
		  System.out.println(currentValue+" " + mean);
		return (currentValue*2)-mean;
	}

}
