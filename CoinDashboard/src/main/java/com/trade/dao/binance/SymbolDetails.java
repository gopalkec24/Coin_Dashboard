package com.trade.dao.binance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.trade.constants.TraderConstants;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SymbolDetails {

	String symbol;
	String status;
	String baseAsset;
	String quoteAsset;
	int quotePrecision;
	List<String>  orderTypes;
	boolean icebergAllowed;
	boolean isSpotTradingAllowed;
	boolean isMarginTradingAllowed;
	
	
	public boolean isMarginTradingAllowed() {
		return isMarginTradingAllowed;
	}

	public void setMarginTradingAllowed(boolean isMarginTradingAllowed) {
		this.isMarginTradingAllowed = isMarginTradingAllowed;
	}

	public boolean isSpotTradingAllowed() {
		return isSpotTradingAllowed;
	}

	public void setSpotTradingAllowed(boolean isSpotTradingAllowed) {
		this.isSpotTradingAllowed = isSpotTradingAllowed;
	}

	BigDecimal minQty;
	BigDecimal maxQty;
	BigDecimal stepSize;
	BigDecimal minNotional;
	boolean initialPriceFilter;
	
	BigDecimal minPrice;
	BigDecimal maxPrice;
	BigDecimal tickSize;

	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public BigDecimal getTickSize() {
		return tickSize;
	}

	public void setTickSize(BigDecimal tickSize) {
		this.tickSize = tickSize;
	}

	public BigDecimal getMinQty() {
		return minQty;
	}

	public void setMinQty(BigDecimal minQty) {
		this.minQty = minQty;
	}

	public BigDecimal getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(BigDecimal maxQty) {
		this.maxQty = maxQty;
	}

	public BigDecimal getStepSize() {
		return stepSize;
	}

	public void setStepSize(BigDecimal stepSize) {
		this.stepSize = stepSize;
	}

	public BigDecimal getMinNotional() {
		return minNotional;
	}

	public void setMinNotional(BigDecimal minNotional) {
		this.minNotional = minNotional;
	}

	public boolean isInitialPriceFilter() {
		return initialPriceFilter;
	}

	public void setInitialPriceFilter(boolean initialPriceFilter) {
		this.initialPriceFilter = initialPriceFilter;
	}

	List<Map<String,Object>> filters;
	int baseAssetPrecision;

	public String getSymbol() {
		return symbol;
	}

	public int getBaseAssetPrecision() {
		return baseAssetPrecision;
	}

	public void setBaseAssetPrecision(int baseAssetPrecision) {
		this.baseAssetPrecision = baseAssetPrecision;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBaseAsset() {
		return baseAsset;
	}

	public void setBaseAsset(String baseAsset) {
		this.baseAsset = baseAsset;
	}

	public String getQuoteAsset() {
		return quoteAsset;
	}

	public void setQuoteAsset(String quoteAsset) {
		this.quoteAsset = quoteAsset;
	}

	public int getQuotePrecision() {
		return quotePrecision;
	}

	public void setQuotePrecision(int quotePrecision) {
		this.quotePrecision = quotePrecision;
	}

	public List<String> getOrderTypes() {
		return orderTypes;
	}

	public void setOrderTypes(List<String> orderTypes) {
		this.orderTypes = orderTypes;
	}

	public boolean isIcebergAllowed() {
		return icebergAllowed;
	}

	public void setIcebergAllowed(boolean icebergAllowed) {
		this.icebergAllowed = icebergAllowed;
	}

	public List<Map<String, Object>> getFilters() {
		return filters;
	}

	public void setFilters(List<Map<String, Object>> filters) {
		this.filters = filters;
		initializeLotSize();
	}
	
	
	
	public void initializeLotSize() 
	{
		
		if (!this.initialPriceFilter) {
			for (Map<String, Object> filter : this.filters) {

				if (filter.containsKey("filterType")) {
					String value = (String) filter.get("filterType");
					if (value.equalsIgnoreCase("LOT_SIZE")) {
						this.minQty = getBigDecimalValue(filter.get("minQty"));
						this.maxQty = getBigDecimalValue(filter.get("maxQty"));
						this.stepSize = getBigDecimalValue(filter.get("stepSize"));
						this.initialPriceFilter = true;
					} else if (value.equalsIgnoreCase("PRICE_FILTER")) {
						this.minPrice = getBigDecimalValue(filter.get("minPrice"));
						this.maxPrice = getBigDecimalValue(filter.get("maxPrice"));
						this.tickSize = getBigDecimalValue(filter.get("tickSize"));
						this.initialPriceFilter = true;
					}
					else if(value.equalsIgnoreCase("MIN_NOTIONAL")) {
						this.minNotional = getBigDecimalValue(filter.get("minNotional"));
						this.initialPriceFilter = true;
					}

				}
			} 
		}
		
	}

	private BigDecimal getBigDecimalValue(Object object) {
		BigDecimal validValue = TraderConstants.NEGATIVE_ONE;
		if(object instanceof String) {
			validValue = new BigDecimal((String)object);
		}
		else if(object instanceof Integer) {
			validValue = new BigDecimal((Integer)object);
		}
		
		return validValue;
	}

	public String toString() {
		
		return this.symbol+" "+this.status+ " " + this.filters.toString();
	}
	
	
	
}
