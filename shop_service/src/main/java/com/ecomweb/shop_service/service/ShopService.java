package com.ecomweb.shop_service.service;


import com.ecomweb.shop_service.dto.request.IntrospectRequest;
import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.IntrospectResponse;
import com.ecomweb.shop_service.entity.Shop;
import com.ecomweb.shop_service.entity.ShopAddress;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import com.ecomweb.shop_service.mapper.ShopAddressMapper;
import com.ecomweb.shop_service.mapper.ShopMapper;
import com.ecomweb.shop_service.repository.ShopRepository;
import com.ecomweb.shop_service.util.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {

    ShopRepository shopRepository;
    ShopMapper shopMapper;
    ShopAddressMapper shopAddressMapper;
    WebClient webClient;

    public void create(ShopCreateRequest shopCreateRequest) {

        String token = AuthUtil.getToken();
        webClient.post()
                .uri("http://service-b/api/users/toSeller")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class).flatMap(errorBody -> {
                            // Publish lỗi lên RabbitMQ
                            publishErrorMessage("ServiceB", response.statusCode(), errorBody, correlationId);
                            return Mono.error(new ServiceException(response.statusCode(), errorBody));
                        })
                )
                .bodyToMono(ResponseDTO.class) // map JSON về object
                .block();                     // block để lấy object trực tiếp



        Shop shop = shopMapper.toShop(shopCreateRequest);
        ShopAddress shopAddress = shopAddressMapper.toShopAddress(shopCreateRequest);
        shop.setShopAddress(shopAddress);
        shopRepository.save(shop);
    }

    public void update(ShopUpdateRequest shopUpdateRequest) {
        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Shop shop = user.getShop();
        shopMapper.toShop(shop,shopUpdateRequest);
        ShopAddress shopAddress = shop.getShopAddress();
        shopAddressMapper.toShopAddress(shopAddress,shopUpdateRequest);

        shopRepository.save(shop);
    }

    public ShopResponse getInfo() {
       Shop shop = shopRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName());
       ShopResponse shopResponse = shopMapper.toShopResponse(shop);
       shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
       return shopResponse;
    }

    public ShopResponse getInfoById(String id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTS));
        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
        return shopResponse;
    }

    public String getUserIdByShopId(String shopId){
        return shopRepository.findById(shopId).orElseThrow(
                ()-> new AppException(ErrorCode.SHOP_NOT_EXISTS)
        ).getUser().getId();
    }




}