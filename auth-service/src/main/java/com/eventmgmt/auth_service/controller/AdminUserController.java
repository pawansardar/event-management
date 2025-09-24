package com.eventmgmt.auth_service.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.eventmgmt.auth_service.dto.response.UserPageResponse;
import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.mapper.UserMapper;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class AdminUserController {
	private final UserService userService;
	private final UserMapper userMapper;
	
	public AdminUserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<UserPageResponse> getAllUsers(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="200") int size) {
		Page<User> userPage = userService.getAllUsers(page, size);
		
		List<User> users = userPage.getContent();
		List<UserResponse> userResponseList = new ArrayList<>();
		for (User user : users) {
			UserResponse userResponse = userMapper.toResponse(user);
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
		return ResponseEntity.ok(userMapper.toResponse(user));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}/role/{roleId}")
	public ResponseEntity<UserResponse> removeRole(@PathVariable Long userId, @PathVariable int roleId) {
		User user = userService.removeRole(userId, roleId);
		return ResponseEntity.ok(userMapper.toResponse(user));
	}
}
