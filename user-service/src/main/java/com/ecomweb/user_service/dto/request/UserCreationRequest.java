package com.ecomweb.user_service.dto.request;


import com.ecomweb.user_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
    String confirmPassword;

    String fullName;
    String email;
    String phone;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    LocalDate dob;
}
