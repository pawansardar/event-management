package com.eventmgmt.auth_service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public class JwtUtilTest {
	private JwtUtil jwtUtil;
	private String userId;
	private UserDetails userDetails;
	
	@BeforeEach
	void setUp() {
		userId = "1";
		jwtUtil = new JwtUtil("dGVzdC1zZWNyZXQta2V5LXNob3VsZC1iZS1sb25nLXRvLW1lZXQ=", 3600000);   // dummy values for test
		userDetails = new User(userId, "encodedPwd1", List.of(new SimpleGrantedAuthority("ROLE_ATTENDEE")));
	}
	
	@Test
	void generateToken_shouldContainCorrectClaims() {
		String token = jwtUtil.generateToken(userId, userDetails);
		assertThat(token).isNotNull();
		
		Claims claims = jwtUtil.extractClaims(token);
		assertThat(claims.getSubject()).isEqualTo(userId);
		assertThat(claims.get("roles", List.class)).contains("ROLE_ATTENDEE");
		assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
	}
	
	@Test
	void extractClaims_withInvalidToken_shouldThrowException() {
		String token = "invalid-token-for-testing";
		
		assertThrows(JwtException.class,
				() -> jwtUtil.extractClaims(token),
				"Expected extractClaims() to throw exception for malformed token, but it didn't");
	}
	
	@Test
	void isTokenValid_withValidToken_shouldReturnTrue() {
		String token = jwtUtil.generateToken(userId, userDetails);
		boolean valid = jwtUtil.isTokenValid(token, userId);
		
		assertThat(valid).isTrue();
	}
	
	@Test
	void isTokenValid_withInvalidUserId_shouldReturnFalse() {
		String token = jwtUtil.generateToken(userId, userDetails);
		boolean valid = jwtUtil.isTokenValid(token, "2");
		
		assertThat(valid).isFalse();
	}
	
	@Test
	void isTokenValid_withExpiredToken_shouldThrowException() {
		JwtUtil shortLivedJwtUtil = new JwtUtil("dGVzdC1zZWNyZXQta2V5LXNob3VsZC1iZS1sb25nLXRvLW1lZXQ=", 1);
		
		String token = shortLivedJwtUtil.generateToken(userId, userDetails);
		try {
			Thread.sleep(5);
		}
		catch(InterruptedException ignoredException) {}
		
		assertThrows(JwtException.class,
				() -> shortLivedJwtUtil.isTokenValid(token, userId),
				"Expected extractClaims() to throw exception for malformed token, but it didn't");
	}
}
