package com.eventmgmt.auth_service.mapper;

import java.util.Collections;

import org.mapstruct.Mapper;

import com.eventmgmt.auth_service.dto.response.UserResponse;
import com.eventmgmt.auth_service.model.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
	UserResponse toResponse(User user);
	
	default UserResponse toResponseWithoutRoles(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setAddress(user.getAddress());
		response.setRoles(Collections.emptySet());
		return response;
	}
}
