package com.qhuyns.ecomweb.controller;

import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.ProductResponse;
import com.qhuyns.ecomweb.service.WeaviateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/weaviate")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WeaviateController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String WEAVIATE_GRAPHQL = "http://localhost:8082/v1/graphql";
    private static final String WEAVIATE_OBJECTS = "http://localhost:8082/v1/objects";
    private static final String WEAVIATE_SCHEMA = "http://localhost:8082/v1/schema";

    WeaviateService  weaviateService;

    @PostMapping("/search")
    public ApiResponse<Page<ProductResponse>> search(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam MultipartFile image,
            @RequestParam(defaultValue = "0.85") double certainty // mặc định 85% giống,
    ) throws IOException {
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(weaviateService.search(image,certainty,page,size))
                .build();
    }

    // API tạo schema mới
    // to do : them vao run application
    // postman test
    @PostMapping("/add-schema")
    public ResponseEntity<?> addSchema() {
        String schemaJson = """
        {
          "class": "Product",
          "description": "A product with image and ID",
          "vectorizer": "img2vec-neural",
          "moduleConfig": {
            "img2vec-neural": {
              "imageFields": ["image"]
            }
          },
          "properties": [
            {
              "name": "productId",
              "dataType": ["text"],
              "description": "The ID of the product"
            },
            {
              "name": "image",
              "dataType": ["blob"],
              "description": "Base64-encoded product image"
            }
          ]
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(schemaJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_SCHEMA, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // Thêm sản phẩm mới
    // postman test
    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(
            @RequestParam String productId,
            @RequestParam MultipartFile image) throws IOException {

        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

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
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_OBJECTS, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

   
    // Tìm kiếm sản phẩm bằng ảnh với ngưỡng lọc
    // postman test
    @PostMapping("/by-image")
    public ResponseEntity<?> searchByImage(
            @RequestParam MultipartFile image,
            @RequestParam(defaultValue = "0.85") double certainty // mặc định 85% giống
    ) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // GraphQL query có thêm certainty
        String graphql = "{ \"query\": \"{ Get { Product(nearImage: {image: \\\"" + base64Image +
                "\\\", certainty: " + certainty +
                "}, limit: 5) { productId _additional { certainty distance } } } }\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // postman test
    @GetMapping("/all-products")
    public ResponseEntity<?> getAllProducts() {
        String graphql = "{ \"query\": \"{ Get { Product { productId _additional { id } } } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        return restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);
    }

    // Xóa Product theo productId
    // postman test
    @DeleteMapping("/delete-by-productId/{productId}")
    public ResponseEntity<?> deleteByProductId(@PathVariable String productId) {
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
    if (objectIds.isEmpty()) {
      return ResponseEntity.badRequest().body("Không tìm thấy object với productId: " + productId);
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
    return ResponseEntity.ok("Đã xóa " + deleted + " Product với productId: " + productId);
    }

}


