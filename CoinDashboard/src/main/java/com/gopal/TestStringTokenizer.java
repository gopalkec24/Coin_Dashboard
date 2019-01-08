package com.gopal;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

public class TestStringTokenizer {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		/*String names="tejeswar,Gopal,Edumban,prem";
		
		StringTokenizer tokenizer = new StringTokenizer(names,",");
		while(tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}
		
		String containsName = "filestore?:testing";
		
		containsName = containsName.indexOf("?:") > 0 ? containsName.substring(0,containsName.indexOf("?:")) : containsName;
		System.out.println(containsName);*/
		
		
		List<String> employee = new ArrayList<String>();
		
		employee.add("Gopala");
		employee.add("krishnan");
		String json= new Gson().toJson(employee);
		List<Map<String,String>> map= new ArrayList<Map<String,String>>();
		Map<String,String> data= new HashMap<String,String>();
		
		for(int i=0; i<2; i++) {
			data.put("cosa", "gc");
			data.put("preset", "gc");
			data.put("select", "gc");
			data.put("from", "gc");
			data.put("where", "gc");
			map.add(data);
		}
		ObjectMapper mapper = new ObjectMapper();
		json=mapper.writeValueAsString(map);
		System.out.println(json);
	}
	
	
	

}
