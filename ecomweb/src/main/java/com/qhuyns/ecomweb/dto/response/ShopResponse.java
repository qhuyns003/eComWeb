package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopResponse {

    String id;
    String name;
    String description;
    String address;
    String status;
    LocalDateTime createdAt;
    ShopAddressResponse shopAddressResponse;

}
