package com.eventmgmt.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.repository.RoleRepository;

@Service
public class RoleService {
	private RoleRepository roleRepo;
	
	public RoleService(RoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}
	
	public List<Role> getAllRoles() {
		List<Role> roles = roleRepo.findAll();
		return roles;
	}
}
