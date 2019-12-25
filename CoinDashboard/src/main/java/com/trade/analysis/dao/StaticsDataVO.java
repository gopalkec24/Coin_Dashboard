package com.trade.analysis.dao;

import java.util.List;

import com.trade.dao.coinmarketcap.CMCData;

public class StaticsDataVO {

	List<CMCData> topGain ;
	List<CMCData> topLoss ;
	
	public List<CMCData> getTopGain() {
		return topGain;
	}
	public void setTopGain(List<CMCData> topGain) {
		this.topGain = topGain;
	}
	public List<CMCData> getTopLoss() {
		return topLoss;
	}
	public void setTopLoss(List<CMCData> topLoss) {
		this.topLoss = topLoss;
	}
	
	
}
