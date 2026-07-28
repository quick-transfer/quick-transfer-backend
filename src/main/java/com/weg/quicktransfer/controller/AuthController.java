package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequestDTO requestDTO) {

        LoginResponseDTO response = userService.login(requestDTO);

        ResponseCookie cookie = ResponseCookie
                .from("JWT", response.token())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        SecurityContextHolder.clearContext();

        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(false) // true when on production
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/password-reset")
    public UserResponseDTO resetPassword(@RequestBody @Valid LoginRequestDTO requestDTO) {
        return userService.resetPassword(requestDTO);
    }
}