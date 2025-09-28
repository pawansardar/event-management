package com.eventmgmt.auth_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eventmgmt.auth_service.exception.custom.ResourceConflictException;
import com.eventmgmt.auth_service.exception.custom.ResourceNotFoundException;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.repository.RoleRepository;
import com.eventmgmt.auth_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private UserRepository userRepo;
	
	@Mock
	private RoleRepository roleRepo;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private AuthenticationManager authManager;
	
	@InjectMocks
	private AuthService authService;
	
	private User user;
	private Role role;
	
	@BeforeEach
	void setup() {
		user = new User();
		role = new Role("ROLE_ATTENDEE", Set.of(user));
		user.setId(1);
		user.setName("Pawan");
		user.setEmail("example@email.com");
		user.setPassword("encodedPwd1");
		user.setAddress("Pune, India");
		user.setRoles(Set.of(role));
	}
	
	@Test
	void authenticate_shouldReturnUser() {
		String email = "example@email.com";
		String password = "encodedPwd1";
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
		when(authManager.authenticate(authRequest)).thenReturn(new UsernamePasswordAuthenticationToken(email, password));
		when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
		
		User authUser = authService.authenticate(email, password);
		
		assertThat(authUser).isNotNull();
		assertThat(authUser).isEqualTo(user);
		
		verify(authManager).authenticate(authRequest);
		verify(userRepo).findByEmail(email);
	}
	
	@Test
	void authenticate_shouldThrow_whenInvalidCredentials() {
		String email = "example@email.com";
		String wrongPassword = "wrongPwd";
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, wrongPassword);
		when(authManager.authenticate(authRequest)).thenThrow(new BadCredentialsException("Invalid credentials"));
		
		assertThatThrownBy(() -> authService.authenticate(email, wrongPassword))
		.isInstanceOf(BadCredentialsException.class)
		.hasMessageContaining("Invalid credentials");
		
		verify(authManager).authenticate(authRequest);
		verify(userRepo, never()).findByEmail(email);
	}
	
	@Test
	void signup_shouldReturnUser_whenNewUser() {
		when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(roleRepo.findByName("ROLE_ATTENDEE")).thenReturn(Optional.of(role));
		when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPwd1");
		when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		User registeredUser = authService.signup(user.getName(), user.getEmail(), user.getPassword(), user.getAddress());
		
		assertThat(registeredUser).isNotNull();
		assertThat(registeredUser.getEmail()).isEqualTo(user.getEmail());
		assertThat(registeredUser.getPassword()).isEqualTo("encodedPwd1");
	}
	
	@Test
	void signup_shouldThrow_whenUserConflicts() {
		when(userRepo.findByEmail(user.getEmail())).thenThrow(new ResourceConflictException("Email already exists"));
		
		assertThatThrownBy(() -> authService.signup(user.getName(), user.getEmail(), user.getPassword(), user.getAddress()))
		.isInstanceOf(ResourceConflictException.class)
		.hasMessageContaining("Email already exists");
		
		verify(userRepo, never()).save(user);
	}
	
	@Test
	void signup_shouldThrow_whenRoleNotFound() {
		when(roleRepo.findByName(role.getName())).thenThrow(new ResourceNotFoundException("Role not found"));
		
		assertThatThrownBy(() -> authService.signup(user.getName(), user.getEmail(), user.getPassword(), user.getAddress()))
		.isInstanceOf(ResourceNotFoundException.class)
		.hasMessageContaining("Role not found");
		
		verify(userRepo, never()).save(user);
	}
}
