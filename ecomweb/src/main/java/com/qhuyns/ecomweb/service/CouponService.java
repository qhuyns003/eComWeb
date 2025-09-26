package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.CouponResponse;
import com.qhuyns.ecomweb.dto.response.UserCouponResponse;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.mapper.CouponMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.repository.CouponRepository;
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
public class CouponService {

    CouponRepository couponRepository;
    CouponMapper couponMapper;
    IdentityFeignClient identityFeignClient;
    public List<CouponResponse> getByShopId(String shopId) {

        String userId = identityFeignClient.getUserIdByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getResult();
        return couponRepository.findAvailableCouponsForShopAndUser(shopId, userId)
                .stream().map(couponMapper::toCouponResponse).collect(Collectors.toList());
    };
    public List<CouponResponse> getByUserId() {
        List<UserCouponResponse> userCouponResponses = identityFeignClient.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult();
        List<String> couponId = userCouponResponses.stream().map(UserCouponResponse::getCouponId).collect(Collectors.toList());
        String userId = identityFeignClient.getUserIdByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getResult();
        return couponRepository.findValidUnusedCouponsOfUser(userId,couponId)
                .stream().map(couponMapper::toCouponResponse).collect(Collectors.toList());
    };
}
