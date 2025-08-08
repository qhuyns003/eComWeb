package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.entity.UserAddress;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.mapper.UserAddressMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.repository.UserAddressRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAddressService {

    UserAddressMapper userAddressMapper;
    UserAddressRepository userAddressRepository;
    UserRepository userRepository;

    public List<UserAddressResponse> getAll() {
        List<UserAddress> userAddressList = userAddressRepository
                .findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    List<UserAddressResponse>  userAddressResponses = userAddressRepository
            .findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
            .stream().map(ua -> userAddressMapper.toUserAddressResponse(ua))
            .collect(Collectors.toList());

        return userAddressRepository
                .findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .stream().map(ua -> userAddressMapper.toUserAddressResponse(ua))
                .collect(Collectors.toList());

    }
    public void create(UserAddressRequest userAddressRequest) {
        UserAddress userAddress = userAddressMapper.toUserAddress(userAddressRequest);
        if(userAddressRequest.isDefaultAddress()){
            List<UserAddress> userAddressList = userAddressRepository.findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            for(UserAddress userAddress1 : userAddressList){
                userAddress1.setDefaultAddress(false);
                userAddressRepository.save(userAddress1);
            }
        }
        userAddress.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED)));
        userAddressRepository.save(userAddress);
    }

    public void delete(String id) {
        UserAddress userAddress = userAddressRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)
        );
        if(userAddress.isDefaultAddress()){
            throw new AppException(ErrorCode.DO_NOT_DELETE_USER_ADDRESS);
        }
       userAddressRepository.deleteById(id);
    }

    public void update(String id, UserAddressRequest userAddressRequest) {
        UserAddress userAddress = userAddressRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)
        );
        userAddressMapper.toUserAddress(userAddress, userAddressRequest);
        if(userAddressRequest.isDefaultAddress()){
            List<UserAddress> userAddressList = userAddressRepository.findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            for(UserAddress userAddress1 : userAddressList){
                if(userAddress1.getId()==userAddress.getId()){
                    continue;
                }
                userAddress1.setDefaultAddress(false);
                userAddressRepository.save(userAddress1);
            }
        }

        userAddressRepository.save(userAddress);
    }


}