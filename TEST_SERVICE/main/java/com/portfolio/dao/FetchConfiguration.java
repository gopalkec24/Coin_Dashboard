package com.portfolio.dao;

import java.util.List;
import java.util.Map;

public class FetchConfiguration {

	AttributeVO[] attributeFetch;	
	boolean securityRequired;
	String fetchURL;
	String name;
	String result;
	List<String> transCurrency;
	int allFetch;
	Map<String,String> currencyAlias;
	String getSymbolParameter;
	
	public String getGetSymbolParameter() {
		return getSymbolParameter;
	}
	public void setGetSymbolParameter(String getSymbolParameter) {
		this.getSymbolParameter = getSymbolParameter;
	}
	public Map<String, String> getCurrencyAlias() {
		return currencyAlias;
	}
	public void setCurrencyAlias(Map<String, String> currencyAlias) {
		this.currencyAlias = currencyAlias;
	}
	public int getAllFetch() {
		return allFetch;
	}
	public void setAllFetch(int allFetch) {
		this.allFetch = allFetch;
	}
	/*public boolean isAllFetch() {
		return allFetch;
	}
	public void setAllFetch(boolean allFetch) {
		this.allFetch = allFetch;
	}*/
	public List<String> getTransCurrency() {
		return transCurrency;
	}
	public void setTransCurrency(List<String> transCurrency) {
		this.transCurrency = transCurrency;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public boolean isSecurityRequired() {
		return securityRequired;
	}
	public void setSecurityRequired(boolean securityRequired) {
		this.securityRequired = securityRequired;
	}
	public String getFetchURL() {
		return fetchURL;
	}
	public void setFetchURL(String fetchURL) {
		this.fetchURL = fetchURL;
	}
	public AttributeVO[] getAttributeFetch() {
		return attributeFetch;
	}
	public void setAttributeFetch(AttributeVO[] attributeFetch) {
		this.attributeFetch = attributeFetch;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/*public List<AttributeVO> getAttributeFetch() {
		return attributeFetch;
	}
	
	public void setAttributeFetch(List<AttributeVO> attributeFetch) {
		this.attributeFetch = attributeFetch;
	}*/
	
	
}
