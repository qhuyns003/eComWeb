//package com.qhuyns.ecomweb.service;
//
//import com.qhuyns.ecomweb.dto.response.CategoryResponse;
//import com.qhuyns.ecomweb.dto.response.PaymentResponse;
//import com.qhuyns.ecomweb.dto.response.RoleResponse;
//import com.qhuyns.ecomweb.entity.Payment;
//import com.qhuyns.ecomweb.mapper.CategoryMapper;
//import com.qhuyns.ecomweb.repository.CategoryRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
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
//public class PaymentService {
//
//    public List<PaymentResponse> getAll() {
//        List<PaymentResponse> paymentResponseList = new ArrayList<>();
//        for(Payment payment : Payment.values()) {
//            paymentResponseList.add(new PaymentResponse().builder()
//                            .description(payment.getDescription())
//                            .name(payment.name())
//                    .build());
//        };
//        return paymentResponseList;
//    }
//}
