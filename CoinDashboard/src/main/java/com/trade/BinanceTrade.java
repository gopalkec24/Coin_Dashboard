package com.trade;

import java.math.BigDecimal;
import java.util.List;

import javax.crypto.Mac;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.trade.constants.TraderConstants;
import com.trade.dao.ATMarketStaticsVO;
import com.trade.dao.ATOrderDetailsVO;
import com.trade.dao.binance.BinanceErrorDAO;
import com.trade.dao.binance.BinanceExchangeInfo;
import com.trade.dao.binance.BinanceMarketStatics;
import com.trade.dao.binance.BinanceSuccess;
import com.trade.dao.binance.SymbolDetails;
import com.trade.exception.AutoTradeException;
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
	private static final String RESOURCE_ORDER_TEST = "/api/v3/order/test";
	private static final String RESOURCE_ORDER_DELETE = RESOURCE_ORDER;
	private static final String X_MBX_APIKEY_HEADER_KEY = "X-MBX-APIKEY";
	private static final String API_ENDPOINT = "https://api.binance.com";
	private static final String API_KEY= "";
	private static final String SECRET_KEY="";
	private static final String ALGORITHM="HmacSHA256";
	private static final String PRICE_TICKER = "/api/v1/ticker/24hr";
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
		WebTarget target = getTarget().path(RESOURCE_OPENORDER).queryParam(BINANCE_TIMESTAMP,getTimeStamp());
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
				 TradeLogger.LOGGER.fine(response.getHeaderString("content-type"));
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
		WebTarget target = getTarget().path("/api/v3/allOrders").queryParam(BINANCE_TIMESTAMP,getTimeStamp());
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
		WebTarget target = getTarget().path(RESOURCE_ORDER_DELETE).queryParam(BINANCE_TIMESTAMP,getTimeStamp());

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
	public long getTimeStamp()
	{
		WebTarget target = getTarget().path("/api/v1/time");		
		 Response response = target.request().get();
		 if(response!= null && response.getStatus() == 200) 
		 {
			 String responseStr= response.readEntity(String.class);
			 JSONObject json= new JSONObject(responseStr);
			 response.close();
			return (Long) json.get("serverTime");
		 }
		return System.currentTimeMillis()-5000;
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
				.queryParam(BINANCE_TIMESTAMP,getTimeStamp());
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		
		
		if(exchangeInfo == null) {
			exchangeInfo = getExchangeInfo();
		}
		//TODO Minimum Notational validation is required
				
				BigDecimal refinedPrice = TraderConstants.NEGATIVE_ONE;
				BigDecimal refinedStopPrice = TraderConstants.NEGATIVE_ONE;
				
				
		if(TradeClient.isNullorEmpty(symbol) || TradeClient.isNullorEmpty(orderType) || TradeClient.isNullorEmpty(orderSubType) ) 
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
			


			if(orderSubType.contains("LIMIT")) {
				target= target.queryParam("timeInForce", "GTC");
				if (orderDetails.getOrderPrice() != null) {
					refinedPrice= getRefinedPriceForBinance(exchangeInfo,symbol,orderDetails.getOrderPrice());
					TradeLogger.LOGGER.info("Refined Price is " + refinedPrice+" Actual Price  is "+orderDetails.getOrderPrice());
					target = target.queryParam("price", refinedPrice.toPlainString());
				}
			}

			if(orderDetails.getStopPrice() != null &&  (orderSubType.contains("STOP_LOSS") || orderSubType.contains("TAKE_PROFIT")))
			{
				refinedStopPrice= getRefinedPriceForBinance(exchangeInfo,symbol,orderDetails.getStopPrice());
				target=	target.queryParam("stopPrice", refinedStopPrice.toPlainString());
			}

			
			BigDecimal refinedQuantity = getRefinedQuantityForBinance(exchangeInfo,symbol,orderDetails.getQuantity(),refinedPrice);
			TradeLogger.LOGGER.finest("Refined Quantity is " + refinedQuantity+" Actual Quantity is "+orderDetails.getQuantity());
			
			if(AutoTradeUtilities.isValidMarketData(refinedQuantity))
			{
			
			
			orderDetails.setPlacedQuantity(refinedQuantity);			
			target = target.queryParam("quantity", refinedQuantity.toPlainString());
			
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
				 response.close();

			}
			mapOrderDetailsToATOrder(returnValue,error,orderDetails);
			}
			
		}


		return orderDetails;
	}
	
	public ATOrderDetailsVO placeOrderTest(ATOrderDetailsVO orderDetails) {
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
		WebTarget target = getTarget().path(RESOURCE_ORDER_TEST)				
				.queryParam(BINANCE_TIMESTAMP,getTimeStamp());
		String symbol = getSymbolForExchange(orderDetails.getCoin(), orderDetails.getCurrency());
		
		
		if(exchangeInfo == null) {
			exchangeInfo = getExchangeInfo();
		}
		//TODO Minimum Notational validation is required
		BigDecimal refinedPrice = getRefinedPriceForBinance(exchangeInfo,symbol,orderDetails.getOrderPrice());
		BigDecimal refinedQuantity = getRefinedQuantityForBinance(exchangeInfo,symbol,orderDetails.getQuantity(),refinedPrice);
		
		TradeLogger.LOGGER.info("Refined Quantity is " + refinedQuantity+" Actual Quantity is "+orderDetails.getQuantity());
		TradeLogger.LOGGER.info("Refined Price is " + refinedPrice+" Actual Price  is "+orderDetails.getOrderPrice());
		
		
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
					target = target.queryParam("price", refinedPrice.toPlainString());
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
					/*returnValue = (BinanceSuccess) getDAOObject(responseValue, BinanceSuccess.class);				
					TradeLogger.LOGGER.info(returnValue.toString());*/
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
				 response.close();

			}
			mapOrderDetailsToATOrder(returnValue,error,orderDetails);
		}


		return orderDetails;
	}
	
	private BigDecimal getMinimumNotational(BinanceExchangeInfo exchangeInfo2, String symbol) {
		BigDecimal refinedQuantity = TraderConstants.NEGATIVE_ONE;
		if(exchangeInfo2 != null) 
		{
			if(exchangeInfo2.getSymbols()!= null ) 
			{
				SymbolDetails symDetails = getSymbolDetails(exchangeInfo2.getSymbols(),symbol);
				if(!symDetails.isInitialPriceFilter()) {
					symDetails.initializeLotSize();
				}
				refinedQuantity= symDetails.getMinNotional();
			}
		}
		return refinedQuantity;
	}
	private BigDecimal getRefinedPriceForBinance(BinanceExchangeInfo exchangeInfo2,String symbol,BigDecimal orderPrice) {
		BigDecimal refinedQuantity = TraderConstants.NEGATIVE_ONE;
		if(exchangeInfo2 != null) 
		{
			if(exchangeInfo2.getSymbols()!= null ) 
			{
				SymbolDetails symDetails = getSymbolDetails(exchangeInfo2.getSymbols(),symbol);
				if(!symDetails.isInitialPriceFilter()) {
					symDetails.initializeLotSize();
				}
				refinedQuantity = orderPrice.subtract((orderPrice.subtract(symDetails.getMinPrice())).remainder(symDetails.getTickSize()));
			}
		}
		return refinedQuantity;
	}
	private BigDecimal getRefinedQuantityForBinance(BinanceExchangeInfo exchangeInfo2, String symbol, BigDecimal quantity,BigDecimal refinedPrice) {
		BigDecimal refinedQuantity = TraderConstants.NEGATIVE_ONE;
		if(exchangeInfo2 != null) 
		{
			if(exchangeInfo2.getSymbols()!= null ) 
			{
				SymbolDetails symDetails = getSymbolDetails(exchangeInfo2.getSymbols(),symbol);
				if(!symDetails.isInitialPriceFilter()) 
				{
					symDetails.initializeLotSize();
				}
				refinedQuantity = quantity.subtract((quantity.subtract(symDetails.getMinQty())).remainder(symDetails.getStepSize()));
				BigDecimal minNotational = getMinimumNotational(exchangeInfo,symbol);
				
				//Doing minNotational Value validation
				BigDecimal totalTransactAmt = TraderConstants.NEGATIVE_ONE;
				boolean increaseStep= true;
				do
				{
					totalTransactAmt = refinedPrice.multiply(refinedQuantity);
					if(totalTransactAmt.compareTo(minNotational) == TraderConstants.COMPARE_LOWER)
					{
						TradeLogger.LOGGER.info("Total Transact Amt : " + totalTransactAmt+ " is less than minNotational value  : "+ minNotational);
						TradeLogger.LOGGER.info("Increasing the refined Quantity : "+ refinedQuantity);
						refinedQuantity = refinedQuantity.add(symDetails.getStepSize());
						TradeLogger.LOGGER.info("Increased Refined Quantity :"+refinedQuantity);
					}
					else 
					{
						TradeLogger.LOGGER.info("Total Transact Amt : " + totalTransactAmt+ " is greater than minNotational value  : "+ minNotational);
						increaseStep =false;
					}
				}while(increaseStep);
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
		orderDetails.setOrderPrice(new BigDecimal(returnValue.getPrice()));
		orderDetails.setStatus(getATStatusFromExchangeStatus(returnValue.getStatus()));
		orderDetails.setClientStatus(returnValue.getStatus());
		orderDetails.setExecutedQuantity(new BigDecimal(returnValue.getExecutedQty()));
		orderDetails.setCummQuoteQty(new BigDecimal(returnValue.getCummulativeQuoteQty()));
		orderDetails.setSuccess(true);
		
		}
		else if(error!= null)
		{
			orderDetails.setClientStatusCode(error.getCode()+"");
			orderDetails.addErrorMessage("CLIENT MSG :: " + error.getMsg());
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
		 response.close();
		return exchangeInfo;
	}
	public ATOrderDetailsVO deleteOrder(ATOrderDetailsVO orderDetails) {
		

		 
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_ORDER_DELETE).queryParam(BINANCE_TIMESTAMP,getTimeStamp());
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
			else if(response.getStatus() == 407) {
				String errorMsg = response.readEntity(String.class);
				TradeLogger.LOGGER.severe(errorMsg);
				orderDetails.setClientStatus(errorMsg);
				orderDetails.setResultCode(TradeStatusCode.DELETE_ORDER_FAILURE);
				orderDetails.setSuccess(false);
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
			 response.close();
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
		WebTarget target = getTarget().path(RESOURCE_ORDER).queryParam(BINANCE_TIMESTAMP,getTimeStamp());
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
				 response.close();
				mapOrderDetailsToATOrder(success, error, orderDetails);
			}
		}
	
		return orderDetails;
	}
	
	 public ATMarketStaticsVO getExchangePriceStatics(String symbol,String currency) throws AutoTradeException 
	 {

		 ATMarketStaticsVO marketStaticsVO = new ATMarketStaticsVO();
		 marketStaticsVO.setExchangeName("BINANCE");
		 marketStaticsVO.setCurrency(currency);
		 marketStaticsVO.setSymbol(symbol);

		 if(AutoTradeUtilities.isNullorEmpty(symbol) || AutoTradeUtilities.isNullorEmpty(currency))
		 {
			 throw new AutoTradeException("symbol name or Currency cannot null value. Please pass the value." );
		 }

		 WebTarget target = getTarget().path(PRICE_TICKER);

		 String symbolForEx = getSymbolForExchange(symbol, currency);
		 target=target.queryParam("symbol",symbolForEx);

		 TradeLogger.LOGGER.fine("Final Request URL : "+  target.getUri());
		
		 Response response = target.request().get();
		 BinanceMarketStatics responseValue = null;
		 BinanceErrorDAO error = null;

		 if(response.getStatus() == 200) 
		 {
			 TradeLogger.LOGGER.finest(response.getHeaderString("content-type"));
			 responseValue = response.readEntity(BinanceMarketStatics.class);
			 mapMarKetStatics(responseValue,marketStaticsVO);
			 marketStaticsVO.setSuccess(true);
		 }
		 else
		 {
			 String errorMsg = response.readEntity(String.class);
			 TradeLogger.LOGGER.severe(errorMsg);			
			 marketStaticsVO.setSuccess(false);
			 if (response.getStatus() != 407) 
			 {
				 error = (BinanceErrorDAO) getDAOObject(errorMsg, BinanceErrorDAO.class);
				 TradeLogger.LOGGER.severe("Error Code " + error.getCode());
				 TradeLogger.LOGGER.severe("Error Message " + error.getMsg());

			 }

		 }

		 response.close();

		 return marketStaticsVO;

	 }
	private void mapMarKetStatics(BinanceMarketStatics responseValue,ATMarketStaticsVO marketStatics) {
		
		marketStatics.setPriceChange(AutoTradeUtilities.getBigDecimalValue(responseValue.getPriceChange()));
		marketStatics.setPricePercentFor24H(AutoTradeUtilities.getBigDecimalValue(responseValue.getPriceChangePercent()));
		marketStatics.setLastPrice(AutoTradeUtilities.getBigDecimalValue(responseValue.getLastPrice()));
		marketStatics.setHighPrice(AutoTradeUtilities.getBigDecimalValue(responseValue.getHighPrice()));
		marketStatics.setLowPrice(AutoTradeUtilities.getBigDecimalValue(responseValue.getLowPrice()));
		marketStatics.setExchangePrice(true);
	}
	 
	 
}
