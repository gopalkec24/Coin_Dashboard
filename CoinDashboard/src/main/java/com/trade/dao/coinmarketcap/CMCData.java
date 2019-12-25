package com.trade.dao.coinmarketcap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CMCData {
	
	public int id;
	public String name;
	public String symbol;
	public String slug;
	int num_market_pairs;
	String date_added;
	public BigDecimal max_supply;
	public BigDecimal circulating_supply;
	public BigDecimal total_supply;
	public int cmc_rank;
	public String last_updated;
	public List<String> tags;	
	public CMCPlatform platform;
	
	Map<String,CMCQuotesData> quote;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getNum_market_pairs() {
		return num_market_pairs;
	}

	public void setNum_market_pairs(int num_market_pairs) {
		this.num_market_pairs = num_market_pairs;
	}

	

	public BigDecimal getMax_supply() {
		return max_supply;
	}

	public void setMax_supply(BigDecimal max_supply) {
		this.max_supply = max_supply;
	}

	public BigDecimal getCirculating_supply() {
		return circulating_supply;
	}

	public void setCirculating_supply(BigDecimal circulating_supply) {
		this.circulating_supply = circulating_supply;
	}

	public BigDecimal getTotal_supply() {
		return total_supply;
	}

	public void setTotal_supply(BigDecimal total_supply) {
		this.total_supply = total_supply;
	}

	public int getCmc_rank() {
		return cmc_rank;
	}

	public void setCmc_rank(int cmc_rank) {
		this.cmc_rank = cmc_rank;
	}

	public String getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public CMCPlatform getPlatform() {
		return platform;
	}

	public void setPlatform(CMCPlatform platform) {
		this.platform = platform;
	}

	public Map<String, CMCQuotesData> getQuote() {
		return quote;
	}

	public void setQuote(Map<String, CMCQuotesData> quote) {
		this.quote = quote;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDate_added() {
		return date_added;
	}

	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}
	
	
	

}
