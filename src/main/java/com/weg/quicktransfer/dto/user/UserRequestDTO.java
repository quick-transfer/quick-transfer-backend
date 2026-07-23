package com.weg.quicktransfer.dto.user;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,

    @NotBlank(message = "Username must not be blank")
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String username,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    String email,
    
    @NotBlank(message = "Password must not be blank")
    @Length(min = 14, message = "Password must be at least 14 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "The password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    String password
) {
}
