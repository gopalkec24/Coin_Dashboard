package com.portfolio.utilis;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.portfolio.dao.AttributeVO;
import com.portfolio.dao.FetchConfiguration;

public class GetCurrentMarketPrice {
	
	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
		
		GetCurrentMarketPrice cmp = new GetCurrentMarketPrice();
		List<String> coinList= new ArrayList<String>();
		/*coinList.add("XRP/ETH");
		coinList.add("ONT/ETH")*/;
		coinList.add("ETH/MXN");
		coinList.add("BCH/MXN");
		coinList.add("BTC/MXN");
		
		System.out.println(cmp.getCurrentMarketPrice("BITSO",coinList));
		//System.out.println(GetCurrentRate.getDetailsFromSite("https://api.bitso.com/v3/ticker/?book=btc_mxn"));
		
	}
	
	

	public Map<String, Map<String, Object>> getCurrentMarketPrice(FetchConfiguration configVO) throws MalformedURLException, ProtocolException, IOException 
	{
		String exchangeName = configVO.getName();
		String fetchURL = configVO.getFetchURL();
		boolean securityRequired= configVO.isSecurityRequired();
		
		
	  Map<String,Map<String,Object>> exchangeValue = new TreeMap<String,Map<String,Object>>();
		
		getDataForAll(configVO);
		
		
		return exchangeValue;
		
	}



	private Map<String,Map<String,Object>> getDataForAll(FetchConfiguration configVO) throws MalformedURLException, ProtocolException, IOException {
		
		Map<String,Map<String,Object>> exchangeValue = null;
		
		String response = GetCurrentRate.getDetailsFromSite(configVO.getFetchURL());
		System.out.println(response);
		List<String> transCurrency = configVO.getTransCurrency();
		exchangeValue= new TreeMap<String,Map<String,Object>>();
		if(configVO.getResult().equalsIgnoreCase("JSONARRAY")) {
			JSONArray object = new JSONArray(response);
			//System.out.println("object : "+ object.toString(4));
			System.out.println(object.getClass() +" " + object.length());
			for(int i=0;i<object.length(); i++) {
				Object symbolDetails = object.get(i);
				if(symbolDetails instanceof JSONObject) {
					JSONObject symbol = object.getJSONObject(i);
					getCoinStat(symbol,configVO.getAttributeFetch(),transCurrency,exchangeValue);
				}
			}
	
		}
		else
		{
			JSONObject object = new JSONObject(response);
			System.out.println("object : "+ object.toString(4));
			System.out.println(object.getClass());
		}
		return exchangeValue;
	}

	public  Map<String,Map<String,Object>> getCurrentMarketPrice(String exchangeName,List<String> tradedPair){
		
		
		FetchConfiguration configVO = getFetchConfigurationForExchange(exchangeName);
		Map<String,Map<String,Object>> exchangeValue = null;
		
		if(configVO != null) {		
			if(configVO.isAllFetch()) {
				try {
					exchangeValue = getDataForAll(configVO);
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
			else
			{	
				exchangeValue = getDataForSpecificList(tradedPair, configVO);
			}
		}
		return exchangeValue;
		
	}



	private Map<String, Map<String, Object>> getDataForSpecificList(List<String> tradedPair, FetchConfiguration configVO) {
		Map<String, Map<String, Object>> exchangeValue =null;
		String requestSymbol= null;
		for(AttributeVO attr :configVO.getAttributeFetch())
		{
			if(attr.getTgt_attr_name().equalsIgnoreCase("symbol"))
			{
				requestSymbol = attr.getAttr_name();
				break;
			}
			
			
		}
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
					getCoinStat(symbol,configVO.getAttributeFetch(),configVO.getTransCurrency(),exchangeValue);
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
	  
	private String getEquivalentSymbol(String pair, String exchangeName) {
		
		String symbolValue;
		if(exchangeName.equalsIgnoreCase("BINANCE")) {
			symbolValue=pair.replaceAll("/","").toUpperCase();
		}
		else if(exchangeName.equalsIgnoreCase("BITSO")) {
			symbolValue= pair.replaceAll("/", "_").toLowerCase();
		}
		else
		{
		  System.out.println("No Equivalent Symbol Communicate Found for Exchange :"+exchangeName);
		  symbolValue=pair;
		}
		
		
		return symbolValue;
	}

	private void getCoinStat(JSONObject symbol, AttributeVO[] attributeFetch, List<String> transCurrency, Map<String,Map<String, Object>> exchangeValue) {
		Map<String,Object> coinstat = new TreeMap<String, Object>();
		String symbolStr = null;
		String currencyStr  =null;
		System.out.println("sumbol "+symbol.toString());
		for(AttributeVO attrVO : attributeFetch) 
		{
			
			  
			   JSONObject json = symbol;
			   if (attrVO.getPath()!= null && attrVO.getPath().length() > 0) {
				String[] pathValues = attrVO.getPath().split("\\.");
				System.out.println("Path " + attrVO.getPath() + "path values " + pathValues.length);
				for (int i = 0; i < pathValues.length; i++) {
					//TODO jsonArray  type. determine JsonArray or JSONObject 
					//Let see for it
					System.out.println("Path values " + pathValues[i]);
					json = json.getJSONObject(pathValues[i]);
					System.out.println(json.toString());
				} 
			}
				if(attrVO.getAttr_src_dataType().equalsIgnoreCase("String"))
			    {
			    	//System.out.println("String Value for attribute : "+attrVO.getAttr_name() +"is "+json.getString(attrVO.getAttr_name()));
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
			    	System.out.println(symbolVal);
			    	for(String currency: transCurrency) {
			    		System.out.println(currency);
			    		if(symbolVal.toLowerCase().contains(currency.toLowerCase())) {
			    			System.out.println(symbolVal.substring(0,symbolVal.toLowerCase().indexOf(currency.toLowerCase())));
			    			symbolStr= symbolVal.substring(0,symbolVal.toLowerCase().indexOf(currency.toLowerCase()));
			    			symbolStr=removeSpecialCharacters(symbolStr);
			    			currencyStr= symbolVal.substring(symbolVal.toLowerCase().indexOf(currency.toLowerCase()));
			    			coinstat.put("symbol",symbolStr);
			    			coinstat.put("tradeCurrency",currencyStr);
			    		}
			    	}
			    	
			 	}
 			 
		}
		if(symbolStr!= null && currencyStr!= null) {
			exchangeValue.put(symbolStr.toUpperCase()+"/"+currencyStr.toUpperCase(),coinstat);
			
		}
		else
		{
		 System.out.println("No Currency found for this "+ symbol.toString(4));	
		}
		
	}

	private String removeSpecialCharacters(String symbolStr) {
		
		String alphaOnly = symbolStr.replaceAll("[^a-zA-Z]+","");
		System.out.println("afterRemoveing the speial"+alphaOnly);
		
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
	
	
	
	

}
