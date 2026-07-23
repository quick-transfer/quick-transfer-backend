package com.weg.quicktransfer.dto.coordinator;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CoordinatorUpdateRequestDTO(
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,

    @Size(max = 100, message = "The username can have a maximum of 100 characters")
    String username,

    @Email(message = "Email must be a valid email address")
    String email,
    
    @Length(min = 14, message = "Password must be at least 14 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "The password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    String password
) {
}
