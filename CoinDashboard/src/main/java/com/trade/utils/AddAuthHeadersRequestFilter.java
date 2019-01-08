package com.trade.utils;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class AddAuthHeadersRequestFilter implements ClientRequestFilter  {
	
	private final String username;
	private final String password;

	public AddAuthHeadersRequestFilter(String proxyAuthUsername, String proxyAuthPassword) {
		this.username = proxyAuthUsername;
		this.password = proxyAuthPassword;
	}

	public void filter(ClientRequestContext requestContext) throws IOException {
		String encoded = new String(Base64.getEncoder()
				.encode((new String(this.username + ":" + this.password).getBytes())));
	
		requestContext.getStringHeaders().add("Proxy-Authorization", "Basic "+encoded);
		
	}

}
