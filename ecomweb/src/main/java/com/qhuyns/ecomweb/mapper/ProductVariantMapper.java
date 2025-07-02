package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.ProductVariantResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.ProductVariant;
import com.qhuyns.ecomweb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "detailAttributes", ignore = true)
    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);

}

