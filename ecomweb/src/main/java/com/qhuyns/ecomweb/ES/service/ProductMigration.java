package com.qhuyns.ecomweb.ES.service;

import com.qhuyns.ecomweb.ES.document.ProductDocument;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductImage;
import com.qhuyns.ecomweb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductMigration {

    private final ProductRepository productRepository;
    private final ProductESService elasticsearchService;

    /**
     * Import toàn bộ products từ MySQL sang Elasticsearch
     */
    public void importAllProductsToElasticsearch() {
        log.info("Starting bulk import from MySQL to Elasticsearch...");

        List<Product> allProducts = productRepository.findAll();
        log.info("Found {} products in MySQL", allProducts.size());

        List<ProductDocument> documents = allProducts.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());

        elasticsearchService.bulkImport(documents);

        log.info("Successfully imported {} products to Elasticsearch", documents.size());
    }

    private ProductDocument convertToDocument(Product product) {
        // Tìm main image
        String mainImageId = null;
        String mainImageUrl = null;

        for (ProductImage img : product.getImages()) {
            if (img.getIsMain() != null && img.getIsMain()) {
                mainImageId = img.getId();
                mainImageUrl = img.getUrl();
                break;
            }
        }

        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .shopId(product.getShopId())
                .mainImageId(mainImageId)
                .mainImageUrl(mainImageUrl)
                .build();
    }
}