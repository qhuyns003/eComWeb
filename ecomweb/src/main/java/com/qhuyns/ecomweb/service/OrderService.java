package com.qhuyns.ecomweb.service;


import com.qhuyns.ecomweb.dto.request.OrderItemRequest;
import com.qhuyns.ecomweb.dto.request.OrderRequest;
import com.qhuyns.ecomweb.dto.request.OrderShopGroupRequest;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.entity.Coupon;
import com.qhuyns.ecomweb.entity.Order;
import com.qhuyns.ecomweb.entity.OrderItem;
import com.qhuyns.ecomweb.entity.OrderShopGroup;
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
import org.springframework.stereotype.Service;

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
    public void create(OrderRequest orderRequest) {
      Order order = orderMapper.toOrder(orderRequest);
      order.setUserAddress(userAddressRepository.findById(orderRequest.getUserAddressId()).orElseThrow(
              () -> new AppException(ErrorCode.USER_ADDRESS_NOT_EXISTS)
      ));
      for(String couponId : orderRequest.getCouponIds()) {
          Coupon coupon = couponRepository.findById(couponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
          order.getCoupons().add(coupon);
      }
      for(OrderShopGroupRequest orderShopGroupRequest : orderRequest.getOrderShopGroups()){
          OrderShopGroup orderShopGroup = orderShopGroupMapper.toOrderShopGroup(orderShopGroupRequest);
          for(String shopCouponId : orderShopGroupRequest.getShopCouponIds()) {
              Coupon shopCoupon = couponRepository.findById(shopCouponId).orElseThrow(()-> new AppException(ErrorCode.COUPON_NOT_EXISTS));
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
    }


}
