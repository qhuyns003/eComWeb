package com.ecomweb.identity_service.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    // khong can @Id cung dc, nhung k co se k dung dc method findById co san cua spring data, tu custome
    // phai dat ten la id neu trung
    @Id
    String id;
    String description;
}
