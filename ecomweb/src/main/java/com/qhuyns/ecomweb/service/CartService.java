package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.dto.response.PaymentResponse;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.repository.CartRepository;
import com.qhuyns.ecomweb.repository.ProductVariantRepository;
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
    ProductVariantRepository pvRepository;;
    public List<CartResponse> getAll() {
        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true).orElseThrow(
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
                    .imageUrl(ImagePrefix.IMAGE_PREFIX+product.getImages().stream().filter(img -> img.getIsMain()==true).findFirst().get().getUrl())
                    .build();
            cartResponseList.add(cartResponse);
        };
        return cartResponseList;
    }

    public void addToCart(CartRequest cartRequest) {
        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        ProductVariant pv = pvRepository.findById(cartRequest.getProductVariantId()).orElseThrow(
                () -> new AppException(ErrorCode.VARIANT_NOT_FOUND)
        );
        if(pv.getStock()<=0){
            throw new AppException(ErrorCode.DONNT_ENOUGH_PRODUCT);
        }
        Cart cart = Cart.builder()
                .user(user)
                .quantity(cartRequest.getQuantity())
                .productVariant(pv)
                .build();
        cartRepository.save(cart);
    }
}
