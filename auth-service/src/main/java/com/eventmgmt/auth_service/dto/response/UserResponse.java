package com.eventmgmt.auth_service.dto.response;

import java.util.Set;

public class UserResponse {
	private long id;
	private String name;
	private String email;
	private String address;
	private Set<RoleResponse> roles;
	
	public UserResponse() {}

	public UserResponse(long id, String name, String email, String address, Set<RoleResponse> roles) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.address = address;
		this.roles = roles;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Set<RoleResponse> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoleResponse> roles) {
		this.roles = roles;
	}
}
