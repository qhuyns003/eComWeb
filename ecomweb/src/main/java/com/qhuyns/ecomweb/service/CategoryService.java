package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.ProductOverviewResponse;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.util.RedisCacheHelper;
import com.qhuyns.ecomweb.util.RedisKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    RedisCacheHelper cacheHelper;

    public List<CategoryResponse> getAll() throws Exception {
        // Kiểm tra cache
        List<CategoryResponse> cachedCategories = cacheHelper.getFromCache(RedisKey.GET_ALL_CATEGORY.getKey(),  List.class);
        if (cachedCategories != null) {
            return cachedCategories;
        }
        List<CategoryResponse> categoryResponses = categoryRepository.findAll().stream().map(cat -> categoryMapper.toCategoryResponse(cat)).collect(Collectors.toList());
        cacheHelper.saveToCache(RedisKey.GET_ALL_CATEGORY.getKey(), categoryResponses, RedisKey.GET_ALL_CATEGORY.getTtl());

        return categoryResponses;
    }
}
