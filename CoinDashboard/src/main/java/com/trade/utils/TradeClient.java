package com.trade.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

@SuppressWarnings("deprecation")
public class TradeClient {
	
	
	private static  String PROXY_HOSTNAME = "10.121.11.32";
	private static  int PROXY_PORT = 8080;
	private static  String PROXY_AUTH_USERNAME = "hcltech\natarajan_g";
	private static String  PROXY_AUTH_PASSWORD = "4";
	public static WebTarget getClient(String endpoint,boolean proxy) {
		
		System.out.println(ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY);
	
		ClientBuilder cb = ClientBuilder.newBuilder();		
		Client client = cb.build();		
		WebTarget target = client.target(endpoint);
		
		return target;
		
	}

	public static WebTarget getAdvancedClient(String endpoint,boolean proxy) 
	{
		
		Client client = null;
		if(proxy) {
		
			System.setProperty("http.proxyHost", PROXY_HOSTNAME);
			System.setProperty("http.proxyPort", PROXY_PORT+"");
	
			// 1. Create AuthCache instance
			HttpHost hostProxy = new HttpHost(PROXY_HOSTNAME, 8080);
			
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
			    new AuthScope(PROXY_HOSTNAME, 8080), 
			    new UsernamePasswordCredentials(PROXY_AUTH_USERNAME, PROXY_AUTH_PASSWORD));
			
			AuthCache authCache = new BasicAuthCache();
			AuthScheme basicAuth = new BasicScheme();
			authCache.put(hostProxy, basicAuth);
			
			BasicHttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			 httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, hostProxy);
			ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient, localContext);
			 client = new ResteasyClientBuilder().httpEngine(engine).build();
			
		}
		else
		{
		client= ClientBuilder.newBuilder().build();
		}
		WebTarget target = client.target(endpoint);
		
		return target;
		
	}

	public static Mac getCryptoMac(String key, String alogrithm) 
	{
		Mac sha256_HMAC =null;
		 try {
				 sha256_HMAC = Mac.getInstance(alogrithm);
				 SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), alogrithm);
				 sha256_HMAC.init(secret_key);
				 
			} catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				
				e.printStackTrace();
			}
		 return sha256_HMAC;
		
	}
	
	public static String encode(String key,String data,String algortihm) {
		String encode= null;
		 	try {
				encode = getEncodeData(getCryptoMac(key, algortihm), data);
			} catch (IllegalStateException e) {
				
				e.printStackTrace();
			}		 
		return encode;
		 
	 }
	
	public static String getEncodeData(Mac mac,String data) {
		String encode= null;
		try {
			encode = Hex.encodeHexString(mac.doFinal(data.getBytes("UTF-8")));
		} catch (IllegalStateException e) {
		
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
		}		 
	return encode;
		
	}
	public static boolean isNullorEmpty(String value) {
		boolean valid = false;
		if(value == null || value.equalsIgnoreCase("")) 
		{
			valid = true;
		}	
		return valid;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getDAOObject(String errorMsg,Class target) {
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
	
	public static long getTimeInMSFromString(String value,String format) {
		
		SimpleDateFormat df = new SimpleDateFormat(format);
		
		try {
			Date dt = df.parse(value);
			TradeLogger.LOGGER.info("Converted Date Format :" + dt);
			return dt.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return -1;
		
		
		
	}
}
