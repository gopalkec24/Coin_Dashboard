package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.portfolio.dao.TransactionDetailsVO;

public class ReadTradeConfig {

	private static final String CONFIG_VALUE_SEPERATOR = "_";
	private static final String KEY_VALUE_PAIR_SEPERATOR = "=";
	private Hashtable<String,List<String>> tradeConfig = new Hashtable<String,List<String>>();
	List<String> nonCryptoList= new ArrayList<String>();
	String COMMA_SEPEARTOR = ",";
	
	public List<TransactionDetailsVO> getConfigurationDetails(String csvFile) throws Exception{


			BufferedReader br = null;
			String line = "";
			
			List<TransactionDetailsVO> transactionList = new ArrayList<TransactionDetailsVO>();

			try
			{
				br = new BufferedReader(new FileReader(csvFile));


				while ((line = br.readLine()) != null) 
				{
					//line is empty and startswith # means it is comment in transaction
					System.out.println(line);
					if(line.length()<0 || line.equalsIgnoreCase("") ||line.startsWith("#"))
					{
						continue;
					}
					if(line.startsWith("TRADECONFIG")){
						getTradeConfigDetail(line);
					}
					else if(line.startsWith("NON_CYRPTO_CURRENCY")){
						loadNonCyptoCurrency(line);
					}
					else
					{
						System.out.println("No Load configuration  present for this :"+line);
					}

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
			return transactionList;
		
	 }


	private void loadNonCyptoCurrency(String line) throws Exception {
		String[] tradeCon=line.split(KEY_VALUE_PAIR_SEPERATOR);
		if(tradeCon== null || tradeCon.length<2){
			throw new Exception("Non Crypto Currency is not properly configured"+line);
		}
		
		nonCryptoList= convertCommaSeperatedToList(tradeCon[1]);
	}
    private List<String> convertCommaSeperatedToList(String value) throws Exception{
    	String[] values= value.split(COMMA_SEPEARTOR);
		if(values==null){
			throw new Exception(" No comma sepearted value in "+value);
		}
    	List<String> list= new ArrayList<String>();
		for(String currency : values)
		{
			list.add(currency);
		}
		return list;
    }

	private void getTradeConfigDetail(String line) throws Exception  {
		
		String[] tradeCon=line.split(KEY_VALUE_PAIR_SEPERATOR);
		if(tradeCon== null || tradeCon.length<2){
			throw new Exception("Trade currency for Exchange is not properly configured"+line);
		}
        
		String[] keydetails=tradeCon[0].split(CONFIG_VALUE_SEPERATOR);
		if(keydetails== null || keydetails.length<2){
			throw new Exception(" Exchange name is not properly configured in trade currency"+keydetails);
		}
		
		
	
		tradeConfig.put(keydetails[1],convertCommaSeperatedToList(tradeCon[1]));
		
	}
	public Hashtable<String,List<String>> getTradeConfig(){
		return tradeConfig;
	}
	public List<String> getNonCryptoList(){
		return nonCryptoList;
	}
	public boolean isTradeCurrency(String exchangeName, String coinName) {
		   if(tradeConfig.containsKey(exchangeName)){
			   if(tradeConfig.get(exchangeName).contains(coinName))
			   {
				   return true;
			   }
		   }
			return false;
		}
	public boolean isCryptoCurrency(String coinName) {
		
		if(nonCryptoList.contains(coinName))
		{
			return false;
		}
		return true;
	}
}
