package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.ProductVariantRequest;
import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.ProductVariantResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.ProductVariant;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "detailAttributes", ignore = true)
    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);


    @Mapping(target = "detailAttributes", ignore = true)
    // annotation giup khong map cac gia trị null vào doi tuong cu
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toProductVariant(@MappingTarget ProductVariant productVariant, ProductVariantRequest productVariantRequest);
}

