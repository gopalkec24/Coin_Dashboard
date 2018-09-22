package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolio.dao.FetchConfiguration;
import com.portfolio.dao.MainnetConfiguration;

import org.apache.commons.io.FileUtils;


public class GetCurrentRate {
	
	

	private static final String CM_ID = "id";

	private static final String CM_SYMBOL = "symbol";

	private static final String CM_STATUS = "status";

	private static final String CM_PRICE = "price";

	private static final String CURRENCY_USD = "USD";

	private static final String CM_QUOTES = "quotes";

	private static final String CM_DATA = "data";

	private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal("1");

	private static final Map<String,Integer> CACHED_COIN_ID = Collections.synchronizedMap(new HashMap<String,Integer>());
		
	public static List<FetchConfiguration> fetchConfig = null;
	public static List<MainnetConfiguration> mainnetData = null;

	public static void main(String[] args) throws Exception {

		 /* String endpointURL = "https://api.coinmarketcap.com/v2/listings/";
// String response=getDetailsFromSite(endpointURL);
 
 String responseStr="{\"data\":[ {\"id\": 1,\"name\": \"Bitcoin\",\"symbol\": \"BTC\",\"website_slug\": \"bitcoin\"}"
			+ ",{ \"id\": 2,\"name\": \"Litecoin\",\"symbol\": \"LTC\",\"website_slug\": \"litecoin\"}]}";
 JSONObject object= new JSONObject(responseStr);
 System.out.println(object.toString());
 JSONArray jsoncargo = object.getJSONArray("data");

 String findSymbol = "LTC";
 for (int i = 0; i < jsoncargo.length(); i++) {
	 	System.out.println(jsoncargo.get(i).toString());
		 String symbol = jsoncargo.getJSONObject(i).getString("symbol");
		 if(symbol!=null && symbol.equalsIgnoreCase(findSymbol)){
			 int id= jsoncargo.getJSONObject(i).getInt("id");
			 System.out.println(id);
		 }
 }*/
		
		String responseStr="{\"data\": {\"id\": 1,\"name\": \"Bitcoin\","+ 
       "\"symbol\": \"BTC\", \"website_slug\": \"bitcoin\", \"rank\": 1, \"circulating_supply\": 17165000.0, "+
       " \"total_supply\": 17165000.0, \"max_supply\": 21000000.0, \"quotes\": {"+
           " \"USD\": { \"price\": 7623.24, \"volume_24h\": 4017970000.0, \"market_cap\": 130852914600.0," +
               " \"percent_change_1h\": 0.05,\"percent_change_24h\": 2.8, \"percent_change_7d\": 19.45}"+
        "},\"last_updated\": 1532318722},\"metadata\": {\"timestamp\": 1532318365,\"error\": null}"+
"}";
		//getDetailForSymbol(responseStr);
		/*List<String> coinList= new ArrayList<String>();
		coinList.add("BTC");
		coinList.add("LTC");
		coinList.add("ETH");
		coinList.add("USDT");
		coinList.add("VEN");
		System.out.println(getMarketPriceFromCoinMarketCap(coinList));*/
	/*List<String> exchagne = new ArrayList();
		exchagne.add("BINANCE");
		getCurrentMarketPriceFromExchange(exchagne);*/
		//cacheCoinIdFromMarketPlace();
		//getNonCryptoCurrencyValue("USD","MXN");

		for(MainnetConfiguration mainData : GetCurrentRate.getMainNetConfigData())
		{
			System.out.println(mainData.getExchangeName());
		}
	}

	public static String getDetailsFromSite(String endpointURL)
			throws MalformedURLException, IOException, ProtocolException {
		StringBuffer response = new StringBuffer();
		System.out.println(endpointURL);
		HttpURLConnection conn =  ReadTradeConfig.getHttpURLConnection(endpointURL);
		conn.addRequestProperty("User-Agent", "Mozilla/4.0");
		//conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept", "application/json");

		if (conn==null || conn.getResponseCode() != 200) {
			System.out.println("Error in retreive result for :"+endpointURL);
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			//System.out.println(output);
			response.append(output);
		}
		System.out.println("Going to disconnect");		
		
		return response.toString();
	}

	public static Map<String, BigDecimal> getCurrentPriceFromCoinMarketCap(List<String> tradedSymbol) throws Exception {
		if(ReadTradeConfig.useLatestVersion)
		{
			return getMarketPriceFromCoinMarketCap(tradedSymbol);
		}
		else
		{
			return getCurrentPriceFromCoinMapV2(tradedSymbol);
		 
		}
	}

