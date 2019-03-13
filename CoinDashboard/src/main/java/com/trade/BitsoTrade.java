package com.trade;

import javax.crypto.Mac;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.bitso.BitsoDeleteOrderVO;
import com.trade.dao.bitso.BitsoOrderResponse;
import com.trade.dao.bitso.BitsoOrderVO;
import com.trade.dao.bitso.BitsoPayload;
import com.trade.dao.bitso.BitsoResponse;
import com.trade.exception.AutoTradeException;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class BitsoTrade extends BaseTrade{

	private static final String BALANCE_ORDER = "/v3/balance/";
	private static final String EX_STATUS_OPEN = "open";
	private static final String API_ENDPOINT = "https://api.bitso.com";
	private static final String RESOURCE_ORDER = "/v3/orders/";
	private static final String API_KEY= "CjcnRrbVhS";
	private static final String SECRET_KEY="";
	public static WebTarget target = null;
	public static Mac mac = null;
	private static final String ALGORITHM="HmacSHA256";
	

	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails) {
		WebTarget target = getTarget().path(RESOURCE_ORDER);
		//nonce,httpmethod,requestPath,jsonPayload
		String symbol = getSymbolForExchange(orderDetails.getCoin(),orderDetails.getCurrency());
		String orderType= null;
		String methodType= "POST";
		long nonce = System.currentTimeMillis();
		if(orderDetails.getOrderType() == TraderConstants.BUY_CALL)
		{
			orderType="buy";			
		}
		else if(orderDetails.getOrderType() == TraderConstants.SELL_CALL){
			orderType = "sell";
		}
		String orderSubType = null;
		if(orderDetails.getOrderSubType() == TraderConstants.MARKET_ORDER){
			orderSubType = "market";
		}
		else if(orderDetails.getOrderSubType() == TraderConstants.LIMIT_ORDER){
			orderSubType = "limit";
		}
		//String jsonPayLoad = null;
	
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderType) || TradeClient.isNullorEmpty(orderSubType) || !AutoTradeUtilities.isValidMarketData(orderDetails.getQuantity())) 
		{

			orderDetails.setErrorMsg("Invalid Query Parameter found in Validation please fix the value");
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setSuccess(false);
		}
		else
		{
		//Construct json data
		JSONObject jsonObject = new JSONObject();		
		jsonObject.put("book", symbol);
		jsonObject.put("side", orderType);
		jsonObject.put("type", orderSubType);
		jsonObject.put("major", orderDetails.getQuantity());
		if(orderSubType.equalsIgnoreCase("limit") && AutoTradeUtilities.isValidMarketData(orderDetails.getOrderPrice()))
		{
		jsonObject.put("price", orderDetails.getOrderPrice());
		}
		String signatureData = nonce+methodType+RESOURCE_ORDER+jsonObject.toString();
		TradeLogger.LOGGER.info("Signature Data :"+signatureData);
		String signature = generateSignature(signatureData);
		TradeLogger.LOGGER.info(signature);
		String authorizationHeader = String.format("Bitso %s:%s:%s",API_KEY,nonce,signature);
		TradeLogger.LOGGER.finest(signature);
		TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
		Response response = target.request().header("Authorization", authorizationHeader).post(Entity.json(jsonObject.toString()));
		String returnValue = null;
		
		if(response.getStatus() == 200) 
		{
			returnValue = response.readEntity(String.class);
			TradeLogger.LOGGER.info(returnValue);
			BitsoOrderVO resObj = (BitsoOrderVO) TradeClient.getDAOObject(returnValue,BitsoOrderVO.class);
			orderDetails.setOrderId(resObj.getPayload().getOid());
			orderDetails.setTransactionTime(System.currentTimeMillis());
			orderDetails.setClientStatus(TradeStatusCode.DEFAULT_SUCCESS_CODE+"");
			orderDetails.setSuccess(true);
			
		}
		else
		{
			String errorMsg = response.readEntity(String.class);
			TradeLogger.LOGGER.severe(errorMsg);	
			BitsoResponse resObj = (BitsoResponse) TradeClient.getDAOObject(errorMsg,BitsoResponse.class);
		
			TradeLogger.LOGGER.severe("Error Code " + resObj.getError().getCode());
			TradeLogger.LOGGER.severe("Error Message " + resObj.getError().getMessage());
			
			orderDetails.setClientStatusCode(resObj.getError().getCode()+"");
			orderDetails.addErrorMessage("CLIENT MSG" +resObj.getError().getMessage());
			orderDetails.setSuccess(false);
			
		}		
		}
		return orderDetails;
	}

	
	private String generateSignature(String queryString) {
		String signature= null;
		if(!TradeClient.isNullorEmpty(queryString)) {
			signature=TradeClient.getEncodeData(getCryptoMacForBITSO(), queryString);
		}
		return signature;
	}
	
	
	public void getBalance() {
		WebTarget target  = getTarget().path(BALANCE_ORDER);

		String methodType= "GET";
		long nonce = System.currentTimeMillis();
		String signatureData = nonce+methodType +BALANCE_ORDER;
		TradeLogger.LOGGER.info("Signature Data :"+signatureData);
		String signature = generateSignature(signatureData);
		TradeLogger.LOGGER.info(signature);
		String authorizationHeader = String.format("Bitso %s:%s:%s",API_KEY,nonce,signature);
		TradeLogger.LOGGER.finest(authorizationHeader);
		Response response = target.request().header("Authorization", authorizationHeader).get();
		String returnValue = null;
		 
		if(response.getStatus() == 200) 
		{
			 returnValue = response.readEntity(String.class);
			 TradeLogger.LOGGER.info(returnValue);
			 
		}
		else {
			returnValue = response.readEntity(String.class);
			TradeLogger.LOGGER.severe(returnValue);
		}
		
	}
	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails) {
		
		WebTarget target = getTarget().path(RESOURCE_ORDER);
		if(TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.GET_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{
			long nonce = System.currentTimeMillis();
			String methodType ="GET";
			String signatureData = nonce+methodType +RESOURCE_ORDER+orderDetails.getOrderId();
			TradeLogger.LOGGER.info("Signature Data :"+signatureData);
			String signature = generateSignature(signatureData);
			TradeLogger.LOGGER.info(signature);
			String authorizationHeader = String.format("Bitso %s:%s:%s",API_KEY,nonce,signature);
			TradeLogger.LOGGER.finest(authorizationHeader);
			target= target.path(orderDetails.getOrderId());
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			Response response = target.request().header("Authorization", authorizationHeader).get();
			String returnValue = null;
			 
			if(response.getStatus() == 200) 
			{
				 returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				BitsoOrderResponse resObj = (BitsoOrderResponse) TradeClient.getDAOObject(returnValue,BitsoOrderResponse.class);
				if (resObj.getPayload()!= null && !resObj.getPayload().isEmpty()) {
					for(BitsoPayload payload : resObj.getPayload()) {
						TradeLogger.LOGGER.info(payload.getOid() + " "+ payload.getStatus() +" "+payload.getCreated_at());
						orderDetails.setClientStatus(payload.getStatus());
						orderDetails.setStatus(getATStatusFromExchangeStatus(payload.getStatus()));
						if(payload.getCreated_at()!= null && !payload.getCreated_at().isEmpty()) {
							//2019-01-02T20:54:59+0000
							orderDetails.setTransactionTime(TradeClient.getTimeInMSFromString(payload.getCreated_at(),"yyyy-MM-dd'T'HH:mm:ssZ"));
						}
					}				
					
					orderDetails.setSuccess(true);
				}
				else 
				{
					orderDetails.setSuccess(false);
					orderDetails.setClientStatusCode("INVALID ORDER ID");
					orderDetails.setClientStatus("UNKNOWN ORDER ID");
					orderDetails.addErrorMessage("AT MSG: Invalid order id passed");
				}
				
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);	
				BitsoResponse resObj = (BitsoResponse) TradeClient.getDAOObject(errorMsg,BitsoResponse.class);
			
				TradeLogger.LOGGER.severe("Error Code " + resObj.getError().getCode());
				TradeLogger.LOGGER.severe("Error Message " + resObj.getError().getMessage());
				orderDetails.setClientStatusCode(resObj.getError().getCode()+"");
				orderDetails.addErrorMessage("CLIENT MSG" +resObj.getError().getMessage());
				orderDetails.setSuccess(false);
				
			}
		}
		
		return orderDetails;
	}
	
	
	/**
	 * Cancel an order in Exchange
	 * Requires orderId from Exchange
	 */
	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {
		WebTarget target = getTarget().path(RESOURCE_ORDER);
		if(TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.DELETE_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{
			/*JSONObject jsonObject = new JSONObject();
			 * +jsonObject.toString();	*/
			long nonce = System.currentTimeMillis();
			String methodType ="DELETE";
			String signatureData = nonce+methodType +RESOURCE_ORDER+orderDetails.getOrderId();
			TradeLogger.LOGGER.info("Signature Data :"+signatureData);
			String signature = generateSignature(signatureData);
			TradeLogger.LOGGER.info(signature);
			String authorizationHeader = String.format("Bitso %s:%s:%s",API_KEY,nonce,signature);
			TradeLogger.LOGGER.finest(signature);
			target= target.path(orderDetails.getOrderId());
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			Response response = target.request().header("Authorization", authorizationHeader).delete();
			String returnValue = null;
			 
			if(response.getStatus() == 200) 
			{
				 returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				BitsoDeleteOrderVO resObj = (BitsoDeleteOrderVO) TradeClient.getDAOObject(returnValue,BitsoDeleteOrderVO.class);
				if (resObj.getPayload()!= null && !resObj.getPayload().isEmpty()) {
					//default only one orderid from server
					orderDetails.setOrderId(resObj.getPayload().get(0));
					orderDetails.setTransactionTime(System.currentTimeMillis());
					orderDetails.setClientStatus(TradeStatusCode.DEFAULT_SUCCESS_CODE + "");
					orderDetails.setSuccess(true);
				}
				else {
					orderDetails.setSuccess(false);
					orderDetails.setClientStatusCode("INVALID ORDER ID");
					orderDetails.setClientStatus("UNKNOWN ORDER ID");
					orderDetails.addErrorMessage("AT MSG: Invalid order id passed");
				}
				
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);	
				BitsoResponse resObj = (BitsoResponse) TradeClient.getDAOObject(errorMsg,BitsoResponse.class);
			
				TradeLogger.LOGGER.severe("Error Code " + resObj.getError().getCode());
				TradeLogger.LOGGER.severe("Error Message " + resObj.getError().getMessage());
				orderDetails.setClientStatusCode(resObj.getError().getCode()+"");
				orderDetails.addErrorMessage("CLIENT MSG" +resObj.getError().getMessage());
				orderDetails.setSuccess(false);
				
			}
			
			
		}
		return orderDetails;
	}
   
	private int getATStatusFromExchangeStatus(String status) {
		int returnValue = -1;
		if(status.equalsIgnoreCase(EX_STATUS_OPEN)) {
			returnValue = TraderConstants.NEW;
		}
		else if(status.equalsIgnoreCase("partial-fill")) {
			returnValue = TraderConstants.PARTIALLY_EXECUTED;
		}
		else if(status.equalsIgnoreCase("FILLED")) {
			returnValue =TraderConstants.EXECUTED;
		}
		else if(status.equalsIgnoreCase("cancelled")) {
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
	public String getSymbolForExchange(String coin, String currency) {
		if(coin == null || currency == null) {
			return null;
		}
		return coin.toLowerCase()+"_"+currency.toLowerCase();
	}
	 
	private static Mac getCryptoMacForBITSO() {
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


	public ATMarketStaticsVO getExchangePriceStatics(String symbol, String currency) throws AutoTradeException {
		// TODO Auto-generated method stub
		return null;
	}
}
