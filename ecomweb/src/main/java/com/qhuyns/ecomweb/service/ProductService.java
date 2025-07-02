package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.DetailAttributeRequest;
import com.qhuyns.ecomweb.dto.request.ProductAttributeRequest;
import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import com.qhuyns.ecomweb.entity.ProductImage;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.*;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.repository.DetailAttributeRepository;
import com.qhuyns.ecomweb.repository.ProductAttributeRepository;
import com.qhuyns.ecomweb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductImageService productImageService;
    ProductImageMapper productImageMapper;
    ShopMapper shopMapper;
    ProductVariantMapper productVariantMapper;
    ProductAttributeMapper productAttributeMapper;
    DetailAttributeMapper detailAttributeMapper;
    CategoryMapper categoryMapper;
    CategoryService categoryService;
    CategoryRepository categoryRepository;
    ProductAttributeRepository productAttributeRepository;
    DetailAttributeRepository detailAttributeRepository;

    public List<ProductOverviewResponse> findTopSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findTopSellingProducts(PageRequest.of(0, limit));
        List<ProductOverviewResponse> productOverviewResponses = new ArrayList<>();
        for (Object[] rs : results) {
            Product product = (Product) rs[0]; // có thể ép xuống lớp con vì vốn dĩ rs[0] là 1 íntance của Product (có các attribute của nó)
            Double rating = (Double)rs[2];
            BigDecimal count = (BigDecimal.valueOf((long)rs[1])) ;
            ProductOverviewResponse productOverviewResponse = productMapper.toProductOverviewResponse(product);
            productOverviewResponse.setImages(productImageService.getAllById(product.getId()));
            productOverviewResponse.setRating(rating != null ? rating : 0.0);
            productOverviewResponse.setNumberOfOrder(count != null ? count : BigDecimal.valueOf(0));
            productOverviewResponses.add(productOverviewResponse);
        }
        return productOverviewResponses;
    }

    public List<ProductOverviewResponse> findNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findNewestProducts(PageRequest.of(0, limit));
        List<ProductOverviewResponse> productOverviewResponses = new ArrayList<>();
        for (Object[] rs : results) {
            Product product = (Product) rs[0]; // có thể ép xuống lớp con vì vốn dĩ rs[0] là 1 íntance của Product (có các attribute của nó)
            Double rating = (Double)rs[2];
            BigDecimal count = (BigDecimal.valueOf((long)rs[1])) ;
            ProductOverviewResponse productOverviewResponse = productMapper.toProductOverviewResponse(product);
            productOverviewResponse.setImages(productImageService.getAllById(product.getId()));
            productOverviewResponse.setRating(rating != null ? rating : 0.0);
            productOverviewResponse.setNumberOfOrder(count != null ? count : BigDecimal.valueOf(0));
            productOverviewResponses.add(productOverviewResponse);
        }
        return productOverviewResponses;
    }

    public ProductDetailResponse getDetailProduct(String id) {
        Object[] rs = productRepository.findNumberOfOrderAndRating(id);
        Product product = productRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        ProductDetailResponse productDetailResponse = productMapper.toProductDetailResponse(product);
        productDetailResponse.setImages(product.getImages().stream()
                .map(img -> productImageMapper.toProductImageResponse(img))
                .toList());
        productDetailResponse.setNumberOfOrder(BigDecimal.valueOf((Long)((Object[]) rs[0])[0])) ;
        productDetailResponse.setRating((Double) ((Object[]) rs[0])[1]);
        productDetailResponse.setProductAttributes(product.getProductAttributes().stream().map(pa -> {
            ProductAttributeResponse productAttributeResponse = productAttributeMapper.toProductAttributeResponse(pa);
            productAttributeResponse.setDetailAttributes(pa.getDetailAttributes().stream().map(
                    da -> detailAttributeMapper.toDetailAttributeResponse(da)
            ).toList());
            return productAttributeResponse;
        }).toList());
        productDetailResponse.setShop(shopMapper.toShopResponse(product.getShop()));

        List<ProductVariantResponse> variantResponses = product.getProductVariants().stream()
                .map(variant -> {
                    ProductVariantResponse variantResponse = productVariantMapper.toProductVariantResponse(variant);

                    List<DetailAttributeResponse> detailAttributeResponses = variant.getDetailAttributes().stream()
                            .map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).toList();
                    variantResponse.setDetailAttributes(detailAttributeResponses);
                    
                    return variantResponse;
                })
                .toList();
        
        productDetailResponse.setProductVariants(variantResponses);
        
        return productDetailResponse;
    }


    public Page<ProductResponse> findProductsWithMainImageByUserId(String userId,int page, int size,String search, int status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = productRepository.findProductsWithMainImageByUserId(userId,search,status,pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Object[] rs : results.getContent()) {
            Product product = (Product) rs[0];
            ProductImage productImage = (ProductImage)rs[1];
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setImages(List.of(productImageMapper.toProductImageResponse(productImage)));
            productResponses.add(productResponse);
        }
        Page<ProductResponse> productResponsePage = new PageImpl<>(
                productResponses,
                results.getPageable(),
                results.getTotalPages()
        );

        return productResponsePage;
    }

    public ProductResponse getProductForUpdate(String id) {
        Product product = productRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        ProductResponse productResponse = productMapper.toProductResponse(product);
        productResponse.setImages(product.getImages().stream().map(img -> productImageMapper.toProductImageResponse(img)).toList());
        productResponse.setCategory(categoryMapper.toCategoryResponse(product.getCategory()));
        productResponse.setProductAttributes(product.getProductAttributes().stream().map(pa ->{
            ProductAttributeResponse productAttributeResponse = productAttributeMapper.toProductAttributeResponse(pa);
            productAttributeResponse.setDetailAttributes(pa.getDetailAttributes().stream().map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).toList());
            return productAttributeResponse;
        }).toList());
        productResponse.setProductVariants(product.getProductVariants().stream().map(pv -> {
            ProductVariantResponse productVariantResponse = productVariantMapper.toProductVariantResponse(pv);
            productVariantResponse.setDetailAttributes(pv.getDetailAttributes().stream().map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).toList());
            return productVariantResponse;
        }).toList());
        return productResponse;
    };

    public void update(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.toProduct(product,productRequest);
        product.setCategory(categoryRepository.findById(productRequest.getCategory().getId()).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        product.setImages(productRequest.getImages().stream().map(img -> productImageMapper.toProductImage(img)).toList());
        List<ProductAttribute> productAttributes = new ArrayList<>();
        for (ProductAttributeRequest par : productRequest.getProductAttributes()){
            if(productAttributeRepository.existsByName(StringUtils.capitalize(par.getName().toLowerCase()))){
                ProductAttribute productAttribute = productAttributeRepository.findByName(StringUtils.capitalize(par.getName().toLowerCase()));
                for (DetailAttributeRequest das : par.getDetailAttributes()){
                    if(detailAttributeRepository.existsByName(das.getName().toLowerCase())){

                    }
                    else{
                        
                    }
                }
            }
            else{

            }
        }
    };




}
