package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.PermissionRequest;
import com.qhuyns.ecomweb.dto.response.PermissionResponse;
import com.qhuyns.ecomweb.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
