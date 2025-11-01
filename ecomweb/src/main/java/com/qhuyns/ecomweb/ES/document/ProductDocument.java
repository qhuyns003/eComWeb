package com.qhuyns.ecomweb.ES.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDocument {

    @Id
    String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    String description;

    @Field(type = FieldType.Keyword)
    String categoryId;

    @Field(type = FieldType.Keyword)
    String shopId;

    @Field(type = FieldType.Double)
    BigDecimal price;

    @Field(type = FieldType.Keyword)
    String status;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;

    // Main image cho display
    @Field(type = FieldType.Keyword)
    String mainImageId;

    @Field(type = FieldType.Keyword)
    String mainImageUrl;
}
