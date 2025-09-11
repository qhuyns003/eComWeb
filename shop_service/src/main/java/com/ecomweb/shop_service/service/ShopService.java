package com.ecomweb.shop_service.service;


import com.ecomweb.shop_service.dto.event.UpgradeToSellerSnapshot;
import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.ShopResponse;
import com.ecomweb.shop_service.entity.Shop;
import com.ecomweb.shop_service.entity.ShopAddress;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import com.ecomweb.shop_service.feignClient.MainFeignClient;
import com.ecomweb.shop_service.mapper.ShopAddressMapper;
import com.ecomweb.shop_service.mapper.ShopMapper;
import com.ecomweb.shop_service.producer.UserProducer;
import com.ecomweb.shop_service.repository.ShopRepository;
import com.ecomweb.shop_service.util.AuthUtil;
import com.ecomweb.shop_service.util.ErrorResponseUtil;
import com.ecomweb.shop_service.util.RedisCacheHelper;
import com.ecomweb.shop_service.util.RedisKey;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// transaction annotation chi dam bao neu lan save thu 2 fail thi back lai lan save dau tien
// khi co them call api phai handler bang mesage broker
// muon transaction rollback khi catch thi phai throw lai loi
// try catch bbat loi truoc globalExceptinoHanlder

// neu catch loi thi no se k in stack trace -> tu in
// neu loi bi bat bang handler thi cung phai log trace ra neu muon in
public class ShopService {

    ShopRepository shopRepository;
    ShopMapper shopMapper;
    ShopAddressMapper shopAddressMapper;
    WebClient webClient;
    UserProducer userProducer;
    RedisCacheHelper cacheHelper;
    MainFeignClient mainFeignClient;

    public ApiResponse<?> create(ShopCreateRequest shopCreateRequest) throws Exception {
        // chan neu da co shop
        String token = AuthUtil.getToken();
        String userId = "";
        try {
            userId = mainFeignClient.upgradeToSeller(UpgradeSellerRequest.builder()
                            .shopName(shopCreateRequest.getName())
                            .build()).getResult().toString();
        } catch (FeignException ex) {
            // try chi co feign nen bat feignex la du
            // bat exception neu con nhung code logic khac
           return ErrorResponseUtil.getResponseBody(ex);
        }
        Shop shop = shopMapper.toShop(shopCreateRequest);
        ShopAddress shopAddress = shopAddressMapper.toShopAddress(shopCreateRequest);
        shop.setShopAddress(shopAddress);
        shop.setUserId(userId);
        try{
            shopRepository.save(shop);
        }
        catch(Exception ex){
            UpgradeToSellerSnapshot data = cacheHelper.getFromCache(RedisKey.ROLLBACK_TO_SELLER.getKey()+userId,  UpgradeToSellerSnapshot.class);
            userProducer.rollbackUser(data);
            throw ex;
        }
        return ApiResponse.builder()
                        .httpStatus(HttpStatus.OK)
                        .result("create successfully")
                        .build();


    }

    public ApiResponse getInfo() {
        String token = AuthUtil.getToken();
        String userId = "";
        try {
            userId = mainFeignClient
                    .getUserId(SecurityContextHolder.getContext().getAuthentication().getName())
                    .getResult().toString();
        } catch (FeignException ex) {
            return ErrorResponseUtil.getResponseBody(ex);
        }
        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(()-> new AppException(ErrorCode.SHOP_NOT_EXISTS));
        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
        return ApiResponse.builder()
                .result(shopResponse)
                .build();
    }

    public ApiResponse update(ShopUpdateRequest shopUpdateRequest) {
        String token = AuthUtil.getToken();
        String userId = "";
        try {
            userId = mainFeignClient
                    .getUserId(SecurityContextHolder.getContext().getAuthentication().getName())
                    .getResult().toString();

        } catch (FeignException ex) {
            return ErrorResponseUtil.getResponseBody(ex);
        }
        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(()-> new AppException(ErrorCode.SHOP_NOT_EXISTS));

        shopMapper.toShop(shop,shopUpdateRequest);
        ShopAddress shopAddress = shop.getShopAddress();
        shopAddressMapper.toShopAddress(shopAddress,shopUpdateRequest);
        shopRepository.save(shop);
        return ApiResponse.builder()
                .result("update successful")
                .build();
    }


    public ApiResponse getInfoById(String id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTS));
        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
        return ApiResponse.builder()
                .result(shopResponse)
                .build();
    }

    public String getUserIdByShopId(String shopId){
        Shop shop = shopRepository.findById(shopId).orElseThrow(
                ()-> new AppException(ErrorCode.SHOP_NOT_EXISTS)
        );
        return shop.getUserId();
    }




}