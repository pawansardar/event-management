package com.eventmgmt.auth_service.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eventmgmt.auth_service.dto.request.UpdateUserRequest;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.model.enums.RoleType;
import com.eventmgmt.auth_service.repository.RoleRepository;
import com.eventmgmt.auth_service.repository.UserRepository;

@Service
public class UserService {
	private UserRepository userRepo;
	private RoleRepository roleRepo;
	
	public UserService(UserRepository userRepo, RoleRepository roleRepo) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}
	
	public User getUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow();
		return user;
	}
	
	public User updateUser(Long userId, UpdateUserRequest request) {
		String name = request.getName();
		String email = request.getEmail();
		String address = request.getAddress();
		
		if (name == null || email == null || name.length() < 1 || email.length() <1) {
			throw new IllegalArgumentException("Required fields cannot be empty.");
		}
		
		User user = userRepo.findById(userId).orElseThrow();
		user.setName(name);
		user.setEmail(email);
		user.setAddress(address);
		userRepo.save(user);
		return user;
	}
	
	public void deleteUser(Long userId) {
		userRepo.findById(userId).orElseThrow();
		userRepo.deleteById(userId);
	}
	
	public Page<User> getAllUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return userRepo.findAll(pageable);
	}
	
	public User assignRole(Long userId, int roleId) {
		User user = userRepo.findById(userId).orElseThrow();
		Role role = roleRepo.findById(roleId).orElseThrow();
		
		Set<Role> roles = user.getRoles();
		if (roles.contains(role)) {
			throw new IllegalArgumentException("Role already assigned for the user!");
		}
		
		roles.add(role);
		return userRepo.save(user);
	}
	
	public User removeRole(Long userId, int roleId) {
		User user = userRepo.findById(userId).orElseThrow();
		Role role = roleRepo.findById(roleId).orElseThrow();
		
		Set<Role> roles = user.getRoles();
		if (!roles.contains(role) || roles.size() < 2 || role.getName().equals(RoleType.ROLE_ATTENDEE.toString())) {
			throw new IllegalArgumentException("This role cannot be removed!");
		}
		
		roles.remove(role);
		return userRepo.save(user);
	}
}
