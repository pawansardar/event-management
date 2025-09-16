package com.eventmgmt.auth_service.dto.response;

public class AuthResponse {
	private String token;
	
	public AuthResponse() {}

	public AuthResponse(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
