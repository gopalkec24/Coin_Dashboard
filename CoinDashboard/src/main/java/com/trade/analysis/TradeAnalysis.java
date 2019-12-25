package com.trade.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.trade.dao.binance.CLData;
import com.trade.exception.AutoTradeException;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class TradeAnalysis
{
	public static WebTarget target = null;
   
	public static void main(String[] args) {
		
		TradeAnalysis analysis =new TradeAnalysis();
		try {
			List<CLData> datList = analysis.getCandleStickFromBinance("BTCUSDT", "1h");
			analysisData(datList);
		} catch (AutoTradeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void analysisData(List<CLData> datList) {
		List<CLData> lowestLowPrice = new ArrayList<CLData>();
		
		for(CLData data : datList) {
			TradeLogger.LOGGER.info("Open Time" +new Date( data.getOpenTime()).toString()+"Close Time" +new Date(data.getCloseTime()).toString());
			
		}
		
	}

	public static WebTarget getTarget(String apiEndPoint,boolean initaileNew) {
		if(target == null || initaileNew) {
			target = TradeClient.getAdvancedClient(apiEndPoint,true);
			TradeLogger.LOGGER.info("Initialized Newly ....");
		}
		return target;
	}
	public List<CLData> getCandleStickFromBinance(String symbol,String interval) throws AutoTradeException
	{
		
		 WebTarget target = getTarget("https://api.binance.com", false);
		 List<CLData> cldata = new ArrayList<CLData>();
		 target=target.path("api/v3/klines");
		 String repsonseStr= null;
		 if(AutoTradeUtilities.isNullorEmpty(symbol) || AutoTradeUtilities.isNullorEmpty(interval))
		 {
			 throw new AutoTradeException("symbol name or Currency cannot null value. Please pass the value." );
		 }
		 
		 target= target.queryParam("symbol", symbol);
		 target = target.queryParam("interval", interval);
		 
		 Response response = target.request().get();
		 
		 if(response!= null && response.getStatus() == 200)
		 {
			 repsonseStr  = response.readEntity(String.class);	
			List<Object> object = (List<Object>) AutoTradeUtilities.getDAOObject(repsonseStr, List.class );
			
			mapData(object,cldata);
			TradeLogger.LOGGER.info(object.toString());
			//TradeLogger.LOGGER.info(repsonseStr);
		 }
		 else
		 {
			 repsonseStr  = response.readEntity(String.class);
			 TradeLogger.LOGGER.info(repsonseStr);
		 }
		 
		return cldata;
		
	}

	private void mapData(List<Object> object, List<CLData> cldataList) {
		
		for(Object obj : object) {
			List datas = (List) obj;
			CLData cldata = new CLData();
			for(int i=0;i<datas.size() ;i++)
			{
				Object data = datas.get(i);
				if(i==0) {
				 cldata.setOpenTime((Long) data);
				}
				else if(i==1) {
				cldata.setOpen(AutoTradeUtilities.getBigDecimalValue(data));
				}
				else if(i==2) {
				cldata.setHigh(AutoTradeUtilities.getBigDecimalValue(data));
				}
				else if(i==3) {
				cldata.setLow(AutoTradeUtilities.getBigDecimalValue(data));	
				}
				else if(i==4) {
				cldata.setClose(AutoTradeUtilities.getBigDecimalValue(data));	
				}
				else if(i==5) {
				cldata.setVolume(AutoTradeUtilities.getBigDecimalValue(data));	
				}
				else if(i==6) {
				cldata.setCloseTime((Long) data);	
				}
				else if(i==7) {
				cldata.setQuoteAssetVol(AutoTradeUtilities.getBigDecimalValue(data));
				}
				else if(i==8) {
				cldata.setTrades((Integer) data);
				}
				else if(i==9) {
				cldata.setTakerBuyBaseAssetVol(AutoTradeUtilities.getBigDecimalValue(data));	
				}
				else if(i == 10) {
				cldata.setTakerBuyQuoteAssetVol(AutoTradeUtilities.getBigDecimalValue(data));	
				}
				else {
				TradeLogger.LOGGER.info(i +"  " +cldata.getClass().getName());
				}
			}
			cldataList.add(cldata);
		}
		
	}
	
}
