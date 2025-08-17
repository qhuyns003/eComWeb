package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomRequest {
    // list tu rqbody luon la mutable -> phai khoi tao moi voi tham so la mutable
    List<String> members = new ArrayList<>();
    String name;
}
