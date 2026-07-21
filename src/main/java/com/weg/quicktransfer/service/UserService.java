package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.exception.InvalidPasswordException;
import com.weg.quicktransfer.exception.InvalidUsernameException;
import com.weg.quicktransfer.security.JwtService;
import com.weg.quicktransfer.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new LoginResponseDTO(token, "Bearer");
    }
}
