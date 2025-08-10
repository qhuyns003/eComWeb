package com.qhuyns.ecomweb.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    // API tạo schema mới
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
    @PostMapping("/by-image")
    public ResponseEntity<?> searchByImage(
            @RequestParam MultipartFile image,
            @RequestParam(defaultValue = "0.85") double certainty // mặc định 85% giống
    ) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // GraphQL query có thêm certainty
        String graphql = "{ \"query\": \"{ Get { Product(nearImage: {image: \\\"" + base64Image +
                "\\\", certainty: " + certainty +
                "}, limit: 5) { productId image _additional { certainty distance } } } }\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(graphql, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WEAVIATE_GRAPHQL, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
