package com.eventmgmt.auth_service.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.model.enums.RoleType;
import com.eventmgmt.auth_service.repository.RoleRepository;
import com.eventmgmt.auth_service.repository.UserRepository;

@Service
public class AuthService {
	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authManager;
	
	public AuthService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder, AuthenticationManager authManager) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
		this.authManager = authManager;
	}
	
	public User authenticate(String email, String password) {
		authManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password)
				);
		
		return userRepo.findByEmail(email)
				.orElseThrow();
	}
	
	public User signup(String name, String email, String password, String address) {
		if (userRepo.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("email already exists!");
		}
		
		Optional<Role> defaultRole = roleRepo.findByName(RoleType.ROLE_ATTENDEE.toString());
		if (defaultRole.isEmpty()) {
			throw new RuntimeException("Default role not found!");
		}
		
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setAddress(address);
		user.getRoles().add(defaultRole.get());
		
		return userRepo.save(user);
	}
}