	private static Map<String, BigDecimal> getCurrentPriceFromCoinMapV2(List<String> tradedSymbol)
			throws Exception, MalformedURLException, ProtocolException, IOException {
		String tickerURL="https://api.coinmarketcap.com/v2/ticker/";		
		cacheCoinId();
		Map<String,BigDecimal> currentPriceMap = new HashMap<String,BigDecimal>();
		for(String findSymbol: tradedSymbol){
			//System.out.println(findSymbol);			
			int id=getCoinId(findSymbol);	 
			if (id!= -1) {
				//System.out.println(tickerURL + id + "/");
				String symbolDetails = getDetailsFromSite(tickerURL + id + "/");
				currentPriceMap.put(findSymbol,getDetailForSymbolV2(symbolDetails));
			}
		}
		return currentPriceMap;
	}

	
	public static Map<String, BigDecimal> getMarketPriceFromCoinMarketCap(List<String> tradedSymbol) throws Exception {
		//CMC_PRO_API_KEY=9afba42a-ebdc-4bf0-a742-d110c154dc49&id=1,2,1027,825&convert=USD"
		String tickerURL=ReadTradeConfig.COINMARKETCAP_QUOTES_LATEST+ReadTradeConfig.api_key;
		cacheCoinId();
		List<Integer> idList= new ArrayList<Integer>();	
		StringBuffer ids= new StringBuffer();
		for(String findSymbol: tradedSymbol)
		{
			int id=getCoinId(findSymbol);
			if (id!= -1) 
			{
				ids.append(","+id);
				idList.add(id);		
				System.out.println("Coin Market ID  found for Coin "+findSymbol + " with ID"+id);
			}
			else {
				System.out.println("Coin Market ID not found for Coin "+findSymbol);
			}
		}
		String marketData= getDetailsFromSite(tickerURL+"&id="+ids.toString().substring(1));
		
		
		return getDetailForSymbols(marketData);
	}
	private static int getCoinId(String findSymbol) throws MalformedURLException, ProtocolException, IOException {
		
		
		if(!ReadTradeConfig.excludedCoinList.contains(findSymbol) && CACHED_COIN_ID.containsKey(findSymbol))
		{
			return CACHED_COIN_ID.get(findSymbol);
		}
			
		return -1;
	}

	private static void cacheCoinId() throws Exception {
		
		//List the Coin Market Cap ID		
		String response = cacheCoinIdFromMarketPlace();
		JSONObject object= new JSONObject(response);
		// System.out.println(object.toString());
		JSONArray jsoncargo = object.getJSONArray(CM_DATA);
		for (int i = 0; i < jsoncargo.length(); i++) {
			 String symbol = jsoncargo.getJSONObject(i).getString(CM_SYMBOL);
			 int id= jsoncargo.getJSONObject(i).getInt(CM_ID);			 
			 CACHED_COIN_ID.put(symbol,id);
		}
	}

	private static String cacheCoinIdFromMarketPlace() throws Exception {
		String endpointURL = ReadTradeConfig.COINMARKETCAP_CRYPTOCURRENCY_MAP+ReadTradeConfig.api_key;
		File lastUpdated= new File(ReadTradeConfig.CONFIG_DIRECTORY+"lastCacheUpdate.json");
		long lastUpdateInMS =0;
		String response  = null;
		String timeStamp = null;
		if(lastUpdated.exists())
		{
		 response = FileUtils.readFileToString(lastUpdated, "utf-8");
		if(response!= null && !response.equalsIgnoreCase("") && response.length() > 0) {
			JSONObject cacheObj = new JSONObject(response);
			 timeStamp = cacheObj.getJSONObject(CM_STATUS).getString("timestamp");
			 lastUpdateInMS= getTimeinMilliseconds(timeStamp);
			 Date ne = new Date(lastUpdateInMS);
			//System.out.println("Value got from Cache "+ timeStamp + " Date " + ne);
		}	
		}
	    System.out.println("Last updated time " +lastUpdateInMS);
	    System.out.println("Current Time " + System.currentTimeMillis()) ;
	    System.out.println(new Date(System.currentTimeMillis()));
		System.out.println("last updated " + (System.currentTimeMillis() - lastUpdateInMS));
		
		if(timeStamp== null || response == null || response.equalsIgnoreCase("") || (lastUpdateInMS +86400000)< System.currentTimeMillis() ) {
			 response=getDetailsFromSite(endpointURL);			 
			 FileUtils.writeStringToFile(lastUpdated,response);
			 System.out.println("New File updated in Cache "+response);
		}				
		return response;
	}

	private static Long getTimeinMilliseconds(String timeStamp) throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		  Date date = format.parse(timeStamp);
		   if(date == null) {
			   throw new Exception();
		   }
		    
