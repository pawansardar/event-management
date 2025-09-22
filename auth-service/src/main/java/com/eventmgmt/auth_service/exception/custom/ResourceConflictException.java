package com.eventmgmt.auth_service.exception.custom;

public class ResourceConflictException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ResourceConflictException(String message) {
		super(message);
	}
}
