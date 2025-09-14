package com.qhuyns.ecomweb.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSnapshot {
    String id;
    String username;
    String fullName;
    String email;
    String phone;
    LocalDate dob;
    String role;
}
