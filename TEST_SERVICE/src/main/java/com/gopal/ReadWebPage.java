package com.gopal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.gopal.currency.ConcurrencyConvertResponseVO;
import com.gopal.currency.CurrencyVO;
import com.gopal.CoinSettingVO;

public class ReadWebPage {

	public static void main(String[] args) throws Exception 
	{
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	    
	   
	    
	    readCoinSetting();
		
		
		
		

	}

	public static Map<String, CoinSettingVO> readCoinSetting() throws Exception, IOException, MalformedURLException {
		List<String>  currencies = new ArrayList<String>();
		currencies.add("INR");
		currencies.add("MXN");
		CurrencyVO currentRateVO = getCurrentCurrencyRate(currencies);

		List<String> coinList = getCoinList();			
		 Map<String,CoinSettingVO> coinSettingVOMap  = new TreeMap<String, CoinSettingVO>();
		for(String coindetails : coinList) {
			System.out.println(coindetails);
			String[] coins= coindetails.split("\\s+");
			int i=0;
			CoinSettingVO settingVO = new CoinSettingVO();
			String key = null;
			int valueIndex = -1;
			for(String co: coins) {
				
					if(co.indexOf("/") > 0) {
						key = co.substring(0, co.indexOf("/"));
						System.out.println("COin Name : "+key);
						settingVO.setCoinName(key);
						valueIndex = i +2;
					}				
					
					else if(co.indexOf("$") == 0 && valueIndex == i) {
						System.out.println(Double.parseDouble(co.substring(1)));
						settingVO.setCurrentRate(Double.parseDouble(co.substring(1)) * currentRateVO.getUSDINR());
					}
				
				i++;
			}
			coinSettingVOMap.put(key, settingVO);
		}
		return coinSettingVOMap;
	}    
	
	
	private static CurrencyVO getCurrentCurrencyRate(List<String> currencies) throws Exception {
		
		//Map<String,Double> currCurrrencyRate = new HashMap<String,Double>();
		
		StringBuffer list = new StringBuffer();
		
		for(String currency : currencies) {
			list.append(currency);
			list.append(",");
		}
		System.out.println(list.toString());
		list = list.length()>0? list.deleteCharAt(list.length()-1):list;
 		System.out.println(list.toString());
		String baseURL="http://www.apilayer.net/api/live?access_key=06773261e0b8ea23e9f73178f8a5d422&format=1&currencies=";			
		baseURL=baseURL.concat(list.toString());		
		String responseValue = getHttpResponse(baseURL);
		System.out.println(baseURL);
		if(responseValue != null ) {
			System.out.println(responseValue);
		}
		ConcurrencyConvertResponseVO cuVO = new ConcurrencyConvertResponseVO();
		Gson gson= new Gson();
		cuVO = gson.fromJson(responseValue,ConcurrencyConvertResponseVO.class);
	
		CurrencyVO conVO= cuVO.getQuotes();
		
		System.out.println("Gopal Check" +conVO.getUSDINR());
		
		return conVO;
		
		
	}

	

	
	private static List<String> getCoinList() throws IOException, MalformedURLException {
		@SuppressWarnings("resource")
		WebClient webClient = new WebClient(BrowserVersion.CHROME);	
		
		ProxyConfig proxyConfig = new ProxyConfig("10.121.11.33", 8080);				
		webClient.getOptions().setProxyConfig(proxyConfig);	
			
		DefaultCredentialsProvider cp= (DefaultCredentialsProvider) webClient.getCredentialsProvider();
		cp.addCredentials("hcltech\natarajan_g","Gopalelk@54");
		
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		HtmlPage page  = webClient.getPage("https://coinmarketcap.com/exchanges/koinex/");
		String pageContent= page.asText();
		String[] lines = pageContent.split("\\r?\\n");
		boolean detectedStart= false;
		boolean detectedEnd = false;
		List<String> coinList = new ArrayList<String>();
		for(String line: lines )
		{
			if(!detectedStart && line.startsWith("#")) {
				detectedStart =true;
				continue;
			}
			else if(detectedStart && line.contains("CoinMarketCap")) {
				break;
			}

			if(detectedStart && !detectedEnd) {
				coinList.add(line);
			}


		}
		return coinList;
	}

	private static String  getHttpResponse(String urlStr) throws Exception {

		// Make a URL to the web page
        URL url = new URL(urlStr);
        
        
        System.setProperty("http.proxyHost", "10.121.11.33");
        System.setProperty("http.proxyPort", "8080");
       /* System.setProperty("http.proxyUser", "hcltech\\natarajan_g");
        System.setProperty("http.proxyPassword", "Gopalelk@54");*/
    URLConnection con = url.openConnection();
    String encoded = new String
    	      (Base64.getEncoder().encode((new String("hcltech\\natarajan_g:Gopalelk@54").getBytes())));
    con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
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
		
	
	
}

	

