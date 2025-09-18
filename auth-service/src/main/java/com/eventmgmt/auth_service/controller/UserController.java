package com.eventmgmt.auth_service.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.dto.request.UpdateUserRequest;
import com.eventmgmt.auth_service.dto.response.RegisterUserResponse;
import com.eventmgmt.auth_service.dto.response.RoleResponse;
import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.service.UserPrincipal;
import com.eventmgmt.auth_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal) {
		Long userId = principal.getId();
		
		User currentUser = userService.getUser(userId);
		
		Set<RoleResponse> roleResponseList = new HashSet<>();
		for (Role role : currentUser.getRoles()) {
			RoleResponse roleResponse = new RoleResponse(role.getId(), role.getName());
			roleResponseList.add(roleResponse);
		}
		
		UserResponse response = new UserResponse(currentUser.getId(), currentUser.getName(), currentUser.getEmail(), currentUser.getAddress(), roleResponseList);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/me")
	public ResponseEntity<RegisterUserResponse> updateAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal, @RequestBody UpdateUserRequest request) {
		Long userId = principal.getId();
		
		User updatedUser = userService.updateUser(userId, request);
		RegisterUserResponse response = new RegisterUserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getAddress());
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAuthenticatedUser(@AuthenticationPrincipal UserPrincipal principal) {
		Long userId = principal.getId();
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
}
