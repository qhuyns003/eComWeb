//package com.qhuyns.ecomweb.service;
//
//
//import com.qhuyns.ecomweb.dto.request.PermissionRequest;
//import com.qhuyns.ecomweb.dto.response.PermissionResponse;
//import com.qhuyns.ecomweb.entity.Permission;
//import com.qhuyns.ecomweb.mapper.PermissionMapper;
//import com.qhuyns.ecomweb.repository.PermissionRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class PermissionService {
//    PermissionRepository permissionRepository;
//    PermissionMapper permissionMapper;
//
//    public PermissionResponse create(PermissionRequest request) {
//        Permission permission = permissionMapper.toPermission(request);
//        permission = permissionRepository.save(permission);
//        return permissionMapper.toPermissionResponse(permission);
//    }
//
//    public List<PermissionResponse> getAll() {
//        var permissions = permissionRepository.findAll();
//        return permissions.stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
//    }
//
//    public void delete(String permission) {
//        permissionRepository.deleteById(permission);
//    }
//}
