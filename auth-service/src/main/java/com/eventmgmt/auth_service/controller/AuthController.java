package com.eventmgmt.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.dto.request.AuthRequest;
import com.eventmgmt.auth_service.dto.request.RegisterUserRequest;
import com.eventmgmt.auth_service.dto.response.AuthResponse;
import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.mapper.UserMapper;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.security.JwtUtil;
import com.eventmgmt.auth_service.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	private final AuthService authService;
	private final UserMapper userMapper;
	
	public AuthController(JwtUtil jwtUtil, UserDetailsService userDetailsService, AuthService authService, UserMapper userMapper) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authService = authService;
		this.userMapper = userMapper;
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
		// Authenticate using username and password
		User user = authService.authenticate(request.getEmail(), request.getPassword());
		
		// Load user details
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		
		// Generate JWT token with user-id as subject
		String token = jwtUtil.generateToken(Long.toString(user.getId()), userDetails);
		
		return ResponseEntity.ok(new AuthResponse(token));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
		User registeredUser = authService.signup(request.getName(), request.getEmail(), request.getPassword(), request.getAddress());
		return ResponseEntity.ok(userMapper.toResponse(registeredUser));
	}
}
