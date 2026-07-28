package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.user.UserFilter;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.exception.FirstLoginException;
import com.weg.quicktransfer.exception.InvalidPasswordException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.mapper.AdminMapper;
import com.weg.quicktransfer.mapper.CoordinatorMapper;
import com.weg.quicktransfer.mapper.ManagerMapper;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.AdminRepository;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.repo.specifications.InterviewSpecification;
import com.weg.quicktransfer.repo.specifications.UserSpecification;
import com.weg.quicktransfer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;

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

        User user = userRepository.findFirstByUsername(request.username())
                .orElse(userRepository.findFirstByName(request.username())
                    .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + request.username())));

        if (user.getFirstLogin()) {
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

        User user = userRepository.findFirstByUsername(requestDTO.username())
                .orElse(userRepository.findFirstByName(requestDTO.username())
                    .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + requestDTO.username())));

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
        };
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id can not be less than 1");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User do not exists"));

        switch (user.getRole()) {
            case ADMIN -> {
                return adminMapper.toResponse(adminRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
            }
            case COORDINATOR -> {
                return coordinatorMapper.toResponse(coordinatorRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
            }
            case MANAGER -> {
                return managerMapper.toResponse(managerRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
            }
            default -> throw new UserNotFoundException("User do not exists");
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Name can not be empty");
        }

        List<User> users = userRepository.findByNameContaining(username);

        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();

        for (User user : users) {
            switch (user.getRole()) {
                case ADMIN -> {
                    AdminResponseDTO adminResponseDTO = adminMapper.toResponse(adminRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(adminResponseDTO);
                }
                case COORDINATOR -> {
                    CoordinatorResponseDTO coordinatorResponseDTO = coordinatorMapper.toResponse(coordinatorRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(coordinatorResponseDTO);
                }
                case MANAGER -> {
                    ManagerResponseDTO managerResponseDTO = managerMapper.toResponse(managerRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(managerResponseDTO);
                }
                default -> throw new UserNotFoundException("User do not exists");
            }
        }

        return userResponseDTOS;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();

        for (User user : users) {
            switch (user.getRole()) {
                case ADMIN -> {
                    AdminResponseDTO adminResponseDTO = adminMapper.toResponse(adminRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(adminResponseDTO);
                }
                case COORDINATOR -> {
                    CoordinatorResponseDTO coordinatorResponseDTO = coordinatorMapper.toResponse(coordinatorRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(coordinatorResponseDTO);
                }
                case MANAGER -> {
                    ManagerResponseDTO managerResponseDTO = managerMapper.toResponse(managerRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(managerResponseDTO);
                }
                default -> throw new UserNotFoundException("User do not exists");
            }
        }

        return userResponseDTOS;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(UserFilter filter) {
        Specification<User> spec = UserSpecification.getFilteredUsers(filter);

        List<User> users = userRepository.findAll(spec);

        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();

        for (User user : users) {
            switch (user.getRole()) {
                case ADMIN -> {
                    AdminResponseDTO adminResponseDTO = adminMapper.toResponse(adminRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(adminResponseDTO);
                }
                case COORDINATOR -> {
                    CoordinatorResponseDTO coordinatorResponseDTO = coordinatorMapper.toResponse(coordinatorRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(coordinatorResponseDTO);
                }
                case MANAGER -> {
                    ManagerResponseDTO managerResponseDTO = managerMapper.toResponse(managerRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User do not exists")));
                    userResponseDTOS.add(managerResponseDTO);
                }
                default -> throw new UserNotFoundException("User do not exists");
            }
        }

        return userResponseDTOS;
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

        userRepository.save(user);

        return switch (user.getRole()) {
            case ADMIN -> adminMapper.toResponse((Admin) user);
            case MANAGER -> managerMapper.toResponse((Manager) user);
            case COORDINATOR -> coordinatorMapper.toResponse((Coordinator) user);
        };
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