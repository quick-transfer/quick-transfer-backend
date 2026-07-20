// package com.weg.quicktransfer;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
// import com.weg.quicktransfer.model.User;
// import com.weg.quicktransfer.repo.UserRepository;
// import com.weg.quicktransfer.exception.UnauthorizedException;
// import com.weg.quicktransfer.service.AuthService;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;

// @ExtendWith(MockitoExtension.class)
// public class AuthenticationBusinessRulesTest {

//     @Mock
//     private UserRepository userRepository;

//     @InjectMocks
//     private AuthService authService;

//     @Test
//     @DisplayName("System should ask to reset password on first login")
//     public void firstLoginShouldRequestPasswordReset() {
//         User mockUser = mock(User.class);
//         when(mockUser.getUserName()).thenReturn("new.user");
//         when(mockUser.getPassword()).thenReturn("encrypted_password");
//         when(mockUser.isFirstLogin()).thenReturn(true);

//         when(userRepository.findByUserName("new.user")).thenReturn(Optional.of(mockUser));

//         LoginResponseDTO response = authService.login("new.user", "encrypted_password");

//         assertTrue(response.isRequiresPasswordReset());
//         assertEquals("/reset-password", response.getRedirectUrl());
//     }

//     @Test
//     @DisplayName("Should generate a valid access token")
//     public void loginValidUserAndPassword_ShouldReturnToken() {
//         User mockUser = mock(User.class);
//         when(mockUser.getUserName()).thenReturn("valid.user");
//         when(mockUser.getPassword()).thenReturn("correct_password");
//         when(mockUser.isFirstLogin()).thenReturn(false);

//         when(userRepository.findByUserName("valid.user")).thenReturn(Optional.of(mockUser));

//         LoginResponseDTO response = authService.login("valid.user", "correct_password");

//         assertFalse(response.isRequiresPasswordReset());
//         assertNotNull(response.getToken());
//     }

//     @Test
//     @DisplayName("System access with invalid user should throw exception")
//     public void systemAccessInvalidUserShouldThrowException() {
//         when(userRepository.findByUserName("nonexistent.user")).thenReturn(Optional.empty());

//         UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
//             authService.login("nonexistent.user", "password123");
//         });

//         assertEquals("Invalid credentials", exception.getMessage());
//     }
// }