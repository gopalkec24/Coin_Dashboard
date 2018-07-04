package com.gopal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionVO {

	public String name;
	public double volume;
	public double price;
	public int transactionType;
	public double commission;
	public Date date;
	public String transactionCurrency;
	//To save the coin live rate at time of transaction
	//Rate should be USD
	public double transactionCurrencyLiveRate;
	//Rate can be USD,INR
	// Rate can be Cryptocurrency Value, but need its Dollar value here
	public String transactionLiveRateCurrType;
	// Rate can be transaction date and time
	public double targetINRValueForUSD;
	
	public double transactionCoinLiveRate;
	
	
	
	
	
	public double getTransactionCurrencyLiveRate() {
		return transactionCurrencyLiveRate;
	}

	public void setTransactionCurrencyLiveRate(double transactionCurrencyLiveRate) {
		this.transactionCurrencyLiveRate = transactionCurrencyLiveRate;
	}

	public double getTransactionCoinLiveRate() {
		return transactionCoinLiveRate;
	}

	public void setTransactionCoinLiveRate(double transactionCoinLiveRate) {
		this.transactionCoinLiveRate = transactionCoinLiveRate;
	}

	public double getTargetINRValueForUSD() {
		return targetINRValueForUSD;
	}

	public void setTargetINRValueForUSD(double targetINRValueForUSD) {
		this.targetINRValueForUSD = targetINRValueForUSD;
	}

	public double getTransactionLiveRate() {
		return transactionCurrencyLiveRate;
	}

	public void setTransactionLiveRate(double transactionLiveRate) {
		this.transactionCurrencyLiveRate = transactionLiveRate;
	}

	public String getTransactionLiveRateCurrType() {
		return transactionLiveRateCurrType;
	}

	public void setTransactionLiveRateCurrType(String transactionLiveRateCurrType) {
		this.transactionLiveRateCurrType = transactionLiveRateCurrType;
	}

	

	public void setDate(String dateValue){
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			this.date = df.parse(dateValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTransactionCurrency() {
		return transactionCurrency;
	}
	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	
	
	
}
