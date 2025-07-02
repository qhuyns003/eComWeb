package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.DetailAttribute;
import com.qhuyns.ecomweb.entity.ProductVariant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttributeResponse {
    String id;
    String name;
    List<DetailAttributeResponse> detailAttributes;
}
