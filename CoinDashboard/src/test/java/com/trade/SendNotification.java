package com.trade;

import java.util.ArrayList;
import java.util.List;

import com.trade.notification.NotificationSender;

public class SendNotification {
	
 
	
	public static void main(String[] args) {
		NotificationSender not = new NotificationSender();
		List<String> recipients = new ArrayList<String>();
		recipients.add("919952818233");
		not.sendNotificationMessage("testing here ",recipients);
}
}
