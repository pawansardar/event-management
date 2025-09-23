package com.eventmgmt.auth_service.dto.request;

import com.eventmgmt.auth_service.validation.annotation.StrongPassword;

import jakarta.validation.constraints.Email;

public class AuthRequest {
	
	@Email(message = "Email should be valid")
	private String email;
	
	@StrongPassword
	private String password;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
