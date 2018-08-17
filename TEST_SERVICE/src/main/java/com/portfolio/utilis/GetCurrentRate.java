package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;



public class GetCurrentRate {

	public static void main(String[] args) throws MalformedURLException, IOException {

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
		getDetailForSymbol(responseStr);
		List<String> coinList= new ArrayList<String>();
		coinList.add("BTC");
		coinList.add("LTC");
		coinList.add("ETH");
		System.out.println(getCurrentPriceFromCoinMarketCap(coinList));

	}
    private static List<Map<String,Object>> getCurrentPriceFromBinance(List<String> coinList){
		List<Map<String,Object>> currentList= new ArrayList<Map<String,Object>>();
    	
    	return null;
    	
    }
	private static String getDetailsFromSite(String endpointURL)
			throws MalformedURLException, IOException, ProtocolException {
		StringBuffer response = new StringBuffer();
		URL url = new URL(endpointURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		//System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			//System.out.println(output);
			response.append(output);
		}
		System.out.println("Going to disconnect");
		conn.disconnect();
		
		return response.toString();
	}

	public static Map<String, BigDecimal> getCurrentPriceFromCoinMarketCap(List<String> tradedSymbol) throws MalformedURLException, ProtocolException, IOException {
		String endpointURL = "https://api.coinmarketcap.com/v2/listings/";
		String tickerURL="https://api.coinmarketcap.com/v2/ticker/";
		String response=getDetailsFromSite(endpointURL);
		JSONObject object= new JSONObject(response);
		// System.out.println(object.toString());
		 JSONArray jsoncargo = object.getJSONArray("data");
		 Map<String,BigDecimal> currentPriceMap = new HashMap<String,BigDecimal>();
		 for(String findSymbol: tradedSymbol){
			// System.out.println("Finding symbol from coinMarketcap"+findSymbol);
		  int id=getCoinId(jsoncargo, findSymbol);
		  if (id!= -1) {
			String ticker_url = tickerURL + id + "/";
			//System.out.println(ticker_url);
			String symbolDetails = getDetailsFromSite(ticker_url);
			currentPriceMap.put(findSymbol,getDetailForSymbol(symbolDetails));
		}
		 }
		return currentPriceMap;
	}

	private static BigDecimal getDetailForSymbol(String symbolDetails) {
		
		
		JSONObject object= new JSONObject(symbolDetails);
		System.out.println(object.toString());
		JSONObject currencyDetails = object.getJSONObject("data").getJSONObject("quotes");
		JSONObject price=currencyDetails.getJSONObject("USD");
		BigDecimal latestPrice = price.getBigDecimal("price");
		
		 //System.out.println(currencyDetails.toString());
		
		return latestPrice;
	}

	private static int getCoinId(JSONArray jsoncargo, String findSymbol) {
		for (int i = 0; i < jsoncargo.length(); i++) {
			 	//System.out.println(jsoncargo.get(i).toString());
				 String symbol = jsoncargo.getJSONObject(i).getString("symbol");
				 if(symbol!=null && symbol.equalsIgnoreCase(findSymbol)){
					 int id= jsoncargo.getJSONObject(i).getInt("id");
					 System.out.println(id);
					 return id;
				 }
		 }
		return -1;
	}
	
	
	}
