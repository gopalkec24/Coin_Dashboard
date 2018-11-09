package com.trade;

import java.io.IOException;
import java.math.BigDecimal;

import javax.crypto.Mac;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.trade.dao.BinanceErrorDAO;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;
import com.trade.utils.TradeStatusCode;

public class BinanceTrade extends BaseTrade
{

	private static final String BINANCE_SIGNATURE = "signature";
	private static final String BINANCE_TIMESTAMP = "timestamp";
	private static final String RESOURCE_OPENORDER = "/api/v3/openOrders";
	private static final String RESOURCE_ORDER = "/api/v3/order";
	private static final String RESOURCE_ORDER_DELETE = RESOURCE_ORDER;
	private static final String X_MBX_APIKEY_HEADER_KEY = "X-MBX-APIKEY";
	private static final String API_ENDPOINT = "https://api.binance.com";
	private static final String API_KEY= "QdBH7lUSI8Ya64yabxhed5tawXU0bwyGeoBrZ8dn4olAPPHroV5wP9TmEfL51T7J";
	private static final String SECRET_KEY="";
	private static final String ALGORITHM="HmacSHA256";
	public static WebTarget target = null;
	public static Mac mac = null;
	
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
	public void getMarketStatics() {
		
		if(getFetchConfigurationForExchange("BINANCE") != null) {
			TradeLogger.LOGGER.info(getFetchConfigurationForExchange("BINANCE").getFetchURL());
		}
	}

	public void buyCoin(String symbol,int subOrderType,BigDecimal quantity,BigDecimal price,BigDecimal stopPrice) {
		String type = getOrderType(subOrderType);
		placeOrder(symbol, "BUY", type, quantity, price, stopPrice);		
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
		else 
		{
			type= "LIMIT";
		
		}

		return type;
	}
	public void sellCoin(String symbol,int subOrderType,BigDecimal quantity,BigDecimal price,BigDecimal stopPrice) {
		String type = getOrderType(subOrderType);
		placeOrder(symbol, "SELL", type, quantity, price, stopPrice);
		
	}
	
	private int placeOrder(String symbol,String orderType,String subOrderType,BigDecimal quantity,BigDecimal price,BigDecimal stopPrice)
	{
		int resultCode = -1;
		//timestamp is mandatory one 		
		WebTarget target = getTarget().path(RESOURCE_ORDER)				
				.queryParam(BINANCE_TIMESTAMP,System.currentTimeMillis());

		if(!TradeClient.isNullorEmpty(symbol)) {
			target =target.queryParam("symbol", symbol);
		}
		else {
			resultCode = TradeStatusCode.INVALIDSYMBOL;
			return resultCode;
		}
		if(!TradeClient.isNullorEmpty(orderType)) {
			target=target.queryParam("side",orderType );
		}
		else
		{ 
			resultCode = TradeStatusCode.INVALID_ORDER_TYPE;
			return resultCode;
		}

		if(!TradeClient.isNullorEmpty(subOrderType)) {
			target=target.queryParam("type",subOrderType );
		}

		target = target.queryParam("quantity", quantity.toPlainString());


		if(subOrderType.contains("LIMIT")) {
			target= target.queryParam("timeInForce", "GTC");
			if (price!= null) {
				target = target.queryParam("price", price.toPlainString());
			}
		}

		if(stopPrice != null &&  (subOrderType.contains("STOP_LOSS") || subOrderType.contains("TAKE_PROFIT")))
		{
			target=	target.queryParam("stopPrice", stopPrice.toPlainString());
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
			Response response = target.request(MediaType.APPLICATION_JSON).header(X_MBX_APIKEY_HEADER_KEY,API_KEY).post(null);
			String returnValue= null;
			if(response.getStatus() == 200) 
			{
				returnValue = response.readEntity(String.class);
				TradeLogger.LOGGER.info(returnValue);
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
		return resultCode;
	}
	@SuppressWarnings("unchecked")
	private Object getDAOObject(String errorMsg,Class target) {
		Object error = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			error = mapper.readValue(errorMsg,target);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return error;
	}
	public void buyCoin() {
		// TODO Auto-generated method stub
		
	}
	public void sellCoin() {
		// TODO Auto-generated method stub
		
	}
	
}
