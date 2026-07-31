package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.auth.AuthenticatedUserResponseDTO;
import com.weg.quicktransfer.dto.auth.FirstAccessRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.auth.PasswordResetRequestDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Value("${app.jwt.expiration}")
    private long expirationMs;

    @Value("${app.security.cookie.secure:true}")
    private boolean secureCookie;

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserResponseDTO> login(@RequestBody @Valid LoginRequestDTO requestDTO) {

        LoginResponseDTO response = userService.login(requestDTO);

        ResponseCookie cookie = ResponseCookie
                .from("JWT", response.token())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .build();

        AuthenticatedUserResponseDTO authenticatedUser = new AuthenticatedUserResponseDTO(
                response.userId(),
                response.name(),
                response.username(),
                response.role()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authenticatedUser);
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }

    @PostMapping("/first-access")
    public ResponseEntity<Void> completeFirstAccess(
            @RequestBody @Valid FirstAccessRequestDTO requestDTO) {
        userService.completeFirstAccess(requestDTO);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        SecurityContextHolder.clearContext();

        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @PostMapping("/password-reset")
    public UserResponseDTO resetPassword(
            @RequestBody @Valid PasswordResetRequestDTO requestDTO,
            org.springframework.security.core.Authentication authentication) {
        return userService.resetPassword(authentication.getName(), requestDTO);
    }
}
