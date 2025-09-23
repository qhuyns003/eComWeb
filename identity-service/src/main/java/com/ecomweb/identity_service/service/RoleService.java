package com.ecomweb.identity_service.service;


import com.ecomweb.identity_service.dto.request.RoleRequest;
import com.ecomweb.identity_service.dto.response.RoleResponse;
import com.ecomweb.identity_service.mapper.RoleMapper;
import com.ecomweb.identity_service.mapper.UserMapper;
import com.ecomweb.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

// Transactional (mac dinh required) neu dat o ca 2 service thi neu A call B, B fail thi rollback B va A
// neu dat Transactional (require_new mode) o B thi chi rollback B, A van tiep tuc -> nguy hiem
// best practice chi dat annotation o method change db
public class RoleService {

    RoleMapper roleMapper ;
    RoleRepository roleRepository;

    @Transactional
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
