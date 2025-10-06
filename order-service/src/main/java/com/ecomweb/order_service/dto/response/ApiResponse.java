package com.ecomweb.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// tra cac trg k null
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
// bo qua cac thuoc tinh JSON khong biet, voi annotation @RequestBody thi dc ghi de mac dinh la true
// neu k them vao thi se bi loi vi JSON toi co truong ma dto k mapping dc (vi du truong hop su dung objectmapper de read 1 json sang dto)
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000;

    private String message;
    private T result;
}
