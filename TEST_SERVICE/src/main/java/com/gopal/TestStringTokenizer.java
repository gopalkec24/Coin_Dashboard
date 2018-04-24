package com.gopal;

import java.util.StringTokenizer;

public class TestStringTokenizer {

	public static void main(String[] args) {
		String names="tejeswar,Gopal,Edumban,prem";
		
		StringTokenizer tokenizer = new StringTokenizer(names,",");
		while(tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}
		
		String containsName = "filestore?:testing";
		
		containsName = containsName.indexOf("?:") > 0 ? containsName.substring(0,containsName.indexOf("?:")) : containsName;
		System.out.println(containsName);
	}

}
