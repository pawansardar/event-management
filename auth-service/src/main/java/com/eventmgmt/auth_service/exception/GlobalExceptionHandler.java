package com.eventmgmt.auth_service.exception;

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

import com.eventmgmt.auth_service.dto.response.ErrorResponse;
import com.eventmgmt.auth_service.exception.custom.ResourceConflictException;
import com.eventmgmt.auth_service.exception.custom.ResourceNotFoundException;
import com.eventmgmt.auth_service.exception.custom.RoleOperationNotAllowedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ResourceConflictException.class)
	public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceConflictException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDbConflict(DataIntegrityViolationException ex) {
		String message = "Data conflict occurred";
		return buildErrorResponse(message, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(RoleOperationNotAllowedException.class)
	public ResponseEntity<ErrorResponse> handleRoleOperationNotAllowed(RoleOperationNotAllowedException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		
		String message = "Validation failed";
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, errors);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
	    return buildErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccountStatusException.class)
	public ResponseEntity<ErrorResponse> handleAccountStatus(AccountStatusException ex) {
	    return buildErrorResponse("Account is locked or disabled", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
	    return buildErrorResponse("You are not authorized to access this resource", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<ErrorResponse> handleInvalidSignature(SignatureException ex) {
	    return buildErrorResponse("Invalid JWT signature", HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
	    return buildErrorResponse("JWT token has expired", HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
		ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
		return new ResponseEntity<>(errorResponse, status);
	}
}