		    return date.getTime();
		    
	}


	public static Map<String,Map<String,Map<String,Object>>> getCurrentMarketPriceFromExchange(List<String> exchangeName) 
	{
		
		Map<String, Map<String, Map<String, Object>>> xchangeCurrentValueMap = new TreeMap<String, Map<String,Map<String,Object>>>();
		
		
		if (fetchConfig == null) {
			fetchConfig = getExchangeConfigData();
		}
		for(FetchConfiguration config : fetchConfig)
		{
		 //FetchConfiguration fetchConfig=  gson.fromJson(response, FetchConfiguration.class);
		/*	System.out.println(config.getFetchURL());
			System.out.println(config.getName());
			System.out.println(config.getAttributeFetch().length);*/
			if(exchangeName.contains(config.getName())) {
			GetCurrentMarketPrice cmp= new GetCurrentMarketPrice();
			try {
				 xchangeCurrentValueMap.put(config.getName(), cmp.getCurrentMarketPrice(config));
				
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
			}
		}
		
		
		return xchangeCurrentValueMap;
	}

	public static List<FetchConfiguration> getExchangeConfigData() {
		File lastUpdated= new File(ReadTradeConfig.CONFIG_DIRECTORY+"configurationURL1.json");
		String response= null;
		if(fetchConfig== null) {
		if(lastUpdated.exists())
		{
		 try {			 
			 
			response = FileUtils.readFileToString(lastUpdated, "utf-8");
			//System.out.println(response);
			 Gson gson = new Gson();
			 Type listType = new TypeToken<List<FetchConfiguration>>(){}.getType();
			 
			 fetchConfig= gson.fromJson(response,listType);
			 
			
			
		 	} 
		 	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
		}
		return fetchConfig;
	}
	
	public static List<MainnetConfiguration> getMainNetConfigData() {
		File lastUpdated= new File(ReadTradeConfig.CONFIG_DIRECTORY+"mainnet.json");
		String response= null;
		if(mainnetData== null) {
		if(lastUpdated.exists())
		{
		 try {			 
			 
			response = FileUtils.readFileToString(lastUpdated, "utf-8");
			//System.out.println(response);
			 Gson gson = new Gson();
			 Type listType = new TypeToken<List<MainnetConfiguration>>(){}.getType();
			 
			 mainnetData= gson.fromJson(response,listType);
			 
			
			
		 	} 
		 	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
		}
		return mainnetData;
	}
	
	private static BigDecimal getDetailForSymbolV2(String symbolDetails) {
  
		JSONObject object= new JSONObject(symbolDetails);
		//System.out.println(object.toString());
		JSONObject currencyDetails = object.getJSONObject(CM_DATA).getJSONObject(CM_QUOTES);
		JSONObject price=currencyDetails.getJSONObject(CURRENCY_USD);
		Object objPrice = price.get(CM_PRICE);
		
		BigDecimal latestPrice = null;
		if(objPrice!= null && objPrice instanceof Double) {
			latestPrice  = new BigDecimal(objPrice.toString());
		}
		
		 //System.out.println(currencyDetails.toString());
		
		return latestPrice;
	}
	
	private static Map<String, BigDecimal> getDetailForSymbols(String symbolDetails) {
		  
		JSONObject object= new JSONObject(symbolDetails);
		//System.out.println(object.toString());
		String[] ids = JSONObject.getNames(object.getJSONObject(CM_DATA));
		BigDecimal latestPrice = null;
		 Map<String,BigDecimal> currentPriceMap = new HashMap<String,BigDecimal>();
		for(String id: ids)
		{
		//System.out.println("Symbol ID: "+id);
		JSONObject currencyDetails = object.getJSONObject(CM_DATA).getJSONObject(id).getJSONObject("quote").getJSONObject(CURRENCY_USD);		 
		String coinName= object.getJSONObject(CM_DATA).getJSONObject(id).getString(CM_SYMBOL);
		Object objPrice = currencyDetails.get(CM_PRICE);
		
		
		if(objPrice!= null && objPrice instanceof Double) {
			latestPrice  = new BigDecimal(objPrice.toString());
			currentPriceMap.put(coinName, latestPrice);
		}
		
		}
		 //System.out.println(currencyDetails.toString());
		
		return currentPriceMap;
	}
	

	public static Map<String,BigDecimal> getNonCryptoCurrencyValue(String source,String coinName) throws Exception, ProtocolException, IOException {
		Map<String,BigDecimal> nonCyptoCurrentValue= new TreeMap<String,BigDecimal>();
		String baseURL="http://www.apilayer.net/api/live?access_key=06773261e0b8ea23e9f73178f8a5d422&source="+source+"&format=1&currencies="+coinName;
		String reponse= getDetailsFromSite(baseURL);
		if(reponse!= null) {
			JSONObject object= new JSONObject(reponse);
			boolean  fetch = object.getBoolean("success");
			if(fetch) {			 
			 getCurrencyValueFromResponse(source, coinName, nonCyptoCurrentValue, object);
			 
			}
			else {
				int code= object.getJSONObject("error").getInt("code");
				if(code == 104) {
					reponse= getDetailsFromSite("http://www.apilayer.net/api/live?access_key=c036cb4353fa9f7a4583407bc18baa95&source="+source+"&format=1&currencies="+coinName);
					 object= new JSONObject(reponse);
					fetch = object.getBoolean("success");
					 getCurrencyValueFromResponse(source, coinName, nonCyptoCurrentValue, object);						
				}
			}
			
		}
		
		return nonCyptoCurrentValue;
		
		
	}

	private static void getCurrencyValueFromResponse(String source, String coinName,
			Map<String, BigDecimal> nonCyptoCurrentValue, JSONObject object) {
		for(String currency: coinName.split(",")) {
			// System.out.println(object.getJSONObject("quotes").getBigDecimal(source+currency));
			 nonCyptoCurrentValue.put(currency,object.getJSONObject(CM_QUOTES).getBigDecimal(source+currency));
		 }
		nonCyptoCurrentValue.put(source, BIG_DECIMAL_ONE);
	}

	
	}
