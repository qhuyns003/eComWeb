package com.ecomweb.identity_service.service;

import com.ecomweb.identity_service.dto.request.UserAddressRequest;
import com.ecomweb.identity_service.dto.response.UserAddressResponse;
import com.ecomweb.identity_service.entity.User;
import com.ecomweb.identity_service.entity.UserAddress;
import com.ecomweb.identity_service.exception.AppException;
import com.ecomweb.identity_service.exception.ErrorCode;
import com.ecomweb.identity_service.mapper.UserAddressMapper;
import com.ecomweb.identity_service.repository.UserAddressRepository;
import com.ecomweb.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserAddressService {
    UserAddressRepository  userAddressRepository;
    UserRepository userRepository;
    UserAddressMapper  userAddressMapper;
    public List<UserAddressResponse> getAll() {
        User user= userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        return  userAddressRepository.findAllByUserId(user.getId())
                .stream().map(userAddressMapper::toUserAddressResponse)
                .collect(Collectors.toList());

    }
    public void create(UserAddressRequest userAddressRequest) {
        UserAddress userAddress = userAddressMapper.toUserAddress(userAddressRequest);
        User user= userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(userAddressRequest.isDefaultAddress()){
            List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(user.getId());
            for(UserAddress userAddress1 : userAddressList){
                userAddress1.setDefaultAddress(false);
                userAddressRepository.save(userAddress1);
            }
        }
        userAddress.setUserId(user.getId());
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
            List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userAddress.getUserId());
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