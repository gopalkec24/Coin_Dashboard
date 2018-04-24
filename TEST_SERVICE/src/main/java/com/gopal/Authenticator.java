package com.gopal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;


public class Authenticator implements ClientRequestFilter {

	private final String user;
	private final String password;
	
	public Authenticator(String user,String password) {
		this.user = user;
		this.password = password;
	}
	
	public void filter(ClientRequestContext requestContext) throws IOException {
		
		MultivaluedMap<String, Object> headers = requestContext.getHeaders();		
		final String basicAuthentication = getBasicAuthentication();		
		headers.add("Authorization", basicAuthentication);
		
	}

	private String getBasicAuthentication() throws UnsupportedEncodingException {
		String token = this.user+ ":"+this.password;
		return "Basic "+java.util.Base64.getEncoder().encodeToString(token.getBytes("UTF-8"));
		
	}

}
