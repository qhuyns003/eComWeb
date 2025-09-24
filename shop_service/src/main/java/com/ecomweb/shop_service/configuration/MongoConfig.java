package com.ecomweb.shop_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
// giup mongo doc duoc 1 so annoation dacbiet nhu CreatedDate (spring data )
// CreationTimeStamp cua jpa nen k dung dc
// Id la cua spring data, duoc cau hinh mac dinh nhan dien nen k can enable van gen dc


// cau hinh replica set +config + annatation enabletransactionmanagement de co the bat transactional tren mongo

public class MongoConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

