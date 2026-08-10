package com.weg.quicktransfer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.exception.FirstLoginException;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.security.JwtService;
import com.weg.quicktransfer.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationBusinessRulesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("System should throw FirstLoginException on first login")
    public void firstLoginShouldRequestPasswordReset() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("new.user", "encrypted_password");

        User mockUser = mock(User.class);
        when(mockUser.getFirstLogin()).thenReturn(true);

        when(userRepository.findFirstByUsername("new.user")).thenReturn(Optional.of(mockUser));

        FirstLoginException exception = assertThrows(FirstLoginException.class, () -> {
            userService.login(requestDTO);
        });

        assertEquals("It is user's first login", exception.getMessage());
    }

    @Test
    @DisplayName("Should generate a valid access token")
    public void loginValidUserAndPassword_ShouldReturnToken() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("valid.user", "correct_password");

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("valid.user");
        when(mockUser.getFirstLogin()).thenReturn(false);

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findFirstByUsername("valid.user")).thenReturn(Optional.of(mockUser));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked_jwt_token");

        LoginResponseDTO response = userService.login(requestDTO);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.token());
        assertEquals("Bearer", response.type());
    }

    @Test
    @DisplayName("System access with invalid user should return generic bad credentials")
    public void systemAccessInvalidUserShouldThrowException() {
        LoginRequestDTO falseLoginRequestDTO = new LoginRequestDTO("nonexistent.user", "password123");

        when(userRepository.findFirstByUsername("nonexistent.user")).thenReturn(Optional.empty());
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.login(falseLoginRequestDTO);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
