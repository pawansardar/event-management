package com.eventmgmt.auth_service.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 1L;
	private final Long id;
    
    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
    
    public Long getId() {
    	return id;
    }
    
    public boolean hasRole(String roleName) {
    	return getAuthorities().stream()
    			.anyMatch(auth -> auth.getAuthority().equals(roleName));
    }
}
