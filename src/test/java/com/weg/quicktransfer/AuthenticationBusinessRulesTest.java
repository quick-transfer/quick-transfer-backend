package com.weg.quicktransfer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repository.UserRepository;
import com.weg.quicktransfer.exception.UnauthorizedException;
import com.weg.quicktransfer.service.AuthService;
import com.weg.quicktransfer.service.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationBusinessRulesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    public void rule001_FirstLogin_ShouldRequestPasswordReset() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("new.user");
        when(mockUser.getPassword()).thenReturn("encrypted_password");
        when(mockUser.isFirstLogin()).thenReturn(true);

        when(userRepository.findByUsername("new.user")).thenReturn(Optional.of(mockUser));

        AuthResponse response = authService.login("new.user", "encrypted_password");

        assertTrue(response.isRequiresPasswordReset(), "System should ask to reset password on first login");
        assertEquals("/reset-password", response.getRedirectUrl());
    }

    @Test
    public void rule002_Login_ValidUserAndPassword_ShouldReturnToken() {
        User mockUser = mock(User.class);
        when(mockUser.isFirstLogin()).thenReturn(false);
        when(userRepository.findByUsername("valid.user")).thenReturn(Optional.of(mockUser));

        AuthResponse response = authService.login("valid.user", "correct_password");

        assertFalse(response.isRequiresPasswordReset());
        assertNotNull(response.getToken(), "Should generate a valid access token");
    }

    @Test
    public void rule002_SystemAccess_InvalidUser_ShouldThrowException() {
        when(userRepository.findByUsername("nonexistent.user")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login("nonexistent.user", "password123");
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }
}