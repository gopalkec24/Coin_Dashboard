package com.gopal;



import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

public class HttpClient {
  
  private static String url1 = "http://www.apache.org/";

  public static void main(String[] args) {
	  
  }

public static String getClient(String url) throws IOException, ClientProtocolException {
	HttpHost proxy = new HttpHost("10.121.11.33", 8080);
	
	CredentialsProvider credsProvider = new BasicCredentialsProvider();
	credsProvider.setCredentials(
	    new AuthScope("10.121.11.33", 8080), 
	    new UsernamePasswordCredentials("hcltech\natarajan_g", "@54"));
	
	
	CloseableHttpClient httpclient =  HttpClientBuilder.create().build();
	
	
	HttpClientContext context = HttpClientContext.create();
	context.setCredentialsProvider(credsProvider);
	//context.setAuthSchemeRegistry(authRegistry);
	//context.setAuthCache(authCache);
	 String responseBody = null;
      try {
          HttpGet httpget = new HttpGet(url1);

          System.out.println("Executing request " + httpget.getRequestLine());

          // Create a custom response handler
          ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

              public String handleResponse(
                      final HttpResponse response) throws ClientProtocolException, IOException {
                  int status = response.getStatusLine().getStatusCode();
                  if (status >= 200 && status < 300) {
                      HttpEntity entity = response.getEntity();
                      return entity != null ? EntityUtils.toString(entity) : null;
                  } else {
                      throw new ClientProtocolException("Unexpected response status: " + status);
                  }
              }

          };
          CloseableHttpResponse response = httpclient.execute(httpget, context);
          responseBody= response.getStatusLine().getReasonPhrase();
          System.out.println("----------------------------------------");
          System.out.println(responseBody);
      } finally {
          httpclient.close();
      }
	return url;
}
  
}