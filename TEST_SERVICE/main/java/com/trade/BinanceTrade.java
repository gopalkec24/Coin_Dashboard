package com.trade;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.binance.BinanceErrorDAO;
import com.trade.dao.binance.BinanceExchangeInfo;
import com.trade.dao.binance.BinanceSuccess;
import com.trade.dao.binance.SymbolDetails;
import com.trade.utils.AutoTradeUtilities;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class BinanceTrade extends BaseTrade
{


	public static final String ATSTATUS_NEW = "NEW";
	private static final String BINANCE_SIGNATURE = "signature";
	private static final String BINANCE_TIMESTAMP = "timestamp";
	private static final String RESOURCE_OPENORDER = "/api/v3/openOrders";
	private static final String RESOURCE_ORDER = "/api/v3/order";
	private static final String RESOURCE_ORDER_DELETE = RESOURCE_ORDER;
	private static final String X_MBX_APIKEY_HEADER_KEY = "X-MBX-APIKEY";
	private static final String API_ENDPOINT = "https://api.binance.com";
	private static final String API_KEY= "j5bIoyvPNkJB6NXpH8ZYbVvx2pfPFXbdKxHN3bCzbpUkxQcVLqzhn2bfbF9DmBiz";
	private static final String SECRET_KEY="";
	private static final String ALGORITHM="HmacSHA256";
	public static WebTarget target = null;
	public static Mac mac = null;
	public static BinanceExchangeInfo exchangeInfo  = null;
	
	public static Mac getCryptoMacForBinance() {
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

	

	
	private String getOrderType(int subOrderType) {
		String type = null;
		if(subOrderType == 1) {
			
			type ="MARKET";
		}
		else if(subOrderType == 2)
		{
			type ="LIMIT";
		}
		else if(subOrderType == 3) {
			type ="STOP_LOSS";
		}
		else if(subOrderType == 4) {
			type ="STOP_LOSS_LIMIT";
		}
		
		return type;
	}
	
	/**
	 * Get all open order for given symbol
	 * @param symbol
	 * @return
	 */
	public String getOrderDetails(String symbol) 
	{
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_OPENORDER).queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());
		//symbol is not mandatory one
		if(symbol!= null && !symbol.equalsIgnoreCase("")) 
		{
			target=target.queryParam("symbol",symbol);
		}	
		//Get the Query String to generate the signature
		String queryString = target.getUri().getQuery();
		TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);
		//Generate the Signature  for query string 
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String returnValue= null;
		if(signature!= null )
		{
			//append the Signature to Query Parameter
			target= target.queryParam(BINANCE_SIGNATURE, signature);	
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			
			Response response = target.request(MediaType.APPLICATION_JSON).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).get();
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
	
	/**
	 * Get the all open , inprogress ,closed order for given symbol 
	 * 
	 * @param symbol
	 * @return
	 */
	public String getAllOrderDetails(String symbol) 
	{
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path("/api/v3/allOrders").queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());
		//symbol is not mandatory one
		if(symbol!= null && !symbol.equalsIgnoreCase("")) 
		{
			target=target.queryParam("symbol",symbol);
		}	
		//Get the Query String to generate the signature
		String queryString = target.getUri().getQuery();
		TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);
		//Generate the Signature  for query string 
		String signature = generateSignature(queryString);
		TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
		String returnValue= null;
		if(signature!= null )
		{
			//append the Signature to Query Parameter
			target= target.queryParam(BINANCE_SIGNATURE, signature);	
			TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
			
			Response response = target.request(MediaType.APPLICATION_JSON).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).get();
			/*String encoded = new String(Base64.getEncoder()
					.encode((new String("hcltech\natarjan_g" + ":" + "Gopalms@54").getBytes())));
		
			Response response  = target.request(MediaType.APPLICATION_JSON).property("Proxy-Authorization","Basic "+encoded).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).get();*/
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info("Return value "+ returnValue);
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
			signature=TradeClient.getEncodeData(getCryptoMacForBinance(), queryString);
		}
		return signature;
	}

	public int deleteOrder(String symbol,String orderId) 
	{
		 int resultCode = TradeStatusCode.DELETE_ORDER_FAILURE;
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_ORDER_DELETE).queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());

		//symbol is not mandatory one
		if(!TradeClient.isNullorEmpty(symbol)) 
		{
			target=target.queryParam("symbol",symbol);
		}
		if(!TradeClient.isNullorEmpty(orderId)) {
			target =target.queryParam("orderId", orderId);
		}
		String signature = generateSignature(target.getUri().getQuery());
		if(signature != null) 
		{
			//append the Signature to Query Parameter
			target= target.queryParam(BINANCE_SIGNATURE, signature);	
			TradeLogger.LOGGER.finest("Final Request URL to delete : "+ target.getUri().toString());
			Response response = target.request().header(X_MBX_APIKEY_HEADER_KEY,API_KEY).delete();
			String returnValue = null;
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				resultCode  = TradeStatusCode.DELETE_ORDER_SUCCESS;
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);				
				BinanceErrorDAO error = (BinanceErrorDAO) getDAOObject(errorMsg,BinanceErrorDAO.class);
				TradeLogger.LOGGER.severe("Error Code " + error.getCode());
				TradeLogger.LOGGER.severe("Error Message " + error.getMsg());
				resultCode = getAutoTradeStatusCode(error.getCode());
				
			}
		}
		return resultCode;

	}
	private int getAutoTradeStatusCode(int error) {
		int resultCode = -1;
		if(error == -2011) {
			resultCode =TradeStatusCode.UNKNOWN_ORDER;
		}
		else if(error == -1013) {
			resultCode = TradeStatusCode.MINIMUM_QUANTITY_NOT_MET;
		}
		else if(error== -2010) {
			resultCode = TradeStatusCode.INSUFFICIENT_ACCOUNT_BALANCE;
		}
		else {
			resultCode = error;
		}
		return resultCode;
	}
	@SuppressWarnings("unchecked")
	private Object getDAOObject(String errorMsg,Class target) {
		Object error = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			error = mapper.readValue(errorMsg,target);
		} catch (JsonParseException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return error;
	}
	
	public String getSymbolForExchange(String coin,String currency){
		
		if(coin == null || currency == null) {
			return null;
		}
		return coin+currency;
		
		
	}
	public ATOrderDetailsVO placeOrder(ATOrderDetailsVO orderDetails) {
		String orderType= null;
		
		if(orderDetails.getOrderType() == TraderConstants.BUY_CALL)
		{
			orderType="BUY";			
		}
		else if(orderDetails.getOrderType() == TraderConstants.SELL_CALL){
			orderType = "SELL";
		}
		String orderSubType = getOrderType(orderDetails.getOrderSubType());
		/*if(orderDetails.getOrderSubType() == TraderConstants.MARKET_ORDER){
			orderSubType = "MARKET";
		}
		else if(orderDetails.getOrderSubType() == TraderConstants.LIMIT_ORDER){
			orderSubType = "LIMIT";
		}*/


		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_ORDER)				
				.queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		
		
		if(exchangeInfo == null) {
			exchangeInfo = getExchangeInfo();
		}
		//TODO Minimum Notational validation is required
		BigDecimal refinedQuantity = getRefinedQuantityForBinance(exchangeInfo,symbol,orderDetails.getQuantity());
		TradeLogger.LOGGER.info("Refined Quantity is " + refinedQuantity+" Actual Quantity is "+orderDetails.getQuantity());
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderType) || TradeClient.isNullorEmpty(orderSubType) || !AutoTradeUtilities.isValidMarketData(refinedQuantity)) 
		{

			orderDetails.setErrorMsg("Invalid Query Parameter found in Validation please fix the value");
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setSuccess(false);
		}
		else
		{
			BinanceSuccess returnValue= null;
			BinanceErrorDAO error = null;
			//setting  the Query Parameters
			target =target.queryParam("symbol", symbol);
			target=target.queryParam("side",orderType );
			target=target.queryParam("type",orderSubType);
			orderDetails.setPlacedQuantity(refinedQuantity);			
			target = target.queryParam("quantity", refinedQuantity.toPlainString());


			if(orderSubType.contains("LIMIT")) {
				target= target.queryParam("timeInForce", "GTC");
				if (orderDetails.getOrderPrice() != null) {
					target = target.queryParam("price", orderDetails.getOrderPrice().toPlainString());
				}
			}

			if(orderDetails.getStopPrice() != null &&  (orderSubType.contains("STOP_LOSS") || orderSubType.contains("TAKE_PROFIT")))
			{
				target=	target.queryParam("stopPrice", orderDetails.getStopPrice().toPlainString());
			}

			//Get the Query String to generate the signature
			String queryString = target.getUri().getQuery();
			TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);
			//Generate the Signature  for query string 
			String signature = generateSignature(queryString);
			TradeLogger.LOGGER.finest("Signature Genereated  " + signature);

			if(signature!= null )
			{
				//append the Signature to Query Parameter
				target= target.queryParam(BINANCE_SIGNATURE, signature);	
				TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
				Response response = target.request().header(X_MBX_APIKEY_HEADER_KEY,API_KEY).post(null);
				String responseValue = null;

				if(response.getStatus() == 200) 
				{
					responseValue = response.readEntity(String.class);
					TradeLogger.LOGGER.info(responseValue);
					returnValue = (BinanceSuccess) getDAOObject(responseValue, BinanceSuccess.class);				
					TradeLogger.LOGGER.info(returnValue.toString());
				}
				else
				{
					String errorMsg = response.readEntity(String.class);
					TradeLogger.LOGGER.severe(errorMsg);			

					if (response.getStatus() != 407) 
					{
						error = (BinanceErrorDAO) getDAOObject(errorMsg, BinanceErrorDAO.class);
						TradeLogger.LOGGER.severe("Error Code " + error.getCode());
						TradeLogger.LOGGER.severe("Error Message " + error.getMsg());

					}

				}

			}
			mapOrderDetailsToATOrder(returnValue,error,orderDetails);
		}


		return orderDetails;
	}
	private BigDecimal getRefinedQuantityForBinance(BinanceExchangeInfo exchangeInfo2, String symbol, BigDecimal quantity) {
		BigDecimal refinedQuantity = TraderConstants.NEGATIVE_ONE;
		if(exchangeInfo2 != null) 
		{
			if(exchangeInfo2.getSymbols()!= null ) 
			{
				SymbolDetails symDetails = getSymbolDetails(exchangeInfo2.getSymbols(),symbol);
				if(!symDetails.isInitialPriceFilter()) {
					symDetails.initializeLotSize();
				}
				refinedQuantity = quantity.subtract((quantity.subtract(symDetails.getMinQty())).remainder(symDetails.getStepSize()));
			}
		}
		return refinedQuantity;
	}
	
	private SymbolDetails getSymbolDetails(List<SymbolDetails> symbols, String symbol) {
		for(SymbolDetails sym : symbols) {
			if(sym!= null && sym.getSymbol().equalsIgnoreCase(symbol)) {
				return sym;
			}
		}
		return null;
	}
	private ATOrderDetailsVO mapOrderDetailsToATOrder(BinanceSuccess returnValue,BinanceErrorDAO error, ATOrderDetailsVO orderDetails) {
		
	//	orderDetails.set
		if(returnValue!= null) 
		{
		orderDetails.setOrderId(returnValue.getOrderId()+"");		
		if (orderDetails.getOrderType() != TraderConstants.GET_CALL) {
			orderDetails.setTransactionTime(returnValue.getTransactTime());
		}
		else 
		{
			orderDetails.setTransactionTime(returnValue.getUpdateTime());
		}
		orderDetails.setStatus(getATStatusFromExchangeStatus(returnValue.getStatus()));
		orderDetails.setClientStatus(returnValue.getStatus());
		orderDetails.setExecutedQuantity(new BigDecimal(returnValue.getExecutedQty()));
		orderDetails.setCummQuoteQty(new BigDecimal(returnValue.getCummulativeQuoteQty()));
		orderDetails.setSuccess(true);
		}
		else if(error!= null)
		{
			orderDetails.setClientStatusCode(error.getCode()+"");
			orderDetails.addErrorMessage("CLIENT MSG" + error.getMsg());
			orderDetails.setSuccess(false);
		}
		else
		{
			orderDetails.setSuccess(false);
			orderDetails.setResultCode(TradeStatusCode.UNKNOWN_ORDER);
			orderDetails.addErrorMessage("Unknown Result Both success and failure objects are null");
		}
		
		return orderDetails;
	}
	private String getStatusFromExchangeStatus(String status) {
		String returnValue = null;
		if(status.equalsIgnoreCase(ATSTATUS_NEW)) {
			returnValue = ATSTATUS_NEW;
		}
		else if(status.equalsIgnoreCase("PARTIALLY_FILLED")) {
			returnValue = "PARTIAL_EXECUTED";
		}
		else if(status.equalsIgnoreCase("FILLED")) {
			returnValue ="EXECUTED";
		}
		else if(status.equalsIgnoreCase("CANCELED")) {
			returnValue ="DELETED";
		}
		else if(status.equalsIgnoreCase("REJECTED")) {
			returnValue ="DELETED";
		}
		else if(status.equalsIgnoreCase("EXPIRED")) {
			returnValue ="EXPIRED";
		}
		return returnValue;
	}
	private int getATStatusFromExchangeStatus(String status) {
		int returnValue = -1;
		if(status.equalsIgnoreCase(ATSTATUS_NEW)) {
			returnValue = TraderConstants.NEW;
		}
		else if(status.equalsIgnoreCase("PARTIALLY_FILLED")) {
			returnValue = TraderConstants.PARTIALLY_EXECUTED;
		}
		else if(status.equalsIgnoreCase("FILLED")) {
			returnValue =TraderConstants.EXECUTED;
		}
		else if(status.equalsIgnoreCase("CANCELED")) {
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
	public BinanceExchangeInfo getExchangeInfo() {
		BinanceExchangeInfo exchangeInfo = null;
		WebTarget target = getTarget().path("/api/v1/exchangeInfo")	;
		Response response = target.request().get();
		String responseValue= null;
		if(response.getStatus() == 200)
		{
			responseValue = response.readEntity(String.class);
			TradeLogger.LOGGER.info(responseValue);
			 exchangeInfo = (BinanceExchangeInfo) getDAOObject(responseValue, BinanceExchangeInfo.class);
		}
		else {
			String errorMsg = response.readEntity(String.class);
			TradeLogger.LOGGER.severe(errorMsg);				
			BinanceErrorDAO error = null;
			if (response.getStatus() != 407) 
			{
				error = (BinanceErrorDAO) getDAOObject(errorMsg, BinanceErrorDAO.class);
				TradeLogger.LOGGER.severe("Error Code " + error.getCode());
				TradeLogger.LOGGER.severe("Error Message " + error.getMsg());
				
			}
		}
		return exchangeInfo;
	}
	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {
		

		 
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_ORDER_DELETE).queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		//symbol is not mandatory one
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.DELETE_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{
			BinanceSuccess success = null;
			BinanceErrorDAO error = null;
		target=target.queryParam("symbol",symbol);
		target =target.queryParam("orderId", orderDetails.getOrderId());
		String signature = generateSignature(target.getUri().getQuery());
		if(signature != null) 
		{
			//append the Signature to Query Parameter
			target= target.queryParam(BINANCE_SIGNATURE, signature);	
			TradeLogger.LOGGER.finest("Final Request URL to delete : "+ target.getUri().toString());
			Response response = target.request().header(X_MBX_APIKEY_HEADER_KEY,API_KEY).delete();
			String returnValue = null;
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
				success = (BinanceSuccess) getDAOObject(returnValue, BinanceSuccess.class);
				orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_SUCCESS);
				orderDetails.setSuccess(true);
			}
			else
			{
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);				
				error = (BinanceErrorDAO) getDAOObject(errorMsg,BinanceErrorDAO.class);
				TradeLogger.LOGGER.severe("Error Code " + error.getCode());
				TradeLogger.LOGGER.severe("Error Message " + error.getMsg());
				
				
				orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_FAILURE);
				orderDetails.setSuccess(false);
			}
		}
		mapOrderDetailsToATOrder(success,error,orderDetails);
		}
		
		return orderDetails;
	}
	
	public ATOrderDetailsVO getOrderStatus(ATOrderDetailsVO orderDetails) 
	{


		//timestamp is mandatory one 	
		//
		//"/api/v3/myTrades"
		WebTarget target = getTarget().path(RESOURCE_ORDER).queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderDetails.getOrderId()) || orderDetails.getOrderType() != TraderConstants.GET_CALL ) 
		{
			orderDetails.setResultCode(TradeStatusCode.INVALIDSYMBOL);
			orderDetails.setErrorMsg("Invalid Parameter passed . Please fix the parameter with correct values");
			orderDetails.setSuccess(false);
			
		}
		else
		{	
			target=target.queryParam("symbol",symbol);
			target=target.queryParam("orderId", orderDetails.getOrderId());
			//Get the Query String to generate the signature
			String queryString = target.getUri().getQuery();
			TradeLogger.LOGGER.finest("QueryString to generate the signature " + queryString);
			//Generate the Signature  for query string 
			String signature = generateSignature(queryString);
			TradeLogger.LOGGER.finest("Signature Genereated  " + signature);
			String returnValue= null;
			if(signature!= null )
			{
				//append the Signature to Query Parameter
				target= target.queryParam(BINANCE_SIGNATURE, signature);	
				TradeLogger.LOGGER.finest("Final Request URL : "+ target.getUri().toString());
				BinanceSuccess success = null;
				BinanceErrorDAO error = null;
				Response response = target.request(MediaType.APPLICATION_JSON).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).get();				
				if(response.getStatus() == 200) 
				{
					returnValue = response.readEntity(String.class);
					TradeLogger.LOGGER.info("GET ORDER STATUS "+ returnValue);
					success = (BinanceSuccess) getDAOObject(returnValue, BinanceSuccess.class);
					orderDetails.setResultCode(TradeStatusCode.FETCH_SUCCESS_CODE);
					orderDetails.setSuccess(true);
				}
				else
				{
					String errorMsg = response.readEntity(String.class);
					TradeLogger.LOGGER.severe("Response Code " + response.getStatus());
					TradeLogger.LOGGER.severe("Response Data " + errorMsg);
					error = (BinanceErrorDAO) getDAOObject(errorMsg,BinanceErrorDAO.class);
					TradeLogger.LOGGER.severe("Error Code " + error.getCode());
					TradeLogger.LOGGER.severe("Error Message " + error.getMsg());
					
					
					orderDetails.setResultCode(TradeStatusCode.FETCH_FAILURE_CODE);
					orderDetails.setSuccess(false);
				}
				mapOrderDetailsToATOrder(success, error, orderDetails);
			}
		}
	
		return orderDetails;
	}
}
