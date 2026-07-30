package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        User user = userRepository.findFirstByUsername(request.username())
                .orElseGet(() -> userRepository.findFirstByName(request.username())
                        .orElseThrow(() -> new UserNotFoundException("User not found with: " + request.username())));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.password()
                )
        );

        if (Boolean.TRUE.equals(user.getFirstLogin())) {
            throw new FirstLoginException("It is user's first login");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional
    public UserResponseDTO resetPassword(LoginRequestDTO requestDTO) {

        User user = userRepository.findFirstByUsername(requestDTO.username())
                .orElseGet(() -> userRepository.findFirstByName(requestDTO.username())
                        .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + requestDTO.username())));

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{14,}$";
        if (requestDTO.password() == null || !requestDTO.password().matches(passwordRegex)) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        if (Boolean.TRUE.equals(user.getFirstLogin())) {
            user.setFirstLogin(false);
        }

        userRepository.save(user);

        return mapUserToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        return mapUserToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByUsername(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        List<User> users = userRepository.searchUsersByName(name);
        return mapUsersToResponseDTOs(users);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();
        return mapUsersToResponseDTOs(users);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(UserFilter filter) {
        Specification<User> spec = UserSpecification.getFilteredUsers(filter);
        List<User> users = userRepository.findAll(spec);
        return mapUsersToResponseDTOs(users);
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO updateRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            user.setName(updateRequestDTO.name());
        }

        userRepository.save(user);
        return mapUserToResponseDTO(user);
    }

    @Transactional
    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User does not exist");
        }
        userRepository.deleteById(id);
    }

    private List<UserResponseDTO> mapUsersToResponseDTOs(List<User> users) {
        return users.stream()
                .map(this::mapUserToResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapUserToResponseDTO(User user) {
        return switch (user.getRole()) {
            case ADMIN -> adminMapper.toResponse(
                    adminRepository.findById(user.getId())
                            .orElseThrow(() -> new UserNotFoundException("Admin does not exist for ID: " + user.getId()))
            );
            case COORDINATOR -> coordinatorMapper.toResponse(
                    coordinatorRepository.findById(user.getId())
                            .orElseThrow(() -> new UserNotFoundException("Coordinator does not exist for ID: " + user.getId()))
            );
            case MANAGER -> managerMapper.toResponse(
                    managerRepository.findById(user.getId())
                            .orElseThrow(() -> new UserNotFoundException("Manager does not exist for ID: " + user.getId()))
            );
            default -> throw new UserNotFoundException("Role not recognized for user ID: " + user.getId());
        };
    }

}
