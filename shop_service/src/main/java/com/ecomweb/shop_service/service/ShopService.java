package com.ecomweb.shop_service.service;


import com.ecomweb.shop_service.dto.event.ShopCreationFailed;
import com.ecomweb.shop_service.dto.event.UserSnapshot;
import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.ShopResponse;
import com.ecomweb.shop_service.entity.Shop;
import com.ecomweb.shop_service.entity.ShopAddress;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import com.ecomweb.shop_service.feignClient.IdentityFeignClient;
import com.ecomweb.shop_service.mapper.ShopAddressMapper;
import com.ecomweb.shop_service.mapper.ShopMapper;
import com.ecomweb.shop_service.producer.ShopProducer;
import com.ecomweb.shop_service.repository.ShopRepository;
import com.ecomweb.shop_service.util.RedisCacheHelper;
import com.ecomweb.shop_service.util.RedisKey;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

// try chi co feign nen bat feignex la du
// bat exception neu con nhung code logic khac

// tuy nhien k chi nen catch moi exception vi neu co nhieu subexxception khac thi se k get duoc body va status dung do moi subex co cach get khac nhau
// Exception chỉ có message, cause, stacktrace -> nên bắt subclass của ex là dạng http ex thì mới lấy đc body và status
// neu k bat Exception de trả về postman dạng không theo cấu trúc định sẵn, status mặc định 500 -> do spring config
// cac loi checked luon phai xu ly bang code bang cach try catch hoac throw => handler cho exception chung de dua ve format chung de co the truyen loi di cho khac


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// transaction annotation chi dam bao neu lan save thu 2 fail thi back lai lan save dau tien
// khi co them call api phai handler bang mesage broker
// muon transaction rollback khi catch thi phai throw lai loi
// try catch bbat loi truoc globalExceptinoHanlder
// neu loi do save thi k can transactional, nhung neu loi throw ra kp do save thi can (TH 1 save)
// voi mông muon bat dc transactional phai cònig sang replica chu k dung standalone


// neu catch loi thi no se k in stack trace -> tu in
// neu loi bi bat bang handler thi cung phai log trace ra neu muon in
public class ShopService {

    // choreo, gửi message kiểu dây chuyền service này sang serviec kia và cứ thế, thường dùng trong nghiệp vụ phụ
    // orches, dùng trong các nghiệp vụ chính, thường là sync nhưu code bên dưới là orches do có 1 service chính quản lý
    // khi nghiệp vụ dài ngta thường triển khai dưới dạng 1 pattern, còn 1-2 service có thể xử lí trong method
    ShopRepository shopRepository;
    ShopMapper shopMapper;
    ShopAddressMapper shopAddressMapper;
    WebClient webClient;
    ShopProducer shopProducer;
    RedisCacheHelper cacheHelper;
    IdentityFeignClient identityFeignClient;

    // inline saga voi UC don gian
    // UC phuc tap su dung orchestrator
    // Orchestrator Saga và Choreography Saga KHÔNG đảm bảo ACID tuyệt đối ma chi la huy logic chu kp rollback db
    // Trong microservice, ACID gần như không khả thi nếu cross-service, trừ khi dùng 2PC/XA transaction
   // compensation fail -> dua vao dlq

    @Transactional
    public void create(ShopCreateRequest shopCreateRequest) throws Exception {
        // chan neu da co shop
        log.info("Transaction active? {}", TransactionSynchronizationManager.isActualTransactionActive());
        String userId = "";

        // loi se bi bat boi handle
        userId = identityFeignClient.upgradeToSeller(UpgradeSellerRequest.builder()
                .shopName(shopCreateRequest.getName())
                .build()).getResult().toString();

        Shop shop = shopMapper.toShop(shopCreateRequest);
        ShopAddress shopAddress = shopAddressMapper.toShopAddress(shopCreateRequest);
        shop.setShopAddress(shopAddress);
        shop.setUserId(userId);

        try{

            shopRepository.save(shop);
        }
        catch(Exception ex){
            UserSnapshot data = cacheHelper.getFromCache(RedisKey.ROLLBACK_TO_SELLER.getKey()+userId,  UserSnapshot.class);
            shopProducer.shopCreationFailed(ShopCreationFailed.builder()
                            .userSnapshot(data)
                    .build());
            throw ex;
        }


    }

    public ShopResponse getInfo() {

        String userId = identityFeignClient
                .getUserId(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult().toString();
        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(()-> new AppException(ErrorCode.SHOP_NOT_EXISTS));
        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
        return shopResponse;
    }

    public void update(ShopUpdateRequest shopUpdateRequest) {
        String userId = identityFeignClient
                .getUserId(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult().toString();

        Shop shop = shopRepository.findByUserId(userId)
                .orElseThrow(()-> new AppException(ErrorCode.SHOP_NOT_EXISTS));

        shopMapper.toShop(shop,shopUpdateRequest);
        ShopAddress shopAddress = shop.getShopAddress();
        shopAddressMapper.toShopAddress(shopAddress,shopUpdateRequest);
        shopRepository.save(shop);

    }


    public ShopResponse getInfoById(String id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTS));
        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
        return shopResponse;
    }

    public String getUserIdByShopId(String shopId){
        Shop shop = shopRepository.findById(shopId).orElseThrow(
                ()-> new AppException(ErrorCode.SHOP_NOT_EXISTS)
        );
        return shop.getUserId();
    }

    public String getShopIdByUserId(String userId){
        Shop shop = shopRepository.findByUserId(userId).orElseThrow(
                ()-> new AppException(ErrorCode.SHOP_NOT_EXISTS)
        );
        return shop.getId();
    }

    public List<String> getShopIdByProvinceId(String provinceId){
        return shopRepository.findByShopAddressProvinceId(provinceId)
                .stream().map(Shop::getId).collect(Collectors.toList());
    }





}