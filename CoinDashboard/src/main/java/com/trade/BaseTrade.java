package com.trade;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.portfolio.dao.FetchConfiguration;
import com.portfolio.utilis.GetCurrentRate;
import com.trade.utils.AutoTradeConfigReader;

public abstract class BaseTrade implements ITrade {

	private final static Map<String,FetchConfiguration> configValue =  null;// GetCurrentRate.getExchangeConfigDataV2((String) AutoTradeConfigReader.getConfigValues().get("tickerConfigFile"));

	
 
 public FetchConfiguration getFetchConfigurationForExchange(String exchangeName) {		
		return configValue.get(exchangeName);
	}
	 
 @SuppressWarnings("unchecked")
	protected Object getDAOObject(String errorMsg,Class target) {
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
}
