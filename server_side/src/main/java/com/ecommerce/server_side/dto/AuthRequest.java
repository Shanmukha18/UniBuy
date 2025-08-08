package com.ecommerce.server_side.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email(message = "Invalid email format")
    private String email;
}
