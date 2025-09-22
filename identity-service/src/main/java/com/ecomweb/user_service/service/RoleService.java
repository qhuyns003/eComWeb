//package com.ecomweb.user_service.service;
//
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class RoleService {
//    RoleRepository roleRepository;
//    PermissionRepository permissionRepository;
//    RoleMapper roleMapper;
//
//    public RoleResponse create(RoleRequest request) {
//        var role = roleMapper.toRole(request);
//
//        var permissions = permissionRepository.findAllById(request.getPermissions());
//        role.setPermissions(new ArrayList<>(permissions));
//
//        role = roleRepository.save(role);
//        return roleMapper.toRoleResponse(role);
//    }
//
//    public List<RoleResponse> getAll() {
//        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
//    }
//
//    public void delete(String role) {
//        roleRepository.deleteById(role);
//    }
//}
