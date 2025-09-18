package com.eventmgmt.auth_service.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {
	private final Long id;
    
    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }
    
    public Long getId() {
    	return id;
    }
}
