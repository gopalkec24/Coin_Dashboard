package com.portfolio.dao;

import java.math.BigDecimal;

public class CryptoTransactionVO {
	
	private static final BigDecimal BIG_DECIMAL_ZERO = new BigDecimal("0");
	
	//coin volume
	BigDecimal initialVol= BIG_DECIMAL_ZERO;
	BigDecimal finalVol =BIG_DECIMAL_ZERO;
	
	//transaction coin volume
	BigDecimal initialTradeVol= BIG_DECIMAL_ZERO;
	BigDecimal finalTradeVol= BIG_DECIMAL_ZERO;
	
	public BigDecimal getInitialTradeVol() {
		return initialTradeVol;
	}
	public void setInitialTradeVol(BigDecimal initialTradeVol) {
		this.initialTradeVol = initialTradeVol;
	}
	public BigDecimal getFinalTradeVol() {
		return finalTradeVol;
	}
	public void setFinalTradeVol(BigDecimal finalTradeVol) {
		this.finalTradeVol = finalTradeVol;
	}
	public BigDecimal getInitialCommissionVol() {
		return initialCommissionVol;
	}
	public void setInitialCommissionVol(BigDecimal initialCommissionVol) {
		this.initialCommissionVol = initialCommissionVol;
	}
	public BigDecimal getFinalCommissionVol() {
		return finalCommissionVol;
	}
	public void setFinalCommissionVol(BigDecimal finalCommissionVol) {
		this.finalCommissionVol = finalCommissionVol;
	}
	//commission coin Volume
	BigDecimal initialCommissionVol = BIG_DECIMAL_ZERO;
	BigDecimal finalCommissionVol= BIG_DECIMAL_ZERO;
		
	//Traded coin volume and commission value
	BigDecimal commissionValue = BIG_DECIMAL_ZERO;
	BigDecimal tradeVol = BIG_DECIMAL_ZERO;
	
	
	//calculated transaction Amt and Effective traded Vol
	BigDecimal effectiveTradeVol= BIG_DECIMAL_ZERO;
	BigDecimal transactionAmt = BIG_DECIMAL_ZERO;
	
	String transactionType;
	String commissionType;	
	String coinName;
	String transactionCoin;
	String commissionCoin;
	String xchangeName;
	
	public String getXchangeName() {
		return xchangeName;
	}
	public void setXchangeName(String xchangeName) {
		this.xchangeName = xchangeName;
	}
	public BigDecimal getInitialVol() {
		return initialVol;
	}
	public void setInitialVol(BigDecimal initialVol) {
		this.initialVol = initialVol;
	}
	public BigDecimal getFinalVol() {
		return finalVol;
	}
	public void setFinalVol(BigDecimal finalVol) {
		this.finalVol = finalVol;
	}
	public BigDecimal getCommissionValue() {
		return commissionValue;
	}
	public void setCommissionValue(BigDecimal commissionValue) {
		this.commissionValue = commissionValue;
	}
	public BigDecimal getTradeVol() {
		return tradeVol;
	}
	public void setTradeVol(BigDecimal tradeVol) {
		this.tradeVol = tradeVol;
	}
	public BigDecimal getEffectiveTradeVol() {
		return effectiveTradeVol;
	}
	public void setEffectiveTradeVol(BigDecimal effectiveTradeVol) {
		this.effectiveTradeVol = effectiveTradeVol;
	}
	public BigDecimal getTransactionAmt() {
		return transactionAmt;
	}
	public void setTransactionAmt(BigDecimal transactionAmt) {
		this.transactionAmt = transactionAmt;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getCommissionType() {
		return commissionType;
	}
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getTransactionCoin() {
		return transactionCoin;
	}
	public void setTransactionCoin(String transactionCoin) {
		this.transactionCoin = transactionCoin;
	}
	public String getCommissionCoin() {
		return commissionCoin;
	}
	public void setCommissionCoin(String commissionCoin) {
		this.commissionCoin = commissionCoin;
	}
	
	

}
