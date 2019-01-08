package com.portfolio.utilis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ExchangeConfig {
	
	Hashtable<String,List<String>> table = new Hashtable<String,List<String>>();
	
	List<String> binanceCoin = new ArrayList<String>();
	
	
	Map<String,List<String>> exchangeSetting = Collections.unmodifiableMap(table);
	
}
