package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.ReviewProductResponse;
import com.qhuyns.ecomweb.dto.response.ShopResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.CustomerReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerReviewMapper {

    //    @Mapping(target = "roles", ignore = true)
    ReviewProductResponse toReviewProductResponse(CustomerReview customerReview);

}
