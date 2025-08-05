package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.dto.response.PaymentResponse;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.ShopMapper;
import com.qhuyns.ecomweb.repository.CartRepository;
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
public class CartService {

    UserRepository userRepository;
    CartRepository cartRepository;
    ShopMapper shopMapper;
    public List<CartResponse> getAll() {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        List<Cart> carts = cartRepository.findByUserId(user.getId());
        List<CartResponse> cartResponseList = new ArrayList<>();
        for (Cart cart : carts) {
            ProductVariant pv = cart.getProductVariant();
            Product product = pv.getProduct();
            List<String> dan = cart.getProductVariant().getDetailAttributes().stream().map(da -> da.getName() )
                    .collect(Collectors.toList());
            CartResponse cartResponse = CartResponse.builder()
                    .id(cart.getId())
                    .quantity(cart.getQuantity())
                    .variantId(pv.getId())
                    .variantPrice(pv.getPrice())
                    .productName(product.getName())
                    .width(product.getWidth())
                    .height(product.getHeight())
                    .length(product.getLength())
                    .weight(product.getWeight())
                    .shop(shopMapper.toShopResponse(product.getShop()))
                    .detailAttributes(dan)
                    .imageUrl(product.getImages().stream().filter(img -> img.getIsMain()==true).findFirst().get().getUrl())
                    .build();
            cartResponseList.add(cartResponse);
        };
        return cartResponseList;
    }
}
