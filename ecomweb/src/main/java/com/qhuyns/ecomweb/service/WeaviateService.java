package com.qhuyns.ecomweb.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.ProductImageResponse;
import com.qhuyns.ecomweb.dto.response.ProductResponse;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductImage;
import com.qhuyns.ecomweb.mapper.ProductImageMapper;
import com.qhuyns.ecomweb.mapper.ProductMapper;
import com.qhuyns.ecomweb.mapper.RoleMapper;
import com.qhuyns.ecomweb.repository.PermissionRepository;
import com.qhuyns.ecomweb.repository.ProductRepository;
import com.qhuyns.ecomweb.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeaviateService {

    @Autowired
    RestTemplate restTemplate;
    private static final String WEAVIATE_GRAPHQL = "http://localhost:8082/v1/graphql";
    private static final String WEAVIATE_OBJECTS = "http://localhost:8082/v1/objects";
    private static final String WEAVIATE_SCHEMA = "http://localhost:8082/v1/schema";

    ProductRepository productRepository;
    ProductImageMapper productImageMapper;
    ProductMapper productMapper;

    public void addProduct(String productId, String base64Image)throws IOException {


        String json = "{"
                + "\"class\": \"Product\","
                + "\"properties\": {"
                + "\"productId\": \"" + productId + "\","
                + "\"image\": \"" + base64Image + "\""
                + "}"
                + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(WEAVIATE_OBJECTS, entity, String.class);

    }
    public void searchByImage(MultipartFile image)throws IOException {
        double certainty = 0.85;
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // GraphQL query có thêm certainty
        String graphql = "{ \"query\": \"{ Get { Product(nearImage: {image: \\\"" + base64Image +
                "\\\", certainty: " + certainty +
                "}, limit: 5) { productId image _additional { certainty distance } } } }\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);
    }


    public void deleteByProductId(String productId) {
        // 1. Truy vấn lấy tất cả objectId theo productId
        String graphql = "{ \"query\": \"{ Get { Product(where: {path: [\\\"productId\\\"], operator: Equal, valueString: \\\"" + productId + "\\\"}) { _additional { id } } } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);

        // 2. Parse tất cả id từ response (dùng regex lấy UUID)
        String body = response.getBody();
        java.util.List<String> objectIds = new java.util.ArrayList<>();
        java.util.regex.Pattern uuidPattern = java.util.regex.Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
        java.util.regex.Matcher matcher = uuidPattern.matcher(body);
        while (matcher.find()) {
            objectIds.add(matcher.group());
        }


        // 3. Gọi DELETE cho từng object
        int deleted = 0;
        for (String objectId : objectIds) {
            String deleteUrl = WEAVIATE_OBJECTS + "/Product/" + objectId;
            try {
                restTemplate.delete(deleteUrl);
                deleted++;
            } catch (Exception e) {
                log.info(e.getMessage());
                // Bỏ qua lỗi từng object
            }
        }
    }

    public Page<ProductResponse> search(MultipartFile image, double certainty, int page, int size) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // GraphQL query có thêm certainty
        String graphql = "{ \"query\": \"{ Get { Product(nearImage: {image: \\\"" + base64Image +
                "\\\", certainty: " + certainty +
                "}, limit: 100) { productId _additional { certainty distance } } } }\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);

        String body = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        JsonNode products = root.path("data").path("Get").path("Product");
        List<String> ids = new ArrayList<>();
        for (JsonNode product : products) {
            String id = product.path("productId").asText();
            ids.add(id);
        }

        // Phân trang danh sách ids
        int fromIndex = Math.min(page * size, ids.size());
        int toIndex = Math.min(fromIndex + size, ids.size());
        List<String> pagedIds = ids.subList(fromIndex, toIndex);

        List<Product> productList = productRepository.findAllById(pagedIds);

        Pageable pageable = PageRequest.of(page, size);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : productList) {
            ProductImageResponse productImageResponse = new ProductImageResponse();
            for(ProductImage productImage: product.getImages()){
                if(productImage.getIsMain()){
                    productImageResponse =productImageMapper.toProductImageResponse(productImage);
                    break;
                }
            }
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setImages(new ArrayList<>(List.of(productImageResponse)));
            productResponses.add(productResponse);
        }

        Page<ProductResponse> productResponsePage = new PageImpl<>(
                productResponses,
                pageable,
                ids.size()
        );

        return productResponsePage;

    }
}
