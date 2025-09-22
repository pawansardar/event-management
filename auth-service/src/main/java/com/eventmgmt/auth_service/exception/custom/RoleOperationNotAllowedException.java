package com.eventmgmt.auth_service.exception.custom;

public class RoleOperationNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 3L;
	
	public RoleOperationNotAllowedException(String message) {
		super(message);
	}
}
