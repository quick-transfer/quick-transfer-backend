package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.user.UserRequestDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.UserMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(token, "Bearer");
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists")));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name can not be empty");
        }

        List<User> users = userRepository.findByNameContaining(name);

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequestDTO updateRequestDTO) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Admin does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            user.setName(updateRequestDTO.name());
        }

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Admin does not exist");
        }

        userRepository.deleteById(id);
    }

}