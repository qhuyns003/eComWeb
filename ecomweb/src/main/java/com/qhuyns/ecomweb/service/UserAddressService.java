package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.mapper.UserAddressMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.repository.UserAddressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAddressService {

    UserAddressMapper userAddressMapper;
    UserAddressRepository userAddressRepository;

    public List<UserAddressResponse> getAll() {
        return userAddressRepository
                .findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .stream().map(ua -> userAddressMapper.toUserAddressResponse(ua))
                .collect(Collectors.toList());

    }
}