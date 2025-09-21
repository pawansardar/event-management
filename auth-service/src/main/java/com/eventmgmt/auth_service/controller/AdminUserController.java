package com.eventmgmt.auth_service.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.dto.response.RoleResponse;
import com.eventmgmt.auth_service.dto.response.UserPageResponse;
import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class AdminUserController {
	private UserService userService;
	
	public AdminUserController(UserService userService) {
		this.userService = userService;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<UserPageResponse> getAllUsers(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="200") int size) {
		Page<User> userPage = userService.getAllUsers(page, size);
		
		List<User> users = userPage.getContent();
		List<UserResponse> userResponseList = new ArrayList<>();
		for (User user : users) {
			Set<RoleResponse> roleResponseList = new HashSet<>();
			for (Role role : user.getRoles()) {
				RoleResponse roleResponse = new RoleResponse(role.getId(), role.getName());
				roleResponseList.add(roleResponse);
			}
			UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAddress(), roleResponseList);
			userResponseList.add(userResponse);
		}
		
		UserPageResponse response = new UserPageResponse(userResponseList, userPage.getNumber(), userPage.getTotalElements(), userPage.getTotalPages());
        return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{userId}/role/{roleId}")
	public ResponseEntity<UserResponse> assignRole(@PathVariable Long userId, @PathVariable int roleId) {
		User user = userService.assignRole(userId, roleId);
		
		Set<RoleResponse> roleResponseSet = new HashSet<>();
		for (Role role : user.getRoles()) {
			RoleResponse roleResponse = new RoleResponse(role.getId(), role.getName());
			roleResponseSet.add(roleResponse);
		}
		
		UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAddress(), roleResponseSet);
		return ResponseEntity.ok(response);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}/role/{roleId}")
	public ResponseEntity<UserResponse> removeRole(@PathVariable Long userId, @PathVariable int roleId) {
		User user = userService.removeRole(userId, roleId);
		
		Set<RoleResponse> roleResponseSet = new HashSet<>();
		for (Role role : user.getRoles()) {
			RoleResponse roleResponse = new RoleResponse(role.getId(), role.getName());
			roleResponseSet.add(roleResponse);
		}
		
		UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAddress(), roleResponseSet);
		return ResponseEntity.ok(response);
	}
}
