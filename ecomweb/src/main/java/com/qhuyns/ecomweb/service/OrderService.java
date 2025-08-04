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
import com.qhuyns.ecomweb.mapper.OrderItemMapper;
import com.qhuyns.ecomweb.mapper.OrderMapper;
import com.qhuyns.ecomweb.mapper.OrderShopGroupMapper;
import com.qhuyns.ecomweb.mapper.RoleMapper;
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
    public OrderResponse create(OrderRequest orderRequest) {
        // kiem tra khong trung
      Order order = orderMapper.toOrder(orderRequest);
      order.setStatus(OrderStatus.PENDING);
      order.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(
              ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
      ));
      order.setUserAddress(userAddressRepository.findById(orderRequest.getUserAddressId()).orElseThrow(
              () -> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)
      ));
      for(String couponId : orderRequest.getCouponIds()) {
          Coupon coupon = couponRepository.findById(couponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
          if(coupon.getUsed().compareTo(coupon.getQuantity())<0){
            coupon.setUsed(coupon.getUsed().add(BigDecimal.ONE));
          }
          order.getCoupons().add(coupon);
      }
      for(OrderShopGroupRequest orderShopGroupRequest : orderRequest.getOrderShopGroups()){
          OrderShopGroup orderShopGroup = orderShopGroupMapper.toOrderShopGroup(orderShopGroupRequest);
          for(String shopCouponId : orderShopGroupRequest.getShopCouponIds()) {
              Coupon shopCoupon = couponRepository.findById(shopCouponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
              if(shopCoupon.getUsed().compareTo(shopCoupon.getQuantity())<0){
                  shopCoupon.setUsed(shopCoupon.getUsed().add(BigDecimal.ONE));
              }
              orderShopGroup.getCoupons().add(shopCoupon);
          }
          for(OrderItemRequest oir: orderShopGroupRequest.getOrderItems()){
              OrderItem orderItem = orderItemMapper.toOrderItem(oir);
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


}
