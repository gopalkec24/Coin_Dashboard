package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.portfolio.dao.TransactionDetailsVO;

public class ReadTransactionDetails {

	private final static int TRANS_DATE_INDEX=0;
	
	private final static int COIN_NAME_INDEX=1;
	private final static int TRANS_VOLUME_INDEX=2;
	private final static int TRANS_PRICE_INDEX=3;
	private final static int TRANS_TYPE_INDEX=4;
	private final static int TRANS_COMM_TYPE_INDEX=5;
	private final static int TRANS_COMM_RATE_INDEX=6;
	private final static int TRANS_CURRENCY_INDEX=7;
	private final static int EXCHANGE_NAME_INDEX=8;
	


	
	
	
	public static void main(String[] args) {
		//TODO
		//Map<String,List<TransactionVO>> transactionMap = getT
		

	}
   
	
	 public List<TransactionDetailsVO> getTransactionDetails(String csvFile) throws NumberFormatException, IOException{


			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			List<TransactionDetailsVO> transactionList = new ArrayList<TransactionDetailsVO>();

			try
			{
				br = new BufferedReader(new FileReader(csvFile));
				String coinName = null;
				while ((line = br.readLine()) != null) 
				{
					//line is empty and startswith # means it is comment in transaction
					System.out.println(line);
					if(line.length()<0 || line.equalsIgnoreCase("") ||line.startsWith("#"))
					{
						continue;
					}
					// use comma as separator
					String[] transactionDetails = line.split(cvsSplitBy);
					TransactionDetailsVO transactionVO = new TransactionDetailsVO();
					coinName=transactionDetails[COIN_NAME_INDEX];
					transactionVO.setCoinName(coinName);
					transactionVO.setPrice(new BigDecimal(transactionDetails[TRANS_PRICE_INDEX]));
					transactionVO.setVolume(new BigDecimal(transactionDetails[TRANS_VOLUME_INDEX]));
					transactionVO.setTransactionType(Integer.parseInt(transactionDetails[TRANS_TYPE_INDEX]));
					transactionVO.setDate(transactionDetails[TRANS_DATE_INDEX]);
					transactionVO.setCurrency(transactionDetails[TRANS_CURRENCY_INDEX]);
					transactionVO.setExchangeName(transactionDetails[EXCHANGE_NAME_INDEX]);
					transactionVO.setCommissionType(Integer.parseInt(transactionDetails[TRANS_COMM_TYPE_INDEX]));
					transactionVO.setCommissionRate(new BigDecimal(transactionDetails[TRANS_COMM_RATE_INDEX]));
					
					transactionList.add(transactionVO);
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
}
