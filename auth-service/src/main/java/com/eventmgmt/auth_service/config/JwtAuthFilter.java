package com.eventmgmt.auth_service.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eventmgmt.auth_service.service.ExtendedUserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ExtendedUserDetailsService userDetailsService;
	
	public JwtAuthFilter(JwtUtil jwtUtil, ExtendedUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String jwt = authHeader.substring(7);
		try {
			String userId = jwtUtil.extractUsername(jwt);
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			if (userId != null && authentication == null && jwtUtil.isTokenValid(jwt, userId)) {
				UserDetails userDetails = this.userDetailsService.loadUserById(Long.valueOf(userId));
				UsernamePasswordAuthenticationToken authToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
			
			filterChain.doFilter(request, response);
		}
		catch (JwtException ex) {
			SecurityContextHolder.clearContext();
			filterChain.doFilter(request, response);
		}
	}

}
