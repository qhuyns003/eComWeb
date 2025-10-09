package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.*;
import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.feignClient.OrderFeignClient;
import com.qhuyns.ecomweb.feignClient.ShopFeignClient;
import com.qhuyns.ecomweb.mapper.*;
import com.qhuyns.ecomweb.repository.*;
import com.qhuyns.ecomweb.util.RedisCacheHelper;
import com.qhuyns.ecomweb.util.RedisKey;
import com.sun.jna.platform.win32.Sspi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductImageService productImageService;
    ProductImageMapper productImageMapper;
    ProductVariantMapper productVariantMapper;
    ProductAttributeMapper productAttributeMapper;
    DetailAttributeMapper detailAttributeMapper;
    CategoryMapper categoryMapper;
    CategoryService categoryService;
    CategoryRepository categoryRepository;
    ProductAttributeRepository productAttributeRepository;
    DetailAttributeRepository detailAttributeRepository;
    ProductImageRepository productImageRepository;
    FileService fileService;
    ProductVariantRepository productVariantRepository;
    WeaviateService  weaviateService;
    RedisCacheHelper cacheHelper;
    ShopFeignClient shopFeignClient;
    IdentityFeignClient identityFeignClient;
    private final OrderFeignClient orderFeignClient;

    // loi chi hien thi so luong mat hang da mua = da co review
    public List<ProductOverviewResponse> findTopSellingProducts(int limit) throws Exception {
        // Kiểm tra cache
        List<ProductOverviewResponse> cachedProducts = cacheHelper.getFromCache(RedisKey.TOP_SELLING_PROD.getKey(),  List.class);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findTopSellingProducts(PageRequest.of(0, limit));
        List<ProductOverviewResponse> productOverviewResponses = new ArrayList<>();
        for (Object[] rs : results) {
            Product product = (Product) rs[0]; // có thể ép xuống lớp con vì vốn dĩ rs[0] là 1 íntance của Product (có các attribute của nó)
            Double rating = (Double)rs[2];
            BigDecimal count = (BigDecimal.valueOf((long)rs[1])) ;
            ProductOverviewResponse productOverviewResponse = productMapper.toProductOverviewResponse(product);
            productOverviewResponse.setImages(productImageService.getAllByProductId(product.getId()));
            productOverviewResponse.setRating(rating != null ? rating : 0.0);
            productOverviewResponse.setNumberOfOrder(count != null ? count : BigDecimal.valueOf(0));
            productOverviewResponses.add(productOverviewResponse);
        }

        cacheHelper.saveToCache(RedisKey.TOP_SELLING_PROD.getKey(), productOverviewResponses, RedisKey.TOP_SELLING_PROD.getTtl());

        return productOverviewResponses;
    }

    public List<ProductOverviewResponse> findNewestProducts(int limit) throws Exception {

        // Kiểm tra cache
        List<ProductOverviewResponse> cachedProducts = cacheHelper.getFromCache(RedisKey.TOP_NEWEST_PROD.getKey(), List.class);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        List<ProductOverviewResponse> productOverviewResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findNewestProducts(PageRequest.of(0, limit));
        for (Object[] rs : results) {
            Product product = (Product) rs[0]; // có thể ép xuống lớp con vì vốn dĩ rs[0] là 1 íntance của Product (có các attribute của nó)
            Double rating = (Double)rs[2];
            BigDecimal count = (BigDecimal.valueOf((long)rs[1])) ;
            ProductOverviewResponse productOverviewResponse = productMapper.toProductOverviewResponse(product);
            productOverviewResponse.setImages(productImageService.getAllByProductId(product.getId()));
            productOverviewResponse.setRating(rating != null ? rating : 0.0);
            productOverviewResponse.setNumberOfOrder(count != null ? count : BigDecimal.valueOf(0));
            productOverviewResponses.add(productOverviewResponse);
        }

        cacheHelper.saveToCache(RedisKey.TOP_NEWEST_PROD.getKey(), productOverviewResponses, RedisKey.TOP_NEWEST_PROD.getTtl());
        return productOverviewResponses;
    }

    public ProductDetailResponse getDetailProduct(String id) {
        Object[] rs = productRepository.findNumberOfOrderAndRating(id);
        Product product = productRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductDetailResponse productDetailResponse = productMapper.toProductDetailResponse(product);
        productDetailResponse.setImages(product.getImages().stream()
                .map(img -> {
                    ProductImageResponse response = productImageMapper.toProductImageResponse(img);
                    response.setUrl(ImagePrefix.IMAGE_PREFIX+img.getUrl());
                    return  response;

                })
                .collect(Collectors.toList()));
        productDetailResponse.setNumberOfOrder(BigDecimal.valueOf((Long)((Object[]) rs[0])[0])) ;
        productDetailResponse.setRating((Double) ((Object[]) rs[0])[1]);
        productDetailResponse.setProductAttributes(product.getProductAttributes().stream().map(pa -> {
            ProductAttributeResponse productAttributeResponse = productAttributeMapper.toProductAttributeResponse(pa);
            productAttributeResponse.setDetailAttributes(pa.getDetailAttributes().stream().map(
                    da -> detailAttributeMapper.toDetailAttributeResponse(da)
            ).collect(Collectors.toList()));
            return productAttributeResponse;
        }).collect(Collectors.toList()));
        productDetailResponse.setShop(shopFeignClient.getShopInfoById(product.getShopId()).getResult());

        List<ProductVariantResponse> variantResponses = product.getProductVariants().stream()
                .map(variant -> {
                    ProductVariantResponse variantResponse = productVariantMapper.toProductVariantResponse(variant);

                    List<DetailAttributeResponse> detailAttributeResponses = variant.getDetailAttributes().stream()
                            .map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).collect(Collectors.toList());
                    variantResponse.setDetailAttributes(detailAttributeResponses);
                    
                    return variantResponse;
                })
                .collect(Collectors.toList());
        
        productDetailResponse.setProductVariants(variantResponses);
        
        return productDetailResponse;
    }


    public Page<ProductResponse> findProductsWithMainImageByUserId(String userId,int page, int size,String search, int status) {

        if (!identityFeignClient.existsById(userId).getResult()){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        Pageable pageable = PageRequest.of(page, size);
        String shopId = shopFeignClient.getShopIdByUserId(userId).getResult();
        Page<Object[]> results = productRepository.findProductsWithMainImageByShopId(shopId,search,status,pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Object[] rs : results.getContent()) {
            Product product = (Product) rs[0];
            ProductImage productImage = (ProductImage)rs[1];
            ProductResponse productResponse = productMapper.toProductResponse(product);

            productResponse.setImages(new ArrayList<>(List.of(productImageMapper.toProductImageResponse(productImage))));
            productResponses.add(productResponse);
        }
        Page<ProductResponse> productResponsePage = new PageImpl<>(
                productResponses,
                results.getPageable(),
                results.getTotalPages()
        );

        return productResponsePage;
    }

    public Page<ProductResponse> searchProduct(int page, int size,String search, String status,ProductFilterRequest productFilterRequest) {
        Sort sort = Sort.unsorted();
        if ("asc".equalsIgnoreCase(productFilterRequest.getSortPrice())) {
            sort = Sort.by("price").ascending();
        } else if ("desc".equalsIgnoreCase(productFilterRequest.getSortPrice())) {
            sort = Sort.by("price").descending();
        }
        Pageable pageable = PageRequest.of(page, size,sort);
        List<String> shopIds = null;
        if(productFilterRequest.getProvinceId() != null){
            shopIds = shopFeignClient.getShopIdByProvinceId(productFilterRequest.getProvinceId()).getResult();
        }
        Page<Object[]> results = productRepository.searchProduct(search,status,pageable,productFilterRequest,shopIds);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Object[] rs : results.getContent()) {

            ProductResponse productResponse = ProductResponse.builder()
                    .id((String)rs[0])
                    .name((String)rs[1])
                    .description((String)rs[2])
                    .price((BigDecimal)rs[3])
                    .status((String)rs[4])
                    .createdAt(((Timestamp) rs[5]).toLocalDateTime())
                    .images(List.of(ProductImageResponse.builder()
                            .id((String)rs[6])
                            .url(ImagePrefix.IMAGE_PREFIX+(String)rs[7])
                            .isMain((Boolean)rs[8])
                            .build()))
                    .build();
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
        productResponse.setImages(product.getImages().stream().map(img -> productImageMapper.toProductImageResponse(img)).collect(Collectors.toList()));
        productResponse.setCategory(categoryMapper.toCategoryResponse(product.getCategory()));
        productResponse.setProductAttributes(product.getProductAttributes().stream().map(pa ->{
            ProductAttributeResponse productAttributeResponse = productAttributeMapper.toProductAttributeResponse(pa);
            productAttributeResponse.setDetailAttributes(pa.getDetailAttributes().stream().map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).collect(Collectors.toList()));
            return productAttributeResponse;
        }).collect(Collectors.toList()));
        productResponse.setProductVariants(product.getProductVariants().stream().map(pv -> {
            ProductVariantResponse productVariantResponse = productVariantMapper.toProductVariantResponse(pv);
            productVariantResponse.setDetailAttributes(pv.getDetailAttributes().stream().map(da -> detailAttributeMapper.toDetailAttributeResponse(da)).collect(Collectors.toList()));
            return productVariantResponse;
        }).collect(Collectors.toList()));
        return productResponse;
    };


    @Transactional
    public void update(String id, ProductRequest productRequest) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.toProduct(product,productRequest);
        product.setCategory(categoryRepository.findById(productRequest.getCategory().getId()).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        List<String> urlImageList =  productRequest.getImages().stream().map(img -> img.getUrl()).collect(Collectors.toList());
        // xoa anh khoi bo nho vat ly
        for(ProductImage pi : product.getImages()){
            if(!urlImageList.contains(ImagePrefix.IMAGE_PREFIX+pi.getUrl())){
                fileService.deleteImage(pi.getUrl());
            }
        }
        // xoa anh khoi db
        // phai co orphan remove moi go tham chieu duoc
        product.getImages().clear();
        // các image sẽ được tạo mới (persist) trước khi các entity bị gỡ và xóa do orphanRemoval -> phải gỡ id khi muốn tạo đối tượng mới không sẽ bị trùng
        product.getImages().addAll(productRequest.getImages().stream().map(img -> {
            ProductImage pi = productImageMapper.toProductImage(img);
            pi.setId(null);
            pi.setProduct(product);
            return pi;
        }).collect(Collectors.toList()));

        productVariantRepository.softDeleteByIdNotIn(productRequest.getProductVariants().stream().map(pv -> pv.getId()).filter(x -> x != null).collect(Collectors.toList()));
        for(ProductVariantRequest pvr : productRequest.getProductVariants()){
            if(pvr.getId()!=null){
                ProductVariant productVariant = productVariantRepository.findById(pvr.getId()).orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));
                productVariantMapper.toProductVariant(productVariant,pvr);
                productVariant.setStatus("1");
            }
            else{
                ProductVariant pv = new ProductVariant();
                for(DetailAttributeRequest dar : pvr.getDetailAttributes()){
                    DetailAttribute da = detailAttributeRepository.findById(dar.getId()).orElseThrow(() -> new AppException(ErrorCode.DETAIL_ATTRIBUTE_NOT_EXISTS));
                    pv.getDetailAttributes().add(da);
                    da.getProductVariants().add(pv);
                }
                productVariantMapper.toProductVariant(pv,pvr);
                pv.setStatus("1");
                // chi can set phia ManyToOne la du de luu quan he
                pv.setProduct(product);
                // van phai set vao list de no thuc hien persist cho pv neu ta khong muon goi ra pvRepository
                product.getProductVariants().add(pv);

            }
        }
        productRepository.save(product);
        weaviateService.deleteByProductId(product.getId());
        for(ProductImage image: product.getImages()){
            URL url = new URL(ImagePrefix.IMAGE_PREFIX+image.getUrl());
            InputStream is = url.openStream();
            byte[] imageBytes = is.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            is.close();
            weaviateService.addProduct(product.getId(),base64Image);
        }
        log.info("success");
    };

    @Transactional
    public void create( ProductRequest productRequest) throws IOException {
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(categoryRepository.findById(productRequest.getCategory().getId()).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        product.getImages().addAll(productRequest.getImages().stream().map(img -> {
            ProductImage pi = productImageMapper.toProductImage(img);
            pi.setId(null);
            pi.setProduct(product);
            return pi;
        }).collect(Collectors.toList()));
        product.setStatus("1");
        String userId = identityFeignClient.getUserIdByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getResult();
        product.setShopId(shopFeignClient.getShopIdByUserId(userId).getResult());

        for(ProductAttributeRequest par : productRequest.getProductAttributes()){
            ProductAttribute pa = productAttributeMapper.toProductAttribute(par);
            for (DetailAttributeRequest dar : par.getDetailAttributes()){
                DetailAttribute da = detailAttributeMapper.toDetailAttribute(dar);
                pa.getDetailAttributes().add(da);
                da.setProductAttribute(pa);
            }
            product.getProductAttributes().add(pa);
            pa.setProduct(product);
        }
        // khi save , trước khi được persist các annotation gen data đi UUID hay CreateDate
        // sẽ gắn dữ liệu vào entity nên bản chất save không cập nhật ngược dữ liệu lên enotty BE
        // nếu getList ra (TH lazy) thì sẽ cập nhật cả dữ liệu list (nếu có cascade không thì cũng không có ý nghĩa)
        // goi getList bao gio thi moi truy van luc do de lay du lieu
        // ben one persist truoc moi co khoa ngoai de persist ben many
        // owner la ben giu khoa ngoai (many)- ben k co mappedby
        // orphanRemoval thuc hien cuoi cung
        // khi tao moi A ma muon dung lai B co san ->  phai luu B trong A o dang tham chieu (lay ra bang find tu db) chu k duoc mapper sang enitty moi bang builder hay mapstruct
        // khi co id trung thi se luon la merge khi save ke ca lay tham chieu hay tao moi
        // tuy nhien, neu A persist thì cung se ep B persist theo se gay ra loi vi neu B co id san roi thi phai la merge -> B phai lay tham chieu se k bi loi do persist k anh huong den managed object
        // merge co the thuc hien tren 1 entity co id k ton tai trong db, tu dong chuyen thanh persist
        productRepository.save(product);
     for(ProductVariantRequest pvr : productRequest.getProductVariants()){
                ProductVariant pv = new ProductVariant();
                for(DetailAttributeRequest dar : pvr.getDetailAttributes()){
                    DetailAttribute da = detailAttributeRepository.findByProductIdAndAttributeNameAndDetailName(product.getId(), dar.getProductAttribute().getName(),dar.getName());
                    pv.getDetailAttributes().add(da);
                    da.getProductVariants().add(pv);
                }
                productVariantMapper.toProductVariant(pv,pvr);
                pv.setStatus("1");
                pv.setProduct(product);
                product.getProductVariants().add(pv);
        }
     productRepository.save(product);
     for(ProductImage image: product.getImages()){
         URL url = new URL(ImagePrefix.IMAGE_PREFIX+image.getUrl());
         InputStream is = url.openStream();
         byte[] imageBytes = is.readAllBytes();
         String base64Image = Base64.getEncoder().encodeToString(imageBytes);
         is.close();
         weaviateService.addProduct(product.getId(),base64Image);
     }
    };

    public void delete(List<String> ids) {

        for(String id : ids){
            Product product = productRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if(orderFeignClient.existsOrderByProductId(product.getProductVariants().stream()
                    .map(pv -> pv.getId()).collect(Collectors.toList())).getResult()){
                throw new AppException(ErrorCode.PRODUCT_HAS_ORDER,product.getName());
            }
            for(ProductImage img : product.getImages()){
                fileService.deleteImage(img.getUrl());
            }
            weaviateService.deleteByProductId(id);
        }
        productRepository.deleteByIdIn(ids);
    };

    public List<ProductOverviewResponse> findByShopId(int limit,String id) throws Exception {
        // Kiểm tra cache
        List<ProductOverviewResponse> cachedProducts = cacheHelper.getFromCache(RedisKey.NEWEST_PROD_OF_SHOP.getKey()+id,  List.class);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findByShopIdAndNewest(pageable,id);
        List<ProductOverviewResponse> productOverviewResponses = new ArrayList<>();
        for (Object[] rs : results) {
            Product product = (Product) rs[0]; // có thể ép xuống lớp con vì vốn dĩ rs[0] là 1 íntance của Product (có các attribute của nó)
            Double rating = (Double)rs[2];
            BigDecimal count = (BigDecimal.valueOf((long)rs[1])) ;
            ProductOverviewResponse productOverviewResponse = productMapper.toProductOverviewResponse(product);
            productOverviewResponse.setImages(productImageService.getAllByProductId(product.getId()));
            productOverviewResponse.setRating(rating != null ? rating : 0.0);
            productOverviewResponse.setNumberOfOrder(count != null ? count : BigDecimal.valueOf(0));
            productOverviewResponses.add(productOverviewResponse);
        }

        cacheHelper.saveToCache(RedisKey.NEWEST_PROD_OF_SHOP.getKey()+id
                , productOverviewResponses
                , RedisKey.NEWEST_PROD_OF_SHOP.getTtl());

        return productOverviewResponses;
    }




}
