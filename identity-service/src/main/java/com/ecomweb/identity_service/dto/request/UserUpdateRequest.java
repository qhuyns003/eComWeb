package com.ecomweb.identity_service.dto.request;

import com.ecomweb.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String oldPassword;
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
    String repeatPassword;
    String fullName;
    String email;
    String phone;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;

    List<String> roles;

    String keycloakId;
}
