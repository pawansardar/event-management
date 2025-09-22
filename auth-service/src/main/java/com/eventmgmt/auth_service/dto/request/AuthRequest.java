package com.eventmgmt.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AuthRequest {
	
	@Email(message = "Email should be valid")
	private String email;
	
	@Size(min = 8, max = 16, message = "Password must be between 8 to 16 characters")
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
