package com.eventmgmt.auth_service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventmgmt.auth_service.dto.response.RoleResponse;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
	private RoleService roleService;
	
	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}
	
	@GetMapping
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		List<Role> roles = roleService.getAllRoles();
		List<RoleResponse> responseList = new ArrayList<>();
		for (Role role : roles) {
			RoleResponse response = new RoleResponse(role.getId(), role.getName());
			responseList.add(response);
		}
		return ResponseEntity.ok(responseList);
	}
}
