package com.eventmgmt.auth_service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
	@Mock
	private UserRepository userRepo;
	
	@InjectMocks
	private CustomUserDetailsService service;
	
	private User user;
	private Role role;
	
	@BeforeEach
	void setUp() {
		user = new User();
		role = new Role("ROLE_ATTENDEE", Set.of(user));
		user.setId(1);
		user.setName("Pawan");
		user.setEmail("example@email.com");
		user.setPassword("encodedPwd1");
		user.setAddress("Pune, India");
		user.setRoles(Set.of(role));
	}
	
	private void assertUserDetailsMatch(UserDetails userDetails, User expectedUser) {
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUsername()).isEqualTo(expectedUser.getEmail());
		assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
		assertTrue(userDetails.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ATTENDEE")));
	}
	
	@Test
	void loadUserByUsername_shouldReturnUserDetails() {
		String email = user.getEmail();
		when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
		
		UserDetails userDetails = service.loadUserByUsername(email);
		
		assertUserDetailsMatch(userDetails, user);
	}
	
	@Test
	void loadUserByUsername_whenUserNotFound_shouldThrowException() {
		String email = "another_example@email.com";
		when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class,
				() -> service.loadUserByUsername(email),
				"Expected loadUserByUsername() to throw, but it didn't");
	}
	
	@Test
	void loadUserById_shouldReturnUserDetails() {
		Long userId = user.getId();
		when(userRepo.findById(userId)).thenReturn(Optional.of(user));
		
		UserDetails userDetails = service.loadUserById(userId);
		
		assertUserDetailsMatch(userDetails, user);
	}
	
	@Test
	void loadUserById_whenUserNotFound_shouldThrowException() {
		Long userId = 2L;
		when(userRepo.findById(userId)).thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class,
				() -> service.loadUserById(userId),
				"Expected loadUserById() to throw, but it didn't");
	}
}
