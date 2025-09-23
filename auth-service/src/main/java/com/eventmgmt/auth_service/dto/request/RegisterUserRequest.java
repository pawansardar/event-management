package com.eventmgmt.auth_service.dto.request;

import com.eventmgmt.auth_service.validation.annotation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterUserRequest {
	
	@NotBlank(message = "Name is mandatory")
	private String name;
	
	@Email(message = "Email should be valid")
	private String email;
	
	@StrongPassword
	private String password;
	
	private String address;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
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
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
}
