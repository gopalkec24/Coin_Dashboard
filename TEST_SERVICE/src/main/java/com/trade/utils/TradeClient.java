package com.trade.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.commons.codec.binary.Hex;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class TradeClient {
	
	
	public static WebTarget getClient(String endpoint,boolean proxy) {
		
		System.out.println(ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY);
	
		ClientBuilder cb = ClientBuilder.newBuilder();		
		Client client = cb.build();		
		WebTarget target = client.target(endpoint);
		
		return target;
		
	}

	public static WebTarget getAdvancedClient(String endpoint,boolean proxy) {
		
		Client client = null;
		if(proxy) {
		client = new ResteasyClientBuilder().defaultProxy("10.121.11.32", 8080).build();
		}
		else {
		client= ClientBuilder.newBuilder().build();
		}
		WebTarget target = client.target(endpoint);
		return target;
		
	}

	public static Mac getCryptoMac(String key, String alogrithm) {
		Mac sha256_HMAC =null;
		 try {
				 sha256_HMAC = Mac.getInstance(alogrithm);
				 SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), alogrithm);
				 sha256_HMAC.init(secret_key);
				 
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return sha256_HMAC;
		
	}
	
	public static String encode(String key,String data,String algortihm) {
		String encode= null;
		 	try {
				encode = getEncodeData(getCryptoMac(key, algortihm), data);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		 
		return encode;
		 
	 }
	
	public static String getEncodeData(Mac mac,String data) {
		String encode= null;
		try {
			encode = Hex.encodeHexString(mac.doFinal(data.getBytes("UTF-8")));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
	return encode;
		
	}
	public static boolean isNullorEmpty(String value) {
		boolean valid = false;
		//symbol is not mandatory one
		if(value == null || value.equalsIgnoreCase("")) 
		{
			valid = true;
		}	
		return valid;
	}
}
