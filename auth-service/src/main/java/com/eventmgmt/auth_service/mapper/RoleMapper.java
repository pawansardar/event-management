package com.eventmgmt.auth_service.mapper;

import org.mapstruct.Mapper;

import com.eventmgmt.auth_service.dto.response.RoleResponse;
import com.eventmgmt.auth_service.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	RoleResponse toResponse(Role role);
}
