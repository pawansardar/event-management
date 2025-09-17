package com.eventmgmt.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.config.JwtUtil;
import com.eventmgmt.auth_service.dto.request.AuthRequest;
import com.eventmgmt.auth_service.dto.request.RegisterUserRequest;
import com.eventmgmt.auth_service.dto.response.AuthResponse;
import com.eventmgmt.auth_service.dto.response.RegisterUserResponse;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private JwtUtil jwtUtil;
	private UserDetailsService userDetailsService;
	private AuthService authService;
	
	public AuthController(JwtUtil jwtUtil, UserDetailsService userDetailsService, AuthService authService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
		// Authenticate using username and password
		User user = authService.authenticate(request.getEmail(), request.getPassword());
		
		// Load user details
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		
		// Generate JWT token with user-id as subject
		String token = jwtUtil.generateToken(Long.toString(user.getId()), userDetails);
		
		return ResponseEntity.ok(new AuthResponse(token));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
		User registeredUser = authService.signup(request.getName(), request.getEmail(), request.getPassword(), request.getAddress());
		RegisterUserResponse response = new RegisterUserResponse(registeredUser.getId(), registeredUser.getName(), registeredUser.getEmail(), registeredUser.getAddress());
		return ResponseEntity.ok(response);
	}
}
