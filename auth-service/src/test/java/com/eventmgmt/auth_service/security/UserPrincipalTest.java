package com.eventmgmt.auth_service.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserPrincipalTest {
	private UserPrincipal userPrincipal;
	
	@BeforeEach
	void setUp( ) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		String roleName = "ROLE_ATTENDEE";
		authorities.add(new SimpleGrantedAuthority(roleName));
		userPrincipal = new UserPrincipal(1L, "example@email.com", "encodedPwd1", authorities);
	}
	
	@Test
	void hasRole_withValidRole_shouldReturnTrue() {
		String role = "ROLE_ATTENDEE";
		boolean hasRole = userPrincipal.hasRole(role);
		
		assertThat(hasRole).isTrue();
	}
	
	@Test
	void hasRole_withInvalidRole_shouldReturnFalse() {
		String role = "ROLE_ADMIN";
		boolean hasRole = userPrincipal.hasRole(role);
		
		assertThat(hasRole).isFalse();
	}
	
	@Test
	void hasRole_withNullRole_shouldReturnFalse() {
		boolean hasRole = userPrincipal.hasRole(null);
		
		assertThat(hasRole).isFalse();
	}
	
	@Test
	void getId_shouldReturnCorrectId() {
		assertThat(userPrincipal.getId()).isEqualTo(1L);
	}
}
