package com.trade.analysis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.trade.analysis.dao.StaticsDataVO;
import com.trade.dao.AutoTradeVO;
import com.trade.dao.coinmarketcap.CMCData;
import com.trade.dao.coinmarketcap.CoinMarketCapDataDAO;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class GetCoinmarketStatics {

	public static final String API_ENDPOINT="https://pro-api.coinmarketcap.com";
	public static final String LATEST_RESOURCE="/v1/cryptocurrency/listings/latest";
	public static final String API_KEY="9afba42a-ebdc-4bf0-a742-d110c154dc49";
	public static WebTarget target = null;
	public static void main(String[] args) throws IOException {
		
		//String data= getCoinMarketStaticsByNOrder(1,100,"1H","top");
		
		//FileUtils.writeStringToFile(new File("C:/Documents/topN.json"), data);
		
		List<CMCData> dataList = getGainerFilterByRank(100,"24H");
		List<CMCData> dataList2 = getLoserFilterByRank(100,"24H");
		StaticsDataVO data = new StaticsDataVO();
		data.setTopGain(dataList);
		data.setTopLoss(dataList2);
		writeDataToJSON("C:/Documents/TopChange20.json", data);
		//testMethod("C:/Documents/TopChange20.json");
	}
	
	public static List<CMCData> getGainerFilterByRank(int topRank,String sortby){
		String orderBy = "asc";
		return getFilteredData(topRank, sortby, orderBy);
		
	}

	public static List<CMCData> getLoserFilterByRank(int topRank,String sortby){
		String orderBy = "desc";
		return getFilteredData(topRank, sortby, orderBy);
		
	}
	private static List<CMCData> getFilteredData(int topRank, String sortby, String orderBy) {
		List<CMCData> dataList = new CopyOnWriteArrayList<CMCData>(); 
		CoinMarketCapDataDAO marketData=getCoinMarketcapDAOByNorder(1,1000,sortby,orderBy);
		if(marketData!= null && (marketData.getStatusCode()!= 404 &&  marketData.getStatus().getError_message() == null)) {
			if(marketData.getData()!= null) {
				for(CMCData data: marketData.getData()) {
					if(data.getCmc_rank() < topRank) {
						dataList.add(data);
					}
					else {
						TradeLogger.LOGGER.finest(" symbol "+ data.getSymbol() + " CMC Rank "+ data.getCmc_rank() );
					}
				}
			}
		}
		else {
			TradeLogger.LOGGER.severe("NO DATA RECEVIED FROM SERVER");
		}
		return dataList;
	}
	
	
	public static CoinMarketCapDataDAO getCoinMarketcapDAOByNorder(int start,int end,String sortby,String sortingOrder) {
		String returnValue = getCoinMarketStaticsByNOrder(start, end, sortby, sortingOrder);
		System.out.println(returnValue);
		CoinMarketCapDataDAO successObj= (CoinMarketCapDataDAO) TradeClient.getDAOObject(returnValue, CoinMarketCapDataDAO.class);
		return successObj;
	}
	
	public static String getCoinMarketStaticsByNOrder(int start,int end,String sortby,String sortingOrder) {
		String returnValue = null;
		target=getTarget(true).path(LATEST_RESOURCE);
		String sort=getSortType(sortby);
		if(sort!=null) {
			target =target.queryParam("sort", sort);
		}
		if(start==0 || start ==-1) {
			start =1;
		}
		
		target = target.queryParam("sort_dir", getSortOrder(sortingOrder))
						.queryParam("cryptocurrency_type", "all")
						.queryParam("start", start)
						.queryParam("limit", end);

		 TradeLogger.LOGGER.info("Complete Query " +target.getUri().toString());
		Response response =  target.request()
				.accept(MediaType.APPLICATION_JSON)
				.header("X-CMC_PRO_API_KEY",API_KEY)
				.get();
		
		if(response != null && response.getStatus() == 200) 
		{
			returnValue=response.readEntity(String.class);
		}
		else if(response.getStatus()!=407)
		{
			
			returnValue=response.readEntity(String.class);
		
		}
		TradeLogger.LOGGER.finest("response == "+ response.getStatus()+" Return value is "+ returnValue);
	
		
		return returnValue;
	}
	
	private static String getSortOrder(String sortingOrder) {
		String sortOrder = "desc";
		if(sortingOrder.equalsIgnoreCase("bottom")|| sortingOrder.equalsIgnoreCase("asc")) {
			sortOrder ="asc";
		}
		return sortOrder;
	}
	private static String getSortType(String sortby) {
		String sort;
		if(sortby== null) {
			sort = null;
		}
		else if(sortby.equalsIgnoreCase("1H")) {
			sort="percent_change_1h";
		}
		else if(sortby.equalsIgnoreCase("24H")||sortby.equalsIgnoreCase("1D")) {
			sort="percent_change_24h";
			
		}
		else if(sortby.equalsIgnoreCase("7D")) {
			sort="percent_change_7d";
		}
		else {
			sort= null;
		}
		return sort;
	}
	public static WebTarget getTarget(boolean initaileNew) {
		if(target == null || initaileNew) {
			target = TradeClient.getAdvancedClient(API_ENDPOINT,true);
			TradeLogger.LOGGER.info("Initialized Newly ....");
		}
		return target;
	}
	
	public static CoinMarketCapDataDAO  getCacheData() {
		
		
		
		
		
		return null;
		
	}
	public static void testMethod(String fileName) {
		String returnValue;
		try {
			returnValue = FileUtils.readFileToString(new File(fileName));
			CoinMarketCapDataDAO successObj= (CoinMarketCapDataDAO) TradeClient.getDAOObject(returnValue, CoinMarketCapDataDAO.class);
			if(successObj!= null) {
				for(CMCData data : successObj.getData()) {
					TradeLogger.LOGGER.info("Data ID :: "+ data.getQuote().get("USD").getPercent_change_1h());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void writeDataToJSON(String jsonFilePath,StaticsDataVO data) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			FileUtils.write(new File(jsonFilePath),mapper.defaultPrettyPrintingWriter().writeValueAsString(data));
		} catch (JsonGenerationException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
}
