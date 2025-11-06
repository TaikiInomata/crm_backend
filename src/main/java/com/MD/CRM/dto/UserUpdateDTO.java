package com.MD.CRM.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 150, message = "Fullname must not exceed 150 characters")
    private String fullname;

    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    private String password;
}
