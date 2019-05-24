package com.trade;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.livecoin.LivecoinOrderResponseVO;
import com.trade.dao.livecoin.LivecoinOrderStatusVO;
import com.trade.dao.livecoin.LivecoinPriceStatusVO;
import com.trade.exception.AutoTradeException;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class LivecoinTrade extends BaseTrade {

	private static final String EXCHANGE_NAME = "LIVECOIN";
	private static final String EXCHANGE_TICKER_API = "/exchange/ticker";
	private static final String SELLMARKET_ORDER_API = "/exchange/sellmarket";
	private static final String BUYMARKET_ORDER_API = "/exchange/buymarket";
	private static final String SELLLIMIT_ORDER_API = "/exchange/selllimit";
	private static final String BUYLIMIT_ORDER_API = "/exchange/buylimit";
	private static final String CANCEL_ORDER_API = "/exchange/cancellimit";
	private static final String ORDER_ID = "orderId";
	private static final String CURRENCY_PAIR = "currencyPair";
	private static final String ALGORITHM="HmacSHA256";
	public static WebTarget target = null;
	public static Mac mac = null;
	private static final String API_KEY= "";
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
			 postData.put(CURRENCY_PAIR, getSymbolForExchange(symbol,currency));
		}	
		
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
		WebTarget target = getTarget();
		if(orderDetails.getOrderType() == TraderConstants.BUY_CALL && orderDetails.getOrderSubType() == TraderConstants.LIMIT_ORDER) {
			target = target.path(BUYLIMIT_ORDER_API);
		}
		else if(orderDetails.getOrderType() == TraderConstants.SELL_CALL && orderDetails.getOrderSubType() ==TraderConstants.LIMIT_ORDER) {
			
			target = target.path(SELLLIMIT_ORDER_API);
		}
		else if(orderDetails.getOrderType() == TraderConstants.BUY_CALL && orderDetails.getOrderSubType() == TraderConstants.MARKET_ORDER) {
			target = target.path(BUYMARKET_ORDER_API);
		}
		else if(orderDetails.getOrderType() == TraderConstants.SELL_CALL && orderDetails.getOrderSubType() ==TraderConstants.MARKET_ORDER) {
			target = target.path(SELLMARKET_ORDER_API);
			
		}
		 Map<String, String> postData = new TreeMap<String, String>();
		
		//symbol is  mandatory one
		if(orderDetails.getCoin()!= null && !orderDetails.getCoin().equalsIgnoreCase("") && orderDetails.getCurrency()!=null && !orderDetails.getCurrency().equalsIgnoreCase("")) 
		{
			postData.put(CURRENCY_PAIR, getSymbolForExchange(orderDetails.getCoin(),orderDetails.getCurrency()));
			postData.put("price",orderDetails.getOrderPrice().toPlainString());
			postData.put("quantity", orderDetails.getQuantity().toPlainString());
			
		}	
		 String queryString = buildQueryString(postData);
         TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);	
         
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String responseValue= null;
		
		if(signature!= null )
		{	
			target = addParametersToRequest(postData, target);
			TradeLogger.LOGGER.info("Final Request URL : "+ target.getUri().toString());
			Response response = target.request().header(APIKEY_HEADER_KEY,API_KEY).header("Sign", signature).post(Entity.entity(queryString, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
				
			if(response.getStatus() == 200) 
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(responseValue);
				LivecoinOrderResponseVO returnValue= null;
				 returnValue = (LivecoinOrderResponseVO) getDAOObject(responseValue, LivecoinOrderResponseVO.class);				
				 if (returnValue.isSuccess()) 
				 {
					orderDetails.setTransactionTime(System.currentTimeMillis());
					orderDetails.setClientStatus(TradeStatusCode.DEFAULT_SUCCESS_CODE + "");
					orderDetails.setOrderId(returnValue.getOrderId());
					orderDetails.setSuccess(true);
				}
				 else 
				 {
					 orderDetails.setSuccess(false);
					 orderDetails.addErrorMessage(returnValue.getException());
				 }
				TradeLogger.LOGGER.info(returnValue.toString());
			}
			else
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
				TradeLogger.LOGGER.severe("Response Data " + responseValue);
				orderDetails.setSuccess(false);
				orderDetails.setClientStatus(TradeStatusCode.DEFAULT_ERROR_CODE+"");
				orderDetails.addErrorMessage(responseValue);
			}
		}
		return orderDetails;
	}

	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {

		
		if(TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.DELETE_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{
			
			WebTarget target = getTarget().path(CANCEL_ORDER_API);
			
		 Map<String, String> postData = new TreeMap<String, String>();
		
		//symbol is  mandatory one
		if(orderDetails.getCoin()!= null && !orderDetails.getCoin().equalsIgnoreCase("") && orderDetails.getCurrency()!=null && !orderDetails.getCurrency().equalsIgnoreCase("")) 
		{
			postData.put(CURRENCY_PAIR, getSymbolForExchange(orderDetails.getCoin(),orderDetails.getCurrency()));
			
			postData.put(ORDER_ID, orderDetails.getOrderId());
			
		}	
		 String queryString = buildQueryString(postData);
         TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);	
         
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String responseValue= null;
		
		if(signature!= null )
		{	
			target = addParametersToRequest(postData, target);
			TradeLogger.LOGGER.info("Final Request URL : "+ target.getUri().toString());
			Response response = target.request().header(APIKEY_HEADER_KEY,API_KEY).header("Sign", signature).post(Entity.entity(queryString, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
				
			if(response.getStatus() == 200) 
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(responseValue);
				LivecoinOrderResponseVO returnValue= null;
				 returnValue = (LivecoinOrderResponseVO) getDAOObject(responseValue, LivecoinOrderResponseVO.class);				
				 if (returnValue.isSuccess() && returnValue.isCancelled()) 
				 {
					orderDetails.setTransactionTime(System.currentTimeMillis());
					orderDetails.setClientStatus(TradeStatusCode.DELETE_ORDER_SUCCESS + "");					
					orderDetails.setSuccess(true);
					orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_SUCCESS);
				}
				 else
				 {
					 orderDetails.setSuccess(false);
					 orderDetails.addErrorMessage(returnValue.getException());
					 orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_FAILURE);
				 }
				TradeLogger.LOGGER.info(returnValue.toString());
			}
			else
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
				TradeLogger.LOGGER.severe("Response Data " + responseValue);
				
				orderDetails.setSuccess(false);
				orderDetails.setClientStatus(TradeStatusCode.DEFAULT_ERROR_CODE + "");
				orderDetails.addErrorMessage(responseValue);
				 orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_FAILURE);
			}
		}
		}
		return orderDetails;
	
		
	}

	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails) 
	{
		if(TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.GET_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{
			
			WebTarget target = getTarget().path("/exchange/order");
			
		 Map<String, String> postData = new TreeMap<String, String>();
		
		//symbol is  mandatory one
		if(orderDetails.getCoin()!= null && !orderDetails.getCoin().equalsIgnoreCase("") && orderDetails.getCurrency()!=null && !orderDetails.getCurrency().equalsIgnoreCase("")) 
		{
			postData.put(ORDER_ID, orderDetails.getOrderId());
		}	
		 String queryString = buildQueryString(postData);
         TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);	
         
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String responseValue= null;
		
		if(signature!= null )
		{	
			target = addParametersToRequest(postData, target);
			TradeLogger.LOGGER.info("Final Request URL : "+ target.getUri().toString());
			Response response = target.request().header(APIKEY_HEADER_KEY,API_KEY).header("Sign", signature).get();
				
			if(response.getStatus() == 200) 
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(responseValue);
				LivecoinOrderStatusVO returnValue= null;
				 returnValue = (LivecoinOrderStatusVO) getDAOObject(responseValue, LivecoinOrderStatusVO.class);				
					orderDetails.setSuccess(true);
					orderDetails.setResultCode(TradeStatusCode.FETCH_SUCCESS_CODE);
					
					mapOrderStatusToATOrderDetailsVO(returnValue,orderDetails);
				
			//	TradeLogger.LOGGER.info(returnValue.toString());
			}
			else
			{
				responseValue = response.readEntity(String.class);
				TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
				TradeLogger.LOGGER.severe("Response Data " + responseValue);
				
				orderDetails.setSuccess(false);
				orderDetails.setClientStatus(TradeStatusCode.FETCH_FAILURE_CODE + "");
				orderDetails.addErrorMessage(responseValue);
				 orderDetails.setResultCode(TradeStatusCode.FETCH_FAILURE_CODE);
			}
		}
		}
		return orderDetails;
	
	}
	private void mapOrderStatusToATOrderDetailsVO(LivecoinOrderStatusVO returnValue, ATOrderDetailsVO orderDetails) {

			if(returnValue!= null) {
				orderDetails.setOrderId(returnValue.getId());
				orderDetails.setStatus(getATStatusFromExchangeStatus(returnValue.getStatus()));
				//TODO this could cause issue , in cancellingthe order
				orderDetails.setTransactionTime(-1);
				orderDetails.setClientStatus(returnValue.getStatus());
				orderDetails.setExecutedQuantity(returnValue.getQuantity().subtract(returnValue.getRemaining_quantity()));
				orderDetails.setOrderPrice(returnValue.getPrice());
				orderDetails.setCummQuoteQty(returnValue.getQuantity());
			}
		
	}
	
	private int getATStatusFromExchangeStatus(String status) {
		int returnValue = -1;
		
		if(status.equalsIgnoreCase("partial-fill")) {
			returnValue = TraderConstants.PARTIALLY_EXECUTED;
		}
		else if(status.equalsIgnoreCase("OPEN")) {
			returnValue = TraderConstants.NEW;
		}
		else if(status.equalsIgnoreCase("EXECUTED")) {
			returnValue =TraderConstants.EXECUTED;
		}
		else if(status.equalsIgnoreCase("CANCELLED")) {
			returnValue = TraderConstants.DELETED;
		}
		else if(status.equalsIgnoreCase("REJECTED")) {
			returnValue = TraderConstants.DELETED;
		}
		else if(status.equalsIgnoreCase("EXPIRED")) {
			returnValue = TraderConstants.EXPIRED;
		}
		return returnValue;
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
		
		ATMarketStaticsVO marketStaticsVO = new ATMarketStaticsVO();
		 marketStaticsVO.setExchangeName(EXCHANGE_NAME);
		 marketStaticsVO.setCurrency(currency);
		 marketStaticsVO.setSymbol(symbol);
		WebTarget target = getTarget().path(EXCHANGE_TICKER_API);
		 Map<String, String> postData = new TreeMap<String, String>();
		//symbol is not mandatory one
		if(symbol!= null && !symbol.equalsIgnoreCase("") && currency!=null && !currency.equalsIgnoreCase("")) 
		{
			 postData.put(CURRENCY_PAIR, getSymbolForExchange(symbol,currency));
		}	
		
        String queryString = buildQueryString(postData);
        TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);	
        
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		
		if(signature!= null )
		{	
			target = addParametersToRequest(postData, target);
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			Response response = target.request(MediaType.APPLICATION_JSON).header(APIKEY_HEADER_KEY,API_KEY).header("Sign", signature).get();
			
			String responseValue= response.readEntity(String.class);;
			if(response.getStatus() == 200) 
			{
				LivecoinPriceStatusVO returnValue= null;
				TradeLogger.LOGGER.info("Response Data " + responseValue);
				 returnValue = (LivecoinPriceStatusVO) getDAOObject(responseValue, LivecoinPriceStatusVO.class);		
				mapMarKetStatics(returnValue, marketStaticsVO);
				marketStaticsVO.setSuccess(true);
			}
			else
			{
				TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
				TradeLogger.LOGGER.severe("Response Data " + responseValue);
				marketStaticsVO.setSuccess(false);
				marketStaticsVO.setErrorMsg(responseValue);
			}
		}
		return marketStaticsVO;

	}
	
	private void mapMarKetStatics(LivecoinPriceStatusVO responseValue, ATMarketStaticsVO marketStaticsVO) {
		
		
		//only percentage base Trading is possible
		
		marketStaticsVO.setLastPrice(responseValue.getLast());
		marketStaticsVO.setHighPrice(responseValue.getHigh());
		marketStaticsVO.setLowPrice(responseValue.getLow());
		marketStaticsVO.setExchangePrice(true);
		
		
	}

}
