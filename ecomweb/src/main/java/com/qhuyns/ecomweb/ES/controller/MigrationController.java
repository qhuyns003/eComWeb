package com.qhuyns.ecomweb.ES.controller;

import com.qhuyns.ecomweb.ES.service.ProductMigration;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final ProductMigration migrationService;

    /**
     * Import toàn bộ products từ MySQL sang Elasticsearch
     * URL: POST http://localhost:8080/admin/migration/import-to-elasticsearch
     */
    @PostMapping("/import-to-elasticsearch")
    public ApiResponse<String> importToElasticsearch() {
        migrationService.importAllProductsToElasticsearch();
        return ApiResponse.<String>builder()
                .result("Successfully imported all products to Elasticsearch")
                .build();
    }
}
