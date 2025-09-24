package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.entity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "images",ignore = true)
    ProductOverviewResponse toProductOverviewResponse(Product product);

    // mot so truong hop neu co san mapper thi no se tu dong mapper sang kieu response
    @Mapping(target = "images",ignore = true)
    @Mapping(target = "shop",ignore = true)
    @Mapping(target = "productVariants",ignore = true)
    @Mapping(target = "productAttributes",ignore = true)
    @Mapping(target = "category",ignore = true)
    ProductDetailResponse toProductDetailResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images",ignore = true)
    @Mapping(target = "shop",ignore = true)
    @Mapping(target = "productVariants",ignore = true)
    @Mapping(target = "productAttributes",ignore = true)
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images",ignore = true)
    @Mapping(target = "productVariants",ignore = true)
    @Mapping(target = "productAttributes",ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toProduct(@MappingTarget Product product, ProductRequest productRequest);


    // mapper hoạt động nhờ builder, nên muốn giữ giá trị khởi tạo của entity phải Builder.Dèault
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images",ignore = true)
    @Mapping(target = "productVariants",ignore = true)
    @Mapping(target = "productAttributes",ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product toProduct( ProductRequest productRequest);


}
