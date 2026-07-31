package com.weg.quicktransfer.dto.auth;

public record ChangePasswordRequestDto(
        String currentPassword,
        String newPassword
) {
}
