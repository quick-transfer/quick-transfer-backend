package com.weg.quicktransfer.security;

import com.weg.quicktransfer.exception.InvalidPasswordException;

public final class PasswordPolicy {

    private PasswordPolicy() {
    }

    public static void validate(String password) {
        boolean valid = password != null
                && password.length() >= 14
                && password.chars().anyMatch(Character::isUpperCase)
                && password.chars().anyMatch(Character::isLowerCase)
                && password.chars().anyMatch(Character::isDigit)
                && password.matches(".*[^A-Za-z0-9].*");

        if (!valid) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }
    }
}
