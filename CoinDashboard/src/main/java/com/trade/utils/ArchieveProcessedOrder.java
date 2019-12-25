package com.trade.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.trade.constants.TraderConstants;
import com.trade.dao.AutoTradeVO;
import com.trade.dao.TradeDataVO;

public class ArchieveProcessedOrder {

	public static void main(String[] args) throws JsonSyntaxException, IOException {
		AutoTradeVO actualData=readAutoTradeDataFromJSONNew("C:/Documents/autotradeData4.json");
		AutoTradeVO achieveData = readAutoTradeDataFromJSONNew("C:/Documents/archiveData_json4.json");
		List<TradeDataVO> actualList = actualData.getTradeData();
		CopyOnWriteArrayList<TradeDataVO> modifyList= new CopyOnWriteArrayList<TradeDataVO>(actualList);
		ArrayList<TradeDataVO> archieveList = new ArrayList<TradeDataVO>();
		for(TradeDataVO dataVO : modifyList) {
			if(dataVO.getTriggerEvent() == TraderConstants.COMPLETED || dataVO.getTriggerEvent() == TraderConstants.NEWTRADE_CREATED_FOR_DELETE) {
						archieveList.add(dataVO);
						modifyList.remove(dataVO);
			}
		}
		
		actualData.setTradeData(modifyList);
		actualData.setLastUpdatedTime(System.currentTimeMillis());
		
		achieveData.getTradeData().addAll(archieveList);
		
		
		achieveData.setLastUpdatedTime(System.currentTimeMillis());
		writeAutoTradeDataToJSON("C:/Documents/archiveData_json5.json",achieveData);
		writeAutoTradeDataToJSON("C:/Documents/autotradeData5.json",actualData);
	}
	
	private static AutoTradeVO readAutoTradeDataFromJSONNew(String jsonFilePath) throws JsonSyntaxException, IOException {
		Gson gson = new Gson();
		 Type listType = new TypeToken<AutoTradeVO>(){}.getType();
		 AutoTradeVO configValues= gson.fromJson(FileUtils.readFileToString(new File(jsonFilePath)),listType);
		return configValues;
	}
	public static void writeAutoTradeDataToJSON(String jsonFilePath, AutoTradeVO tradeDataVO) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			FileUtils.write(new File(jsonFilePath),mapper.defaultPrettyPrintingWriter().writeValueAsString(tradeDataVO));
		} catch (JsonGenerationException e) {
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
}
