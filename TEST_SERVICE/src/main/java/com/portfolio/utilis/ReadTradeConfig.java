package com.portfolio.utilis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;

import com.portfolio.dao.TransactionDetailsVO;

public class ReadTradeConfig {

	private static final String CONFIG_VALUE_SEPERATOR = "_";
	private static final String KEY_VALUE_PAIR_SEPERATOR = "=";
	private Hashtable<String,List<String>> tradeConfig = new Hashtable<String,List<String>>();
	List<String> nonCryptoList= new ArrayList<String>();
	String COMMA_SEPEARTOR = ",";
	
	private static String PROXY_AUTH_PASSWORD = "Gopalmp@54";
	private static String PROXY_AUTH_USERNAME = "hcltech\\natarajan_g";
	private static String PROXY_PORT = "8080";
	private static String PROXY_HOSTNAME = "10.121.11.32";
	private static boolean useProxy = true;

	public static final boolean useLatestVersion = true;
	public static List<String> excludedCoinList = new ArrayList<String>();
	
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
	public String getNonCryptoListAsString(){
		return nonCryptoList.toString().substring(1,nonCryptoList.toString().length()-1);
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
	
	
	
	
	public static URLConnection getURLConnection(URL url) throws IOException {

		URLConnection con = url.openConnection();

		if (useProxy) {
			System.setProperty("http.proxyHost", PROXY_HOSTNAME);
			System.setProperty("http.proxyPort", PROXY_PORT);
			System.setProperty("https.proxyHost", PROXY_HOSTNAME);
			System.setProperty("https.proxyPort", PROXY_PORT);
			/*System.out.println("USer naem : "+PROXY_AUTH_USERNAME);
			System.out.println("PAssword : "+PROXY_AUTH_PASSWORD);*/
			String encoded = new String(Base64.getEncoder()
					.encode((new String(PROXY_AUTH_USERNAME + ":" + PROXY_AUTH_PASSWORD).getBytes())));
			System.out.println(encoded);
			con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
			System.out.println("using the proxy");
		}		
		return con;
	}
	public  void setProxyDetails(String hostName,String portNumber,String userName,String password){
		PROXY_HOSTNAME=hostName;
		PROXY_PORT=portNumber;
		PROXY_AUTH_USERNAME=userName;
		PROXY_AUTH_PASSWORD=password;
		useProxy=true;
				
	}


	public static URLConnection getURLConnection(String endpointURL) throws IOException {
		URL url= new URL(endpointURL);
		return getURLConnection(url);
	}
	public void setUseProxy(boolean value) {
		useProxy=value;
	}
	
	public static HttpURLConnection getHttpURLConnection(String endPoint) throws IOException  {
		
		HttpURLConnection conn= (HttpURLConnection) getURLConnection(endPoint);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		return conn;
	}

}
