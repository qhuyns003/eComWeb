package com.ecomweb.order_service.service;


import com.ecomweb.order_service.dto.request.OrderItemRequest;
import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.request.OrderShopGroupRequest;
import com.ecomweb.order_service.dto.response.OrderResponse;
import com.ecomweb.order_service.dto.response.UserResponse;
import com.ecomweb.order_service.entity.*;
import com.ecomweb.order_service.exception.AppException;
import com.ecomweb.order_service.exception.ErrorCode;
import com.ecomweb.order_service.feignClient.IdentityFeignClient;
import com.ecomweb.order_service.mapper.OrderItemMapper;
import com.ecomweb.order_service.mapper.OrderMapper;
import com.ecomweb.order_service.mapper.OrderShopGroupMapper;
import com.ecomweb.order_service.mapper.ShippingAddressMapper;
import com.ecomweb.order_service.repository.CouponRepository;
import com.ecomweb.order_service.repository.OrderItemRepository;
import com.ecomweb.order_service.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {
    OrderItemRepository orderItemRepository;
    IdentityFeignClient identityFeignClient;
  public boolean existsOrderForProduct(List<String> variantIds) {
      return orderItemRepository.existsOrderForProduct(variantIds);
  }

    public Long getNumberOfOrder(List<String> variantIds) {
        return orderItemRepository.getNumberOfOrder(variantIds);
    }

    public UserResponse getOwnerOfOrder(String orderItemId) {
      String customerId = orderItemRepository.findByCustomerReviewId(orderItemId)
              .orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTS)).getOrderShopGroup().getOrder().getUserId();
      UserResponse userResponse = identityFeignClient.getActivatedUser(customerId).getResult();
      return userResponse;
    }
}
