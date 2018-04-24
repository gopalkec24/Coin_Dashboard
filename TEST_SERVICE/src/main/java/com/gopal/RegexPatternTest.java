package com.gopal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternTest {

	public static void main(String[] args) {
		
		String inputStringPattern = "((?i)(USAA|Member)(\\s)*(Number)(\\s|:|-)*\\d{5,9})|\\d{5,9}";
		
		String inputText="This is USAA Number 4546154 , Hi ok this is  testing 4562 44151494 41494 5647";
		
		Pattern pattern = Pattern.compile(inputStringPattern);
		
		Matcher matcher = pattern.matcher(inputText);
		
		 while(matcher.find()) {
			 System.out.println(matcher.group());
		 }

	}

}
