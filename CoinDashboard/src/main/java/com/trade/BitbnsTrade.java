package com.trade;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Currency;

import javax.crypto.Mac;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.RequestBuilder;
import org.json.JSONObject;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.binance.BinanceSuccess;
import com.trade.dao.bitbns.BitbnsTickerDetailsVO;
import com.trade.dao.bitbns.BitbnsTickerVO;
import com.trade.dao.bitbns.BitbnsTimeDAO;
import com.trade.dao.bitso.BitsoOrderVO;
import com.trade.dao.bitso.BitsoResponse;
import com.trade.exception.AutoTradeException;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class BitbnsTrade extends BaseTrade{

	private static final String API_ENDPOINT = "https://api.bitbns.com";
	private static final String API_KEY= "";
	private static final String SECRET_KEY="";
	private static final String ALGORITHM="HmacSHA512";
	public static WebTarget target = null;
	public static Mac mac = null;
	
	public static final String PRICE_TICKER="/api/trade/v1/tickers";
	private static final String RESOURCE_ORDER = "/api/trade/v1";
	
	public static WebTarget getTarget() {
		if(target == null) {
			target = TradeClient.getAdvancedClientV1(API_ENDPOINT,true);
			
			TradeLogger.LOGGER.info("Initialized Newly ....");
		}
		return target;
	}
	
	private String generateSignature(String queryString) {
		String signature= null;
		if(!TradeClient.isNullorEmpty(queryString)) {
			signature=TradeClient.getEncodeData(getCryptoMacForBITBNS(), queryString);
		}
		return signature;
	}
	
	private static Mac getCryptoMacForBITBNS() {
		if(mac== null) {
			mac= TradeClient.getCryptoMac(SECRET_KEY, ALGORITHM);
		}
		return mac;
	}
	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails) {
	
		String orderType= null;
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		if(orderDetails.getOrderType() == TraderConstants.BUY_CALL && orderDetails.getOrderSubType() ==2 )
		{
			orderType="placeBuyOrder";			
		}
		else if(orderDetails.getOrderType() == TraderConstants.SELL_CALL && orderDetails.getOrderSubType() ==2 ){
			orderType = "placeSellOrder";
		}
		WebTarget getTimeTarget = getTarget().path("/api/trade/v1/getServerTime");
		
		Response timeResponse = getTimeTarget.request().header("X-BITBNS-APIKEY",API_KEY).get();
		String timeValue= null;
		String serverTime= null;
		if(timeResponse.getStatus() == 200) 
		{
			timeValue = timeResponse.readEntity(String.class);
			BitbnsTimeDAO timeDao = (BitbnsTimeDAO)TradeClient.getDAOObject(timeValue,BitbnsTimeDAO.class);
			serverTime = timeDao.getServerTime();
			TradeLogger.LOGGER.info(timeValue);
		}
		else
		{
			String errorMsg = timeResponse.readEntity(String.class);
			TradeLogger.LOGGER.severe(errorMsg);	
		}
		TradeLogger.LOGGER.fine("System time in this form" +System.currentTimeMillis() +" BNS form "+serverTime);
		TradeLogger.LOGGER.fine("Symbol "+symbol+ " "+"Currency :: "+orderType);
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderType) || TradeClient.isNullorEmpty(serverTime)) {
			//throw exception or set the Error here
			orderDetails.setErrorMsg("Invalid Query Parameter found in Validation please fix the value");
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setSuccess(false);
		}
		else
		{
			
			
			
			JSONObject body = new JSONObject();
			//body
			body.put("quantity", orderDetails.getQuantity());
			body.put("rate", orderDetails.getOrderPrice());
			
			
			//payload
			JSONObject payloadData = new JSONObject();
			payloadData.put("symbol", "/"+orderType+"/"+symbol);
			payloadData.put("timeStamp_nonce",serverTime);	
			
			payloadData.put("body", body.toString().replaceAll(" ", ""));
			
			//Creating the payload for request---> base64
			TradeLogger.LOGGER.fine(" payload Data for encoding  " +payloadData.toString());
		  String payload = null;
		try {
			payload = Base64.encodeBase64String(payloadData.toString().replaceAll(" ", "").getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  TradeLogger.LOGGER.fine("Base64 encoding as payload " +payload);
			
		   if(payload == null) {
			   
		   }
		   else {
			//Generate the Signature  for query string 
			String signature = generateSignature(payload.toString());
			 TradeLogger.LOGGER.fine("signatire " +signature);
			WebTarget target = getTarget().path(RESOURCE_ORDER)	;
			target= target.path(orderType).path(symbol);
			
			
			Entity ent = Entity.entity(body.toString(),"application/json");
			
			TradeLogger.LOGGER.fine("Final Request URL : "+ target.getUri().toString());
			Builder build= target.request()
								.header("X-BITBNS-APIKEY", API_KEY)
									.header("X-BITBNS-PAYLOAD", payload)
								.header("X-BITBNS-SIGNATURE", signature)								
								.header("Accept",MediaType.APPLICATION_JSON)
								.header("Accept-Charset","utf-8")
								.header("content-type", "application/x-www-form-urlencoded");
								
			Invocation inv = build.buildPost(ent);
			Response response =inv.invoke();
			
			String returnValue = null;
			
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				/*BitsoOrderVO resObj = (BitsoOrderVO) TradeClient.getDAOObject(returnValue,BitsoOrderVO.class);
				orderDetails.setOrderId(resObj.getPayload().getOid());
				orderDetails.setTransactionTime(System.currentTimeMillis());
				orderDetails.setClientStatus(TradeStatusCode.DEFAULT_SUCCESS_CODE+"");
				orderDetails.setSuccess(true);*/
				
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);	
				/*BitsoResponse resObj = (BitsoResponse) TradeClient.getDAOObject(errorMsg,BitsoResponse.class);
			
				TradeLogger.LOGGER.severe("Error Code " + resObj.getError().getCode());
				TradeLogger.LOGGER.severe("Error Message " + resObj.getError().getMessage());
				
				orderDetails.setClientStatusCode(resObj.getError().getCode()+"");
				orderDetails.addErrorMessage("CLIENT MSG" +resObj.getError().getMessage());
				orderDetails.setSuccess(false);*/
				
			}		
						
			//
		   }
		
		
		}
		
		return null;
	}

	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails) {

		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		WebTarget getTimeTarget = getTarget().path("/api/trade/v1/getServerTime");
		
		Response timeResponse = getTimeTarget.request().header("X-BITBNS-APIKEY",API_KEY).get();
		String timeValue= null;
		String serverTime= null;
		if(timeResponse.getStatus() == 200) 
		{
			timeValue = timeResponse.readEntity(String.class);
			BitbnsTimeDAO timeDao = (BitbnsTimeDAO)TradeClient.getDAOObject(timeValue,BitbnsTimeDAO.class);
			serverTime = timeDao.getServerTime();
			TradeLogger.LOGGER.info(timeValue);
		}
		else
		{
			String errorMsg = timeResponse.readEntity(String.class);
			TradeLogger.LOGGER.severe(errorMsg);	
		}
		
		TradeLogger.LOGGER.fine("Symbol "+symbol);
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(serverTime)) {
			//throw exception or set the Error here
			orderDetails.setErrorMsg("Invalid Query Parameter found in Validation please fix the value");
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setSuccess(false);
		}
		else
		{
			
			
			
			JSONObject body = new JSONObject();
			
			String orderType ="currentCoinBalance";
			//payload
			JSONObject payloadData = new JSONObject();
			payloadData.put("symbol", "/"+orderType+"/"+symbol);
			payloadData.put("timeStamp_nonce",serverTime);	
			
			payloadData.put("body", body.toString().replaceAll(" ", ""));
			
			//Creating the payload for request---> base64
			TradeLogger.LOGGER.fine(" payload Data for encoding  " +payloadData.toString());
		  String payload = null;
		try {
			payload = Base64.encodeBase64String(payloadData.toString().replaceAll(" ", "").getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  TradeLogger.LOGGER.fine("Base64 encoding as payload " +payload);
			
		   if(payload == null) {
			   
		   }
		   else {
			//Generate the Signature  for query string 
			String signature = generateSignature(payload.toString());
			 TradeLogger.LOGGER.fine("signatire " +signature);
			WebTarget target = getTarget().path(RESOURCE_ORDER)	;
			target= target.path(orderType).path(symbol);
			Form form = new Form();
			//header BITBNS-APIKEY
			TradeLogger.LOGGER.fine("Final Request URL : "+ target.getUri().toString());
			String userName="hcltech\natarajan_g";
			String password="Gopalsw@54";
			String auth = userName + ":" + password;
		    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
		    String authHeader = "Basic " + new String(encodedAuth);
			Response response = target.request()
								.header("X-BITBNS-APIKEY", API_KEY)
									.header("X-BITBNS-PAYLOAD", payload)
								.header("X-BITBNS-SIGNATURE", signature)								
								.header("Accept",MediaType.APPLICATION_JSON)
								.header("Accept-Charset","utf-8")
								.header(HttpHeaders.AUTHORIZATION,authHeader )
								.header("content-type", "application/x-www-form-urlencoded")
			.post(Entity.json(form));
			//= builder);
			
			String returnValue = null;
			
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				/*BitsoOrderVO resObj = (BitsoOrderVO) TradeClient.getDAOObject(returnValue,BitsoOrderVO.class);
				orderDetails.setOrderId(resObj.getPayload().getOid());
				orderDetails.setTransactionTime(System.currentTimeMillis());
				orderDetails.setClientStatus(TradeStatusCode.DEFAULT_SUCCESS_CODE+"");
				orderDetails.setSuccess(true);*/
				
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);	
				/*BitsoResponse resObj = (BitsoResponse) TradeClient.getDAOObject(errorMsg,BitsoResponse.class);
			
				TradeLogger.LOGGER.severe("Error Code " + resObj.getError().getCode());
				TradeLogger.LOGGER.severe("Error Message " + resObj.getError().getMessage());
				
				orderDetails.setClientStatusCode(resObj.getError().getCode()+"");
				orderDetails.addErrorMessage("CLIENT MSG" +resObj.getError().getMessage());
				orderDetails.setSuccess(false);*/
				
			}		
			 response.close();		
			//
		   }
		
		
		}
		
		return null;
	
		
	}

	public String getSymbolForExchange(String coin, String currency) {
		if(coin == null || currency == null) {
			return null;
		}
		if(currency.equalsIgnoreCase("INR")) {
		return coin.toUpperCase();
		}
		else
		{
			return coin.concat(currency).toUpperCase();
		}
		
	}

	public ATMarketStaticsVO getExchangePriceStatics(String symbol, String currency) throws AutoTradeException {
		 ATMarketStaticsVO marketStaticsVO = new ATMarketStaticsVO();
		 marketStaticsVO.setExchangeName("BITBNS");
		 marketStaticsVO.setCurrency(currency);
		 marketStaticsVO.setSymbol(symbol);
		 if(AutoTradeUtilities.isNullorEmpty(symbol) || AutoTradeUtilities.isNullorEmpty(currency))
		 {
			 throw new AutoTradeException("symbol name or Currency cannot null value. Please pass the value." );
		 }
		 
		 
		 WebTarget target = getTarget().path(PRICE_TICKER);
		//target =  target.path(getSymbolForExchange(symbol, currency));
		TradeLogger.LOGGER.fine(target.getUri().toString());
		Response response=  target.request().header("X-BITBNS-APIKEY",API_KEY).get();
		String returnValue = null;
		TradeLogger.LOGGER.fine(response.getStatus()+"");
		if(response.getStatus() == 200) 
		{
			
			 returnValue = response.readEntity(String.class);
			 TradeLogger.LOGGER.fine("Sucess  :: " +returnValue);
			//TradeLogger.LOGGER.severe("success" + returnValue);
			BitbnsTickerDetailsVO tickerVO= (BitbnsTickerDetailsVO) getDAOObject(returnValue, BitbnsTickerDetailsVO.class);
			if(tickerVO!= null && tickerVO.getData()!= null && !tickerVO.getData().isEmpty())
			{
				String key=symbol;
				if(!currency.equalsIgnoreCase("INR")) {
					key=key+currency;
				}
				BitbnsTickerVO data = tickerVO.getData().get(key);
				 TradeLogger.LOGGER.fine("Sucess  :: " +data.toString());
				if(data!= null) {
					mapExchangeDataToMarketStatics(data,marketStaticsVO);
				}
			}
			else {
				TradeLogger.LOGGER.fine("Invalid Data encourted...." + returnValue);
			}
		}
		else {

			String errorMsg = response.readEntity(String.class);
			TradeLogger.LOGGER.severe("Eror :" +errorMsg);	
		}

		return marketStaticsVO;
	}

	private void mapExchangeDataToMarketStatics(BitbnsTickerVO data, ATMarketStaticsVO marketStaticsVO) {
		marketStaticsVO.setLastPrice(data.getLast_traded_price());
	marketStaticsVO.setLowPrice(data.getVolume().getMin());
	marketStaticsVO.setHighPrice(data.getVolume().getMax());
	marketStaticsVO.setExchangePrice(true);
		
	}

}
