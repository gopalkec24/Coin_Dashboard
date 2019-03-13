package com.trade;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.exception.AutoTradeException;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class LivecoinTrade extends BaseTrade {

	private static final String ALGORITHM="HmacSHA256";
	public static WebTarget target = null;
	public static Mac mac = null;
	private static final String API_KEY= "X2UKCprK19ZHZqnyt66H44PuqG4A2uCM";
	private static final String SECRET_KEY="";
	private static final String API_ENDPOINT = "https://api.livecoin.net";
	private static final String RESOURCE_OPENORDER = "/exchange/client_orders";
	private static final String APIKEY_HEADER_KEY = "Api-Key";
	
	public static Mac getCryptoMacForLivecoin() {
		if(mac== null) {
			mac= TradeClient.getCryptoMac(SECRET_KEY, ALGORITHM);
		}
		return mac;
	}
	public static WebTarget getTarget() {
		if(target == null) {
			target = TradeClient.getAdvancedClient(API_ENDPOINT,true);
			TradeLogger.LOGGER.info("Initialized Newly ....");
		}
		return target;
	}
	
	public String getSymbolForExchange(String coin,String currency){
		
		if(coin == null || currency == null) {
			return null;
		}
		return coin+"/"+currency;
		
		
	}	
	
	/**
	 * Get all open order for given symbol
	 * @param symbol
	 * @return
	 */
	public String getOrderDetails(String symbol,String currency) 
	{
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_OPENORDER);
		 Map<String, String> postData = new TreeMap<String, String>();
		//symbol is not mandatory one
		if(symbol!= null && !symbol.equalsIgnoreCase("") && currency!=null && !currency.equalsIgnoreCase("")) 
		{
			 postData.put("currencyPair", getSymbolForExchange(symbol,currency));
		}	
		
		//String queryString = target.getUri().getQuery();
		//TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);
		//Generate the Signature  for query string 
		
        
		//Get the Query String to generate the signature
         String queryString = buildQueryString(postData);
         TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);	
         
         
         
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String returnValue= null;
		if(signature!= null )
		{	
			
			
			target = addParametersToRequest(postData, target);
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			Response response = target.request(MediaType.APPLICATION_JSON).header(APIKEY_HEADER_KEY,API_KEY).header("Sign", signature).get();
					
					//.header("Sign",signature).get();
			/*String encoded = new String(Base64.getEncoder()
					.encode((new String("hcltech\natarjan_g" + ":" + "Gopalms@54").getBytes())));
		
			Response response  = target.request(MediaType.APPLICATION_JSON).property("Proxy-Authorization","Basic "+encoded).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).get();*/
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
			}
			else
			{
				TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
				TradeLogger.LOGGER.severe("Response Data " + response.readEntity(String.class));
			}
		}
		return returnValue;

	}
	private String generateSignature(String queryString) {
		String signature= null;
		if(!TradeClient.isNullorEmpty(queryString)) {
			
		TradeLogger.LOGGER.info("using the livecoin method " +	byteArrayToHexString(getCryptoMacForLivecoin().doFinal(queryString.getBytes())));
			
			signature=TradeClient.getEncodeData(getCryptoMacForLivecoin(), queryString);
			signature=signature.toUpperCase();
			
			
		}
		return signature;
	}
	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails) {
		// TODO Auto-generated method stub
		return null;
	}
	private static String byteArrayToHexString(byte[] bytes) {
        final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
	private static String buildQueryString(Map<String, String> args) {
        StringBuilder result = new StringBuilder();
        for (String hashKey : args.keySet()) {
            if (result.length() > 0) result.append('&');
            try {
                result.append(URLEncoder.encode(hashKey, "UTF-8"))
                    .append("=").append(URLEncoder.encode(args.get(hashKey), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

	private static WebTarget addParametersToRequest(Map<String, String> args,WebTarget target) {
        
        for (String hashKey : args.keySet()) {
          
            try {
                target= target.queryParam(URLEncoder.encode(hashKey, "UTF-8"),URLEncoder.encode(args.get(hashKey), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return target;
    }
	public ATMarketStaticsVO getExchangePriceStatics(String symbol, String currency) throws AutoTradeException {
		// TODO Auto-generated method stub
		return null;
	}
}
