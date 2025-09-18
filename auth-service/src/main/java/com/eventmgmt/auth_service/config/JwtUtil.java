package com.eventmgmt.auth_service.config;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	@Value("${security.jwt.secret-key}")
	private String SECRET;
	
	@Value("${security.jwt.expiration-time}")
	private long EXPIRATION;
	
	// Generate token
	public String generateToken(String userId, UserDetails userDetails) {
		return Jwts.builder()
				.subject(userId)
				.claim("roles", userDetails.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	// Extract Claims
	public Claims extractClaims(String token) {
		return Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}
	
	// Extract username (subject)
	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}
	
	// Extract roles
	public List<String> extractRoles(String token) {
		return extractClaims(token).get("roles", List.class);
	}
	
	// Check if token is valid
	public boolean isTokenValid(String token, String expectedUserId) {
		return extractUsername(token).equals(String.valueOf(expectedUserId)) &&
				extractClaims(token).getExpiration().after(new Date());
	}
	
	private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
