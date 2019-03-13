package com.trade.dao.binance;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class BinanceSuccessDAO {

	public static void main(String[] args) {
		
		try {
			String jsonString=FileUtils.readFileToString(new File("C:/Documents/binance_success_error.txt"));
			System.out.println(jsonString);
			BinanceSuccess success = (BinanceSuccess) getDAOObject(jsonString, BinanceSuccess.class);
			System.out.println(success.getOrderId());
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
}
