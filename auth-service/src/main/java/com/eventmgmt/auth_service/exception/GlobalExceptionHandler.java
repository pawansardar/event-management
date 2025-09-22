package com.eventmgmt.auth_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eventmgmt.auth_service.exception.custom.ResourceConflictException;
import com.eventmgmt.auth_service.exception.custom.ResourceNotFoundException;
import com.eventmgmt.auth_service.exception.custom.RoleOperationNotAllowedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ResourceConflictException.class)
	public ResponseEntity<Map<String, Object>> handleResourceConflict(ResourceConflictException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDbConflict(DataIntegrityViolationException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(RoleOperationNotAllowedException.class)
	public ResponseEntity<Map<String, Object>> handleRoleOperationNotAllowed(RoleOperationNotAllowedException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		
		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		response.put("error", errors);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
	    return buildErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccountStatusException.class)
	public ResponseEntity<Map<String, Object>> handleAccountStatus(AccountStatusException ex) {
	    return buildErrorResponse("Account is locked or disabled", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
	    return buildErrorResponse("You are not authorized to access this resource", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidSignature(SignatureException ex) {
	    return buildErrorResponse("Invalid JWT signature", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Map<String, Object>> handleExpiredJwt(ExpiredJwtException ex) {
	    return buildErrorResponse("JWT token has expired", HttpStatus.UNAUTHORIZED);
	}

	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("timestamp", LocalDateTime.now());
		errorResponse.put("status", status.value());
		errorResponse.put("error", message);
		return new ResponseEntity<>(errorResponse, status);
	}
}
