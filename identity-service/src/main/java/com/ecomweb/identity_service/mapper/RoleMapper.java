package com.ecomweb.identity_service.mapper;

import com.ecomweb.identity_service.dto.request.RoleRequest;
import com.ecomweb.identity_service.dto.response.RoleResponse;
import com.ecomweb.identity_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
