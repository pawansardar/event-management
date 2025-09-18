package com.eventmgmt.auth_service.service;

import org.springframework.stereotype.Service;

import com.eventmgmt.auth_service.dto.request.UpdateUserRequest;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.repository.UserRepository;

@Service
public class UserService {
	private UserRepository userRepo;
	
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
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
}
