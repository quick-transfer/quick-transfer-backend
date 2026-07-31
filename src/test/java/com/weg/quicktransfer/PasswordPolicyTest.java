package com.weg.quicktransfer;

import com.weg.quicktransfer.exception.InvalidPasswordException;
import com.weg.quicktransfer.security.PasswordPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyTest {

    @Test
    void shouldAcceptStrongPassword() {
        assertDoesNotThrow(() -> PasswordPolicy.validate("StrongP@ssword1!"));
    }

    @Test
    void shouldRejectPasswordWithoutLowercaseCharacter() {
        assertThrows(InvalidPasswordException.class,
                () -> PasswordPolicy.validate("STRONGP@SSWORD1!"));
    }
}
