package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.DetailAttributeResponse;
import com.qhuyns.ecomweb.dto.response.ProductAttributeResponse;
import com.qhuyns.ecomweb.dto.response.ProductVariantResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.DetailAttribute;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import com.qhuyns.ecomweb.entity.ProductVariant;
import com.qhuyns.ecomweb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DetailAttributeMapper {

    DetailAttributeResponse toDetailAttributeResponse(DetailAttribute detailAttribute);

}

