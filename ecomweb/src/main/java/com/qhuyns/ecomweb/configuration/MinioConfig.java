package com.qhuyns.ecomweb.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// du lieu cau truc: như bang user, product -> co cau truc schema hang cot ro rang
// du lieu phi cau truc: khong co dinh nghia du lieu co dinh chung, loai file, metadata (thong tin phu) hoan toan tu do va khac nhau
// du lieu phi cau truc thuong k luu trong db do k co schema chung va du lieu cua no thuong nang (video,img,txt..)
// -> doc/ghi nang va chi phi luu vao disk của db dat hon so voi ghi tren storage rieng
// neu luu tren thu muc du an se mat tinh bao mat

// de hien thi anh co 2 cach
// 1. set public bucket de co the truy cap tu do qua url
// 2. goi api de lay url anh xem co gioi han thoi gian (TH anh la bao mat nhu avt) - url ngau nhien
@Configuration
public class MinioConfig {
    @Value("${minio.url}")
    private String url;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}