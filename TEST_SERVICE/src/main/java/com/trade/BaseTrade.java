package com.trade;

import java.util.Map;


import com.portfolio.dao.FetchConfiguration;
import com.portfolio.utilis.GetCurrentRate;
import com.trade.utils.AutoTradeConfigReader;

public abstract class BaseTrade implements ITrade {

	private final static Map<String,FetchConfiguration> configValue = GetCurrentRate.getExchangeConfigDataV2((String) AutoTradeConfigReader.getConfigValues().get("tickerConfigFile"));

	
 
 public FetchConfiguration getFetchConfigurationForExchange(String exchangeName) {		
		return configValue.get(exchangeName);
	}
	 

}
