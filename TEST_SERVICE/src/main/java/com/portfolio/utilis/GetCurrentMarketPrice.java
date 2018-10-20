package com.portfolio.utilis;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolio.dao.AttributeVO;
import com.portfolio.dao.FetchConfiguration;
import com.portfolio.dao.WatchListVO;

public class GetCurrentMarketPrice {
	
	
	private static final boolean offInternet = false;
	
	static Map<String, FetchConfiguration> configList=null;

	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
		
		GetCurrentMarketPrice cmp = new GetCurrentMarketPrice();
		List<String> coinList= new ArrayList<String>();
		coinList.add("XRP/ETH");
		coinList.add("ONT/ETH");
		/*coinList.add("ETH/INR");
		coinList.add("BCH/ETH");
		coinList.add("BTC/XRP");*/
		
		//System.out.println(cmp.getCurrentMarketPrice("BINANCE",coinList,1));
		//System.out.println(GetCurrentRate.getDetailsFromSite("https://api.bitso.com/v3/ticker/?book=btc_mxn"));
		
		
		File summaryJson = new File("C:/Documents/watchListData.json");
		FileUtils.write(summaryJson,cmp.javaToJson(cmp.getDataForWatchList()));
		
		
		
	}
	
	

	public Map<String, Map<String, Object>> getCurrentMarketPrice(FetchConfiguration configVO) throws MalformedURLException, ProtocolException, IOException 
	{
			
		
	  Map<String,Map<String,Object>> exchangeValue = new TreeMap<String,Map<String,Object>>();
		
		getDataForAll(configVO);
		
		
		return exchangeValue;
		
	}



	private Map<String,Map<String,Object>> getDataForAll(FetchConfiguration configVO) throws MalformedURLException, ProtocolException, IOException {
		
	  System.out.println("In ALL FETCH");
		Map<String,Map<String,Object>> exchangeValue = null;
		
		String response = null;				
		
		if (offInternet) {
		
			response = getDataFromJsonFile("C:/Documents/koinex_sample.json");
		
		}
		
		else
		{
			response = GetCurrentRate.getDetailsFromSite(configVO.getFetchURL());
		}
	//	System.out.println(response);
	
		exchangeValue= new TreeMap<String,Map<String,Object>>();
		if(configVO.getName().equalsIgnoreCase("KOINEX")) {
			ObjectMapper mapper = new ObjectMapper();
			 Map<String,Object> resultMap = mapper.readValue(response, new TypeReference<Map<String,Object>>(){
			});
			 
		//	System.out.println(resultMap); 
			mapResultToTargetMap(resultMap,configVO,exchangeValue);
		}
		else if(configVO.getName().equalsIgnoreCase("BITBNS")) {
			JSONArray arr= new JSONArray(response);
			mapBitBnsResultToTargetMap(arr,configVO,exchangeValue);
		}
		else 
		{
			if(configVO.getResult().equalsIgnoreCase("JSONARRAY")) {
		
			JSONArray object = new JSONArray(response);
			//System.out.println("object : "+ object.toString(4));
			//System.out.println(object.getClass() +" " + object.length());
			for(int i=0;i<object.length(); i++) {
				Object symbolDetails = object.get(i);
				if(symbolDetails instanceof JSONObject) {
					JSONObject symbol = object.getJSONObject(i);
					getCoinStat(symbol,configVO,exchangeValue);
				}
			}
	
		}
		}
		
		return exchangeValue;
	}

	private void mapBitBnsResultToTargetMap(JSONArray arr, FetchConfiguration configVO,
			Map<String, Map<String, Object>> exchangeValue) {
		boolean checkAlias = true;
		if(configVO.getCurrencyAlias() == null)
		{
			checkAlias=false;
		}
		for(int i=0; i<arr.length() ; i++) {
			JSONObject obj = arr.getJSONObject(i);
			 String[] currenucyLot = JSONObject.getNames(obj);
			 String coin = currenucyLot.length ==1 ? currenucyLot[0]:"";
			
			 Map<String,Object> returnMap= new TreeMap<String,Object>();
			 returnMap.put("lastPrice",obj.getJSONObject(coin).getBigDecimal("lastTradePrice"));
			 
			 if(checkAlias && configVO.getCurrencyAlias().containsKey(coin)) {
				 coin=configVO.getCurrencyAlias().get(coin);
			 }
			 
			 exchangeValue.put(coin+"/"+"INR",returnMap);
		}
		
	}



	@SuppressWarnings("unchecked")
	private void mapResultToTargetMap(Map<String, Object> resultMap,FetchConfiguration configVO,
			 Map<String, Map<String, Object>> exchangeValue) {
			
		 
		
			Map<String, Object> statsMap = (Map<String, Object>)resultMap.get("stats");
			for(String currency : statsMap.keySet()) {
				//System.out.println("In Currency :"+ currency );
				Map<String,Object> coinMap=(Map<String, Object>) statsMap.get(currency);
				//System.out.println("In currecny Map details"+coinMap);
				 for(String coinName : coinMap.keySet()) {
					 //System.out.println("In coin NAme >>>>>>>>"+ coinName);
					
					 Map<String,Object> coinDetails = (Map<String, Object>) coinMap.get(coinName);
					 //System.out.println("In coin details  >>>>>>>>"+ coinDetails);
					 exchangeValue.put(coinName.toUpperCase()+"/"+configVO.getCurrencyAlias().get(currency),getCoinDetailsForTarget(coinDetails,configVO.getAttributeFetch()));
					 
				 }
			}
				
			
		
		
	}



	private Map<String, Object> getCoinDetailsForTarget(Map<String, Object> coinDetails, AttributeVO[] attributeFetch) {
		Map<String,Object> returnMap= new TreeMap<String, Object>();
		//System.out.println(coinDetails);
		for(AttributeVO attributeVO : attributeFetch) {
			//System.out.println(attributeVO.getAttr_name());
			if(attributeVO.getAttr_src_dataType().equalsIgnoreCase("String"))
			{	
				returnMap.put(attributeVO.getTgt_attr_name(),getTargetObjectType(attributeVO.getAttr_tgt_dataType(),(String)coinDetails.get(attributeVO.getAttr_name())));
			}
		}
		
		
		return returnMap;
	}



	public static String getDataFromJsonFile(String jsonCompletePath) {
		File lastUpdated= new File(jsonCompletePath);
		String response= null;		
		if(lastUpdated.exists())
		{
		 try {
			response = FileUtils.readFileToString(lastUpdated, "utf-8");					
		 	} 
		 	catch (IOException e) {			
			e.printStackTrace();
		}
	
		}
		return response;
		
	}



	public  Map<String,Map<String,Object>> getCurrentMarketPrice(String exchangeName,List<String> tradedPair,int fetchMode){
		
		
		FetchConfiguration configVO = getFetchConfigurationForExchangeV2(exchangeName);
		Map<String,Map<String,Object>> exchangeValue = null;
		
		if(configVO != null) {	
			try {
			if(configVO.getAllFetch() ==1  ||  (configVO.getAllFetch() == 2  && fetchMode== 1)) 
			{
				
					exchangeValue = getDataForAll(configVO);
					if(fetchMode ==2 ) {
						Iterator<String> iter = exchangeValue.keySet().iterator();
						List<String> removeList = new ArrayList<String>();
						while(iter.hasNext()) {
							String coinPair = iter.next();
							if(tradedPair.contains(coinPair))
							{
								
							}
							else {
								removeList.add(coinPair);
							}
						}
						System.out.println(removeList);
						System.out.println("Removal Success " +exchangeValue.values().removeAll(removeList));
					}
				
			}
			else
			{	
				exchangeValue = getDataForSpecificList(tradedPair, configVO);
			}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return exchangeValue;
		
	}



	private Map<String, Map<String, Object>> getDataForSpecificList(List<String> tradedPair, FetchConfiguration configVO) {
		Map<String, Map<String, Object>> exchangeValue =null;
		String requestSymbol= null;
		/*for(AttributeVO attr :configVO.getAttributeFetch())
		{
			if(attr.getTgt_attr_name().equalsIgnoreCase("symbol"))
			{
				requestSymbol = attr.getAttr_name();
				break;
			}
			
			
		}*/
	System.out.println("IN Specific List");
		requestSymbol = configVO.getGetSymbolParameter();
		if(requestSymbol!= null) 
		{
			String requestURL= null;
			String response= null;
			exchangeValue= new TreeMap<String,Map<String,Object>>();
			for(String pair : tradedPair) 
			{
				String symbolValue = getEquivalentSymbol(pair,configVO.getName());
				requestURL = configVO.getFetchURL()+"?"+requestSymbol+"="+symbolValue.trim();
				try {
					response= GetCurrentRate.getDetailsFromSite(requestURL);
					
					JSONObject symbol=new JSONObject(response);
					getCoinStat(symbol,configVO,exchangeValue);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(RuntimeException e) {
					e.printStackTrace();
				}

			}
		}
		else
		{
			System.out.println("REquest symbol not found for exchange : "+ configVO.getName());
		}
		return exchangeValue;
	}


	//TODO check for error and clean this method after testing
	private FetchConfiguration getFetchConfigurationForExchange(String exchangeName) {
		List<FetchConfiguration> configList = GetCurrentRate.getExchangeConfigData();
		FetchConfiguration configVO  = null;
		for(FetchConfiguration config : configList) {
			if(config.getName().equalsIgnoreCase(exchangeName))
			{
				configVO = config;
				break;
			}
		}
		return configVO;
	}
	
	private FetchConfiguration getFetchConfigurationForExchangeV2(String exchangeName) {
		if(configList== null) {
		configList = GetCurrentRate.getExchangeConfigDataV2();
		}
		FetchConfiguration configVO  = configList.get(exchangeName);		
		return configVO;
	}
	  
	public String getEquivalentSymbol(String pair, String exchangeName) {
		
		String symbolValue;
		if(exchangeName.equalsIgnoreCase("BINANCE")) {
			symbolValue=pair.replaceAll("/","").toUpperCase();
		}
		else if(exchangeName.equalsIgnoreCase("BITSO")) {
			symbolValue= pair.replaceAll("/", "_").toLowerCase();
		}
		else
		{
		  //System.out.println("No Equivalent Symbol Communicate Found for Exchange :"+exchangeName);
		  symbolValue=pair;
		}
		
		
		return symbolValue;
	}

	private void getCoinStat(JSONObject symbol,FetchConfiguration configVO, Map<String,Map<String, Object>> exchangeValue) {
		Map<String,Object> coinstat = new TreeMap<String, Object>();
		String symbolStr = null;
		String currencyStr  =null;
		//System.out.println("sumbol "+symbol.toString());
		for(AttributeVO attrVO : configVO.getAttributeFetch()) 
		{
			
			  
			   JSONObject json = symbol;
			   if (attrVO.getPath()!= null) 
			   {
				   if(attrVO.getPath().contains("<<")) 
				   {
					   String[] pathValues = attrVO.getPath().split("\\.");
					   Map<Integer,Object> jsonObject = new TreeMap<Integer,Object>();
					   int count = 0;
					   
					   //System.out.println("Dynamic variable count "+count);
					   
				   }
				   else
				   {
					   
					   if (attrVO.getPath().length() > 0) {
						String[] pathValues = attrVO.getPath().split("\\.");
						//System.out.println("Path " + attrVO.getPath() + "path values " + pathValues.length);
						for (int i = 0; i < pathValues.length; i++) {
							//TODO jsonArray  type. determine JsonArray or JSONObject 
							//Let see for it
							//System.out.println("Path values " + pathValues[i]);
							json = json.getJSONObject(pathValues[i]);
							//System.out.println(json.toString());
						} 
					}
					if(attrVO.getAttr_src_dataType().equalsIgnoreCase("String"))
					   {
						//System.out.println(json.toString(4));
						  // System.out.println("String Value for attribute : "+attrVO.getAttr_name() +"is "+json.getString(attrVO.getAttr_name()));
						   coinstat.put(attrVO.getTgt_attr_name(), getTargetObjectType(attrVO.getAttr_tgt_dataType(),json.getString(attrVO.getAttr_name())));

					   }
					   else if(attrVO.getAttr_src_dataType().equalsIgnoreCase("Number"))
					   {
						   //System.out.println("Number value for attribute: "+ attrVO.getAttr_name() + " is "+json.getBigDecimal(attrVO.getAttr_name()));
						   coinstat.put(attrVO.getTgt_attr_name(),json.getBigDecimal(attrVO.getAttr_name()));
					   }
					   else {
						   System.out.println("No Configuration found for this source Data Type "+attrVO.getAttr_src_dataType());
						   coinstat.put(attrVO.getTgt_attr_name(),json.get(attrVO.getAttr_name()));
					   }

					   if(attrVO.getTgt_attr_name().equalsIgnoreCase(("symbol")))
					   {
						   String symbolVal= json.getString(attrVO.getAttr_name());
						   //System.out.println(symbolVal);
						   
							for (String currency : configVO.getTransCurrency()) {
								//System.out.println(currency);
								if (symbolVal.toLowerCase().endsWith(currency.toLowerCase())) {
									//System.out.println(symbolVal.substring(0,	symbolVal.toLowerCase().lastIndexOf(currency.toLowerCase())));
									symbolStr = symbolVal.substring(0,
											symbolVal.toLowerCase().lastIndexOf(currency.toLowerCase()));
									symbolStr = removeSpecialCharacters(symbolStr).toUpperCase();
									currencyStr = symbolVal
											.substring(symbolVal.toLowerCase().lastIndexOf(currency.toLowerCase())).toUpperCase();
									coinstat.put("symbol", symbolStr);
									coinstat.put("tradeCurrency", currencyStr);
								}
							} 
						
					   }
					   if(symbolStr!= null && currencyStr!= null) {
						   exchangeValue.put(symbolStr+"/"+currencyStr,coinstat);

					   }
					   else
					   {
						   System.out.println("No Currency found for this "+ symbolStr +  currencyStr  + symbol.toString(4));	
					   }
				   }
		}
			   else
			   {
				   System.out.println("No path configured please configure it for "+ attrVO.getTgt_attr_name());
			   }
		}
		
	}

	private String removeSpecialCharacters(String symbolStr) {
		
		String alphaOnly = symbolStr.replaceAll("[^a-zA-Z]+","");
		//System.out.println("afterRemoveing the speial"+alphaOnly);
		
		return alphaOnly;
	}

	private Object getTargetObjectType(String attr_tgt_dataType, String value) {
		Object returnObj = value;
		if(attr_tgt_dataType.equalsIgnoreCase("BigDecimal")) {
		returnObj= new BigDecimal(value);
		}
		else if(attr_tgt_dataType.equalsIgnoreCase("Boolean")) {
			returnObj= Boolean.parseBoolean(value);
		}
		else if(attr_tgt_dataType.equalsIgnoreCase("String")) {
			 returnObj = value;
		}
		else
		{
			System.out.println("Target Type conversion for this data :"+ attr_tgt_dataType);
		}
		
		
		return returnObj;
		
	}
	
	public Map<String,Map<String,Map<String,Object>>> getDataForWatchList()
	{
		
		Map<String,Map<String,Map<String,Object>>>  exchangeDataMap = new TreeMap<String, Map<String,Map<String,Object>>>();
		
		String response = getDataFromJsonFile("C:/Documents/watchlist.json");
		Gson gson = new Gson();
		 Type listType = new TypeToken<List<WatchListVO>>(){}.getType();
		 
		 List<WatchListVO> fetchConfig= gson.fromJson(response,listType);
		 
		 for(WatchListVO watchVO : fetchConfig) {
		/*	 System.out.println(watchVO.getExchangeName());
			 System.out.println(watchVO.getWatchlist());*/
			 exchangeDataMap.put(watchVO.getExchangeName(), getCurrentMarketPrice(watchVO.getExchangeName(),watchVO.getWatchlist(),2));
			 
		 }
		
		
		//System.out.println(exchangeDataMap);
		
		return exchangeDataMap;
		
	}
	
	public String javaToJson(Map obj) {
		JSONObject jsonObj = new JSONObject(obj);
	//System.out.println(jsonObj.toString(2));
		return jsonObj.toString(4);
	}

}
