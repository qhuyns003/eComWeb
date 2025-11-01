package com.qhuyns.ecomweb.ES.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.qhuyns.ecomweb.ES.document.ProductDocument;
import com.qhuyns.ecomweb.ES.repository.ProductESRepository;
import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.ProductFilterRequest;
import com.qhuyns.ecomweb.dto.response.ProductImageResponse;
import com.qhuyns.ecomweb.dto.response.ProductResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductESService {

    ElasticsearchClient elasticsearchClient;
    ProductESRepository elasticRepository;

    /**
     * SEARCH PRODUCTS - Thay thế ProductRepository.searchProduct()
     */
    public Page<ProductResponse> searchProducts(
            int page,
            int size,
            String search,
            String status,
            ProductFilterRequest filterRequest,
            List<String> shopIds) {

        try {
            // 1. Build Bool Query
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();

            // 2. Text search
            if (StringUtils.hasText(search)) {
                boolQuery.must(MultiMatchQuery.of(m -> m
                        .query(search)
                        .fields("name", "description")
                        .fuzziness("AUTO")
                )._toQuery());
            }

            // 3. Status filter
            if (StringUtils.hasText(status)) {
                boolQuery.filter(TermQuery.of(t -> t
                        .field("status")
                        .value(status)
                )._toQuery());
            }

            // 4. Category filter
            if (filterRequest.getCategoryId() != null) {
                boolQuery.filter(TermQuery.of(t -> t
                        .field("categoryId")
                        .value(filterRequest.getCategoryId())
                )._toQuery());
            }

            // 5. Price range filter - SỬA LẠI
            if (filterRequest.getMinPrice() != null || filterRequest.getMaxPrice() != null) {
                boolQuery.filter(RangeQuery.of(r -> {
                    r.field("price");

                    if (filterRequest.getMinPrice() != null) {
                        r.gte(JsonData.of(filterRequest.getMinPrice().doubleValue()));
                    }
                    if (filterRequest.getMaxPrice() != null) {
                        r.lte(JsonData.of(filterRequest.getMaxPrice().doubleValue()));
                    }

                    return r;
                })._toQuery());
            }


            // 6. Shop filter
            if (shopIds != null && !shopIds.isEmpty()) {
                boolQuery.filter(TermsQuery.of(t -> t
                        .field("shopId")
                        .terms(terms -> terms.value(
                                shopIds.stream()
                                        .map(FieldValue::of)
                                        .collect(Collectors.toList())
                        ))
                )._toQuery());
            }

            // 7. Build sort options
            List<co.elastic.clients.elasticsearch._types.SortOptions> sortOptions = new ArrayList<>();

            if ("asc".equalsIgnoreCase(filterRequest.getSortPrice())) {
                sortOptions.add(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Asc))
                ));
            } else if ("desc".equalsIgnoreCase(filterRequest.getSortPrice())) {
                sortOptions.add(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .field(f -> f.field("price").order(SortOrder.Desc))
                ));
            } else {
                sortOptions.add(co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                        .score(sc -> sc.order(SortOrder.Desc))
                ));
            }

            // 8. Execute search
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query(boolQuery.build()._toQuery())
                    .sort(sortOptions)
                    .from(page * size)
                    .size(size)
                    .trackTotalHits(th -> th.enabled(true))
            );

            SearchResponse<ProductDocument> response = elasticsearchClient.search(
                    searchRequest,
                    ProductDocument.class
            );

            // 9. Convert to ProductResponse
            List<ProductResponse> products = response.hits().hits().stream()
                    .map(hit -> {
                        ProductDocument doc = hit.source();
                        if (doc == null) return null;

                        return ProductResponse.builder()
                                .id(doc.getId())
                                .name(doc.getName())
                                .description(doc.getDescription())
                                .price(doc.getPrice())
                                .status(doc.getStatus())
                                .createdAt(doc.getCreatedAt())
                                .images(List.of(ProductImageResponse.builder()
                                        .id(doc.getMainImageId())
                                        .url(ImagePrefix.IMAGE_PREFIX + doc.getMainImageUrl())
                                        .isMain(true)
                                        .build()))
                                .build();
                    })
                    .filter(p -> p != null)
                    .collect(Collectors.toList());

            long total = response.hits().total().value();

            return new PageImpl<>(products, PageRequest.of(page, size), total);

        } catch (Exception e) {
            log.error("Elasticsearch search error: ", e);
            throw new RuntimeException("Search failed", e);
        }
    }

    /**
     * INDEX PRODUCT - Khi CREATE hoặc UPDATE
     */
    public void indexProduct(ProductDocument document) {
        try {
            elasticRepository.save(document);
            log.info("Indexed product to Elasticsearch: {}", document.getId());
        } catch (Exception e) {
            log.error("Failed to index product {}: ", document.getId(), e);
        }
    }

    /**
     * DELETE PRODUCT - Khi DELETE
     */
    public void deleteProduct(String productId) {
        try {
            elasticRepository.deleteById(productId);
            log.info("Deleted product from Elasticsearch: {}", productId);
        } catch (Exception e) {
            log.error("Failed to delete product {}: ", productId, e);
        }
    }

    /**
     * BULK IMPORT - Import toàn bộ products từ MySQL
     */
    public void bulkImport(List<ProductDocument> documents) {
        try {
            elasticRepository.saveAll(documents);
            log.info("Bulk imported {} products to Elasticsearch", documents.size());
        } catch (Exception e) {
            log.error("Bulk import failed: ", e);
            throw new RuntimeException("Bulk import failed", e);
        }
    }
}
