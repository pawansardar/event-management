package com.eventmgmt.auth_service.dto.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class ErrorResponse {
	private int status;
	private String message;
	private LocalDateTime timestamp;
	private Map<String, String> fieldErrors;
	
	public ErrorResponse() {}

	public ErrorResponse(int status, String message) {
		super();
		this.status = status;
		this.message = message;
		this.fieldErrors = Collections.emptyMap();
		this.timestamp = LocalDateTime.now();
	}

	public ErrorResponse(int status, String message, Map<String, String> fieldErrors) {
		super();
		this.status = status;
		this.message = message;
		this.fieldErrors = fieldErrors;
		this.timestamp = LocalDateTime.now();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(Map<String, String> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
}
