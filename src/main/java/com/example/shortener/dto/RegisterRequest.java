package com.example.shortener.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be 3-50 chars")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password must be at least 6 chars")
    private String password;
}