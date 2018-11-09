package com.trade.utils;


import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TradeLogger {

	 private static final LogManager logManager = LogManager.getLogManager();
	 public static final Logger LOGGER =Logger.getLogger("TradeLogger");
	 static {
		 try {
			 InputStream ins = TradeLogger.class.getClassLoader().getResourceAsStream("logger.properties");
			 if(ins!= null) {
			 logManager.readConfiguration(ins);
			 }
			 else {
				 LOGGER.info("No Logger config properties file found");
				 Handler handlerObj = new ConsoleHandler();
				 handlerObj.setLevel(Level.ALL);
				 LOGGER.addHandler(handlerObj);
				 LOGGER.setLevel(Level.ALL);
				 LOGGER.setUseParentHandlers(false);
			 }
				 
		 }
		 catch(Exception e) {
			 LOGGER.log(Level.INFO,"Error in Loading the properties file",e);
		 }
	 }
	
}
