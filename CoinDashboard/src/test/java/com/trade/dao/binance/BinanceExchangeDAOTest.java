package com.trade.dao.binance;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.trade.constants.TraderConstants;

public class BinanceExchangeDAOTest {

	public static void main(String[] args) {
		try {
			String jsonString=FileUtils.readFileToString(new File("C:/Documents/Binance_json_exchange_info.json"));
			System.out.println(jsonString);
			BinanceExchangeInfo success = (BinanceExchangeInfo) getDAOObject(jsonString, BinanceExchangeInfo.class);
			System.out.println(success.getServerTime());
			System.out.println(getRefinedQuantityForBinance(success,"BTCUSDT",new BigDecimal("0.00156486541154")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static Object getDAOObject(String errorMsg,Class target) {
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
	private static BigDecimal getRefinedQuantityForBinance(BinanceExchangeInfo exchangeInfo2, String symbol, BigDecimal quantity) {
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
	private static SymbolDetails getSymbolDetails(List<SymbolDetails> symbols, String symbol) {
		for(SymbolDetails sym : symbols) {
			if(sym!= null && sym.getSymbol().equalsIgnoreCase(symbol)) {
				return sym;
			}
		}
		return null;
	}
}
