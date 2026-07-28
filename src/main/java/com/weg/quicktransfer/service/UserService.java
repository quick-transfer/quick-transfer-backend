package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.user.UserRequestDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.exception.FirstLoginException;
import com.weg.quicktransfer.exception.InvalidPasswordException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.mapper.UserMapper;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    private final CoordinatorRepository coordinatorRepository;
    private final CoordinatorMapper coordinatorMapper;

    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new UserNotFoundException("User not found with username: " + request.username());
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + request.username()));

        if (user.isFirstLogin()) {
            throw new FirstLoginException("It is user's first login");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(token, "Bearer");
    }

    @Transactional
    public UserResponseDTO resetPassword(LoginRequestDTO requestDTO) {

        User user = userRepository.findByUsername(requestDTO.username())
                .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + requestDTO.username()));

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{14,}$";
        if (requestDTO.password() == null || !requestDTO.password().matches(passwordRegex)) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        userRepository.save(user);

        return switch (user.getRole()) {
            case ADMIN -> adminMapper.toResponse((Admin) user);
            case MANAGER -> managerMapper.toResponse((Manager) user);
            case COORDINATOR -> coordinatorMapper.toResponse((Coordinator) user);
            default -> throw new UserNotFoundException("User with invalid role found");
        };
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {

        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists")));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Name can not be empty");
        }

        List<User> users = userRepository.findByNameContaining(username);

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
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO updateRequestDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Admin does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            user.setName(updateRequestDTO.name());
        }

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteById(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Admin does not exist");
        }

        userRepository.deleteById(id);
    }

}