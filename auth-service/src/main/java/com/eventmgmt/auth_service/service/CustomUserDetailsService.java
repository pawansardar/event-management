package com.eventmgmt.auth_service.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.repository.UserRepository;

@Service
public class CustomUserDetailsService implements ExtendedUserDetailsService {
	private UserRepository userRepo;
	
	public CustomUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
		return buildUserDetails(user);
	}
	
	public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return buildUserDetails(user);
    }
	
	private UserDetails buildUserDetails(User user) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		for (Role role : user.getRoles()) {
			String roleName = role.getName();
			authorities.add(new SimpleGrantedAuthority(roleName));
		}
		
		return new UserPrincipal(
				user.getId(),
				user.getEmail(),
				user.getPassword(),
				authorities
				);
	}

}
