package com.ecomweb.identity_service.service;


import com.ecomweb.identity_service.constant.PredefinedRole;
import com.ecomweb.identity_service.dto.event.EmailVerificationRequest;
import com.ecomweb.identity_service.dto.event.UserCreated;
import com.ecomweb.identity_service.dto.event.UserSnapshot;
import com.ecomweb.identity_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.identity_service.dto.request.UserCreationRequest;
import com.ecomweb.identity_service.dto.request.UserUpdateRequest;
import com.ecomweb.identity_service.dto.response.UserCouponResponse;
import com.ecomweb.identity_service.dto.response.UserResponse;
import com.ecomweb.identity_service.entity.*;
import com.ecomweb.identity_service.exception.AppException;
import com.ecomweb.identity_service.exception.ErrorCode;
import com.ecomweb.identity_service.mapper.UserMapper;
import com.ecomweb.identity_service.producer.UserProducer;
import com.ecomweb.identity_service.repository.*;
import com.ecomweb.identity_service.util.RedisCacheHelper;
import com.ecomweb.identity_service.util.RedisKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserCouponService {

    UserRepository userRepository;
    UserCouponRepository userCouponRepository;
    public List<UserCouponResponse> getByUserName(String username) {
        String userId = userRepository.findByUsernameAndActive(username, true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED))
                .getId();
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        return userCoupons.stream().map(uc -> UserCouponResponse.builder()
                .couponId(uc.getCouponId())
                .userId(userId)
                .build())
                .collect(Collectors.toList());
    }



}

