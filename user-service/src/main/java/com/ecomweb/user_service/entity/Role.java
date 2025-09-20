package com.ecomweb.user_service.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    String name;
    String description;
}
