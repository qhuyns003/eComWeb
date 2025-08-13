package com.qhuyns.ecomweb.service;


import com.qhuyns.ecomweb.dto.request.OrderItemRequest;
import com.qhuyns.ecomweb.dto.request.OrderRequest;
import com.qhuyns.ecomweb.dto.request.OrderShopGroupRequest;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.OrderResponse;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.*;
import com.qhuyns.ecomweb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderMapper orderMapper;
    OrderShopGroupMapper  orderShopGroupMapper;
    OrderItemMapper orderItemMapper;
    UserAddressRepository  userAddressRepository;
    CouponRepository  couponRepository;
    OrderRepository orderRepository;
    UserRepository userRepository;
    ProductVariantRepository  productVariantRepository;
    ShopRepository  shopRepository;
    ShippingAddressMapper  shippingAddressMapper;
    public OrderResponse create(OrderRequest orderRequest) {
        // kiem tra khong trung
      Order order = orderMapper.toOrder(orderRequest);
      order.setStatus(OrderStatus.PENDING);
      order.setUser(userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true).orElseThrow(
              ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
      ));
//      order.setUserAddress(userAddressRepository.findById(orderRequest.getUserAddressId()).orElseThrow(
//              () -> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)
//      ));
      ShippingAddress shippingAddress = shippingAddressMapper.toShippingAddress(
                userAddressRepository.findById(orderRequest.getUserAddressId()).orElseThrow(()-> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)));
      shippingAddress.setOrder(order);
      order.setShippingAddress(shippingAddress);
      for(String couponId : orderRequest.getCouponIds()) {
          Coupon coupon = couponRepository.findById(couponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
          if(coupon.getUsed().compareTo(coupon.getQuantity())<0){
            coupon.setUsed(coupon.getUsed().add(BigDecimal.ONE));
          }
          order.getCoupons().add(coupon);
      }
      for(OrderShopGroupRequest orderShopGroupRequest : orderRequest.getOrderShopGroups()){
          OrderShopGroup orderShopGroup = orderShopGroupMapper.toOrderShopGroup(orderShopGroupRequest);
          orderShopGroup.setShop(shopRepository.findById(orderShopGroupRequest.getShopId()).orElseThrow(
                  ()->new AppException(ErrorCode.SHOP_NOT_EXISTS)));
          for(String shopCouponId : orderShopGroupRequest.getShopCouponIds()) {
              Coupon shopCoupon = couponRepository.findById(shopCouponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
              if(shopCoupon.getUsed().compareTo(shopCoupon.getQuantity())<0){
                  shopCoupon.setUsed(shopCoupon.getUsed().add(BigDecimal.ONE));
              }
              orderShopGroup.getCoupons().add(shopCoupon);
          }
          for(OrderItemRequest oir: orderShopGroupRequest.getOrderItems()){
              OrderItem orderItem = orderItemMapper.toOrderItem(oir);
              ProductVariant productVariant = productVariantRepository.findById(oir.getProductVariantId()).orElseThrow(
                      () -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
              orderItem.setProductVariant(productVariant);
              orderItem.setOrderShopGroup(orderShopGroup);
              orderShopGroup.getOrderItems().add(orderItem);
          }
          orderShopGroup.setOrder(order);
          order.getOrderShopGroups().add(orderShopGroup);
      }
      orderRepository.save(order);
      return orderMapper.toOrderResponse(order);
    }
     public void existingOrder(String orderId) {
        orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTS));
     }

     public void changeStatus(OrderStatus orderStatus,String id) {
        Order order = orderRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTS));
        order.setStatus(orderStatus);
        orderRepository.save(order);
     }

    public void delete(String id) {
       orderRepository.deleteById(id);
    }


}
