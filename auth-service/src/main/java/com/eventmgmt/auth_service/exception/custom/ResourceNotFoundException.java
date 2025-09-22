package com.eventmgmt.auth_service.exception.custom;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2L;
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
