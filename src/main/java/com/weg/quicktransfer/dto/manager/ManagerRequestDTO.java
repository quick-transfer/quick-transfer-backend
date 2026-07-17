package com.weg.quicktransfer.dto.manager;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ManagerRequestDTO(
    @NotBlank
    @Size(max = 100)
    String name,
    @NotBlank
    @Size(max = 100)
    String userName,
    @NotBlank
    @Email
    String email,
    @NotBlank
    @Length(min = 14)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "a senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
    String password,
    @NotBlank
    String section
) {
}
