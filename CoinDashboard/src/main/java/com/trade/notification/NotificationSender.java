package com.trade.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.trade.utils.TradeClient;
import com.trade.utils.TradeLogger;

public class NotificationSender {

	private static final String ACCESS_TOKEN = "ba1c7738054ec208526aa78a41f02a6927268204";
	private static final String QUERY_PARM_ACCESS_TOKEN = "access_token";
	private static final String RESOURCE_POST_MESSAGE = "/1/messages";
	private static final String PARM_RECIPIENTS = "recipients";
	private static final String PARM_IDENTIFIER = "identifier";
	private static final String PARM_TEXT = "text";
	private static final String PARM_MOBILE_APP_ID = "mobileAppId";
	private static final int moblieAppId = 318;
	private static final String API_ENDPOINT = "https://api.catapush.com";
	public static WebTarget target = null;
	
	
	public static void sendNotificationMessage(String messageToSend,List<String> recipients) {
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(PARM_MOBILE_APP_ID, moblieAppId);
			jsonObject.put(PARM_TEXT, messageToSend);
			JSONArray receiot = new JSONArray();
			for (String recipient : recipients) {
				JSONObject receipt = new JSONObject();
				receipt.put(PARM_IDENTIFIER, recipient);
				receiot.put(receipt);
			}
			jsonObject.put(PARM_RECIPIENTS, receiot);
			TradeLogger.LOGGER.info(jsonObject.toString());
			Response response = getTarget().path(RESOURCE_POST_MESSAGE)
					.queryParam(QUERY_PARM_ACCESS_TOKEN, ACCESS_TOKEN).request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(jsonObject.toString()));
			if (response.getStatus() == 200) {
				TradeLogger.LOGGER.info(response.readEntity(String.class));
			} else {
				TradeLogger.LOGGER.info("Error in sending the message" + response.getStatus());
				TradeLogger.LOGGER.info(response.readEntity(String.class));
			} 
		} catch(Exception e) {
			TradeLogger.LOGGER.severe(e.getMessage());
		}
	}

	public static WebTarget getTarget() {
		if(target == null) {
			target = TradeClient.getAdvancedClient(API_ENDPOINT,true);
			TradeLogger.LOGGER.info("Initialized Newly ....");
		}
		return target;
	}
}
