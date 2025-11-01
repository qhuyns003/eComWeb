package com.qhuyns.ecomweb.ES.repository;

import com.qhuyns.ecomweb.ES.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductESRepository extends ElasticsearchRepository<ProductDocument, String> {
    // Spring Data tự generate basic CRUD methods
}
