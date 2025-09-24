package com.eventmgmt.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.dto.request.UpdateUserRequest;
import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.mapper.UserMapper;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.model.enums.RoleType;
import com.eventmgmt.auth_service.security.UserPrincipal;
import com.eventmgmt.auth_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	private final UserMapper userMapper;
	
	public UserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal) {
		Long userId = principal.getId();
		User currentUser = userService.getUser(userId);
		
		UserResponse response = userMapper.toResponse(currentUser);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserResponse> updateAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Valid UpdateUserRequest request) {
		Long userId = principal.getId();
		User updatedUser = userService.updateUser(userId, request);
		return ResponseEntity.ok(userMapper.toResponse(updatedUser));
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal) {
		Long userId = principal.getId();
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId, @AuthenticationPrincipal UserPrincipal principal) {
		User user = userService.getUser(userId);
		
		boolean includeRoles = principal.getId().equals(userId) || principal.hasRole(RoleType.ROLE_ADMIN.toString());
		
		UserResponse response = includeRoles ? userMapper.toResponse(user) : userMapper.toResponseWithoutRoles(user);
		return ResponseEntity.ok(response);
	}
}
