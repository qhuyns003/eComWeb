package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.ProductImageRequest;
import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.ProductImageResponse;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
// \" = " để tránh hiểu " là dấu kết thúc chuỗi
    @Mapping(target = "url", expression = "java(\""+ ImagePrefix.IMAGE_PREFIX +"\" + productImage.getUrl())")
    ProductImageResponse toProductImageResponse(ProductImage productImage);


    @Mapping(target = "url", expression = "java(productImageRequest.getUrl() != null " +

            "? productImageRequest.getUrl().substring(\""+ ImagePrefix.IMAGE_PREFIX +"\".length()) : productImageRequest.getUrl())")
    ProductImage toProductImage(ProductImageRequest productImageRequest);

}
