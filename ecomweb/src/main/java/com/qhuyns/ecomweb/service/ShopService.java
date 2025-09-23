//package com.qhuyns.ecomweb.service;
//
//import com.qhuyns.ecomweb.constant.PredefinedRole;
//import com.qhuyns.ecomweb.dto.request.ShopCreateRequest;
//import com.qhuyns.ecomweb.dto.request.ShopUpdateRequest;
//import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
//import com.qhuyns.ecomweb.dto.response.ShopResponse;
//import com.qhuyns.ecomweb.entity.Role;
//import com.qhuyns.ecomweb.entity.Shop;
//import com.qhuyns.ecomweb.entity.ShopAddress;
//import com.qhuyns.ecomweb.entity.User;
//import com.qhuyns.ecomweb.exception.AppException;
//import com.qhuyns.ecomweb.exception.ErrorCode;
//import com.qhuyns.ecomweb.mapper.ShopAddressMapper;
//import com.qhuyns.ecomweb.mapper.ShopMapper;
//import com.qhuyns.ecomweb.repository.RoleRepository;
//import com.qhuyns.ecomweb.repository.ShopAddressRepository;
//import com.qhuyns.ecomweb.repository.ShopRepository;
//import com.qhuyns.ecomweb.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
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
//public class ShopService {
//
//    ShopRepository shopRepository;
//    UserRepository userRepository;
//    ShopMapper shopMapper;
//    ShopAddressMapper shopAddressMapper;
//    RoleRepository roleRepository;
//
//    public void create(ShopCreateRequest shopCreateRequest) {
//        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        Shop shop = shopMapper.toShop(shopCreateRequest);
//        ShopAddress shopAddress = shopAddressMapper.toShopAddress(shopCreateRequest);
//        shopAddress.setShop(shop);
//        shop.setShopAddress(shopAddress);
//        shop.setUser(user);
//        user.setShop(shop);
//        user.setFullName(shopCreateRequest.getName());
//        List<Role> roles = new ArrayList<>();
//        roles.add(roleRepository.findById(PredefinedRole.SELLER_ROLE).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS)));
//        user.getRoles().clear();
//        user.getRoles().addAll(roles);
//        userRepository.save(user);
//        shopRepository.save(shop);
//    }
//
//    public void update(ShopUpdateRequest shopUpdateRequest) {
//        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        Shop shop = user.getShop();
//        shopMapper.toShop(shop,shopUpdateRequest);
//        ShopAddress shopAddress = shop.getShopAddress();
//        shopAddressMapper.toShopAddress(shopAddress,shopUpdateRequest);
//
//        shopRepository.save(shop);
//    }
//
//    public ShopResponse getInfo() {
//       Shop shop = shopRepository.findByUserUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//       ShopResponse shopResponse = shopMapper.toShopResponse(shop);
//       shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
//       return shopResponse;
//    }
//
//    public ShopResponse getInfoById(String id) {
//        Shop shop = shopRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_EXISTS));
//        ShopResponse shopResponse = shopMapper.toShopResponse(shop);
//        shopResponse.setShopAddressResponse(shopAddressMapper.toShopAddressResponse(shop.getShopAddress()));
//        return shopResponse;
//    }
//
//    public String getUserIdByShopId(String shopId){
//        return shopRepository.findById(shopId).orElseThrow(
//                ()-> new AppException(ErrorCode.SHOP_NOT_EXISTS)
//        ).getUser().getId();
//    }
//
//
//
//
//}