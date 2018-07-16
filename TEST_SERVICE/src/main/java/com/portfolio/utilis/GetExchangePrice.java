package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.gopal.currency.ConcurrencyConvertResponseVO;
import com.portfolio.dao.ExchangeRateList;
import com.portfolio.dao.ExchangeRateVO;

public class GetExchangePrice {
	
	private static String PROXY_AUTH_PASSWORD = "Gopalelk@54";
	private static String PROXY_AUTH_USERNAME = "hcltech\\natarajan_g";
	private static String PROXY_PORT = "8080";
	private static String PROXY_HOSTNAME = "10.121.11.33";
	private static boolean useProxy = false;
	
	
	
	public static void main(String[] args) throws Exception{
		
		GetExchangePrice getExchange = new GetExchangePrice();
		getExchange.getExchangeRate("https://api.binance.com/api/v3/ticker/price");
		
	}
	
	public Map<String,String> getExchangeRate(String exchangeURL) throws Exception{
		
		Map<String,String> symbolMap = new HashMap<String, String>();
		String responseValue = getHttpResponse(exchangeURL);
		System.out.println(exchangeURL);
		if(responseValue != null ) {
			System.out.println(responseValue);
			
			Gson gson= new Gson();
			ExchangeRateList list  = gson.fromJson(responseValue,ExchangeRateList.class);
		}
		
		
		return symbolMap;
		
		
	}
	
	
	
	
	public static void setProxyDetails(String hostName,String portNumber,String userName,String password){
		PROXY_HOSTNAME=hostName;
		PROXY_PORT=portNumber;
		PROXY_AUTH_USERNAME=userName;
		PROXY_AUTH_PASSWORD=password;
		useProxy=true;
				
	}
	
	
	private static String  getHttpResponse(String urlStr) throws Exception {

		// Make a URL to the web page
        URL url = new URL(urlStr);
        
        
        URLConnection con = getURLConnection(url);
    con.connect(); 
        InputStream is =con.getInputStream();

        // Once you have the Input Stream, it's just plain old Java IO stuff.

        // For this case, since you are interested in getting plain-text web page
        // I'll use a reader and output the text content to System.out.

        // For binary content, it's better to directly read the bytes from stream and write
        // to the target file.


        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        StringBuffer sb =new StringBuffer();
        // read each line and write to System.out
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
	
	
	private static URLConnection getURLConnection(URL url) throws IOException {

		URLConnection con = url.openConnection();

		if (useProxy) {
			System.setProperty("http.proxyHost", PROXY_HOSTNAME);
			System.setProperty("http.proxyPort", PROXY_PORT);
			String encoded = new String(Base64.getEncoder()
					.encode((new String(PROXY_AUTH_USERNAME + ":" + PROXY_AUTH_PASSWORD).getBytes())));
			con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
		}
		else{
			
		}
		return con;
	}

}
