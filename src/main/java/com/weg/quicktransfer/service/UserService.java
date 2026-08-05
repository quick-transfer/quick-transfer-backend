package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.auth.FirstAccessRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginRequestDTO;
import com.weg.quicktransfer.dto.auth.LoginResponseDTO;
import com.weg.quicktransfer.dto.auth.PasswordResetRequestDTO;
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
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.repo.specifications.UserSpecification;
import com.weg.quicktransfer.security.JwtService;
import com.weg.quicktransfer.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final AdminMapper adminMapper;

    private final CoordinatorMapper coordinatorMapper;

    private final ManagerMapper managerMapper;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository.findFirstByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

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
    public void completeFirstAccess(FirstAccessRequestDTO requestDTO) {
        User user = userRepository.findFirstByUsername(requestDTO.username())
.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getFirstLogin())) {
            throw new IllegalArgumentException("First access has already been completed.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        requestDTO.currentPassword()
                )
        );

        PasswordPolicy.validate(requestDTO.newPassword());
        user.setPassword(passwordEncoder.encode(requestDTO.newPassword()));
        user.setFirstLogin(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO resetPassword(LoginRequestDTO requestDTO) {

        User user = userRepository.findFirstByUsername(requestDTO.username())
                .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + requestDTO.username()));

        PasswordPolicy.validate(requestDTO.password());

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{14,}$";

        if (requestDTO.password() == null || !requestDTO.password().matches(passwordRegex)) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }

        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        if (Boolean.TRUE.equals(user.getFirstLogin())) {
            user.setFirstLogin(false);
        }

        userRepository.save(user);

        return mapUserToResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO resetPassword(
            String authenticatedUsername,
            PasswordResetRequestDTO requestDTO) {
        User user = userRepository.findFirstByUsername(authenticatedUsername)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user was not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(), requestDTO.currentPassword()));

        PasswordPolicy.validate(requestDTO.newPassword());
        user.setPassword(passwordEncoder.encode(requestDTO.newPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        return mapUserToResponseDTO(user);
    }

    @Transactional
    public void changePassword(String authenticatedUsername, String currentPassword, String newPassword) {
        User user = userRepository.findFirstByUsername(authenticatedUsername)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user was not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(), currentPassword));

        PasswordPolicy.validate(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
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
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapUserToResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsers(UserFilter filter) {
        Specification<User> spec = UserSpecification.getFilteredUsers(filter);
        List<User> users = userRepository.findAll(spec);
        return mapUsersToResponseDTOs(users);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(UserFilter filter, Pageable pageable) {
        Specification<User> spec = UserSpecification.getFilteredUsers(filter);
        return userRepository.findAll(spec, pageable).map(this::mapUserToResponseDTO);
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO updateRequestDTO, String requesterUsername) {
        User requester = userRepository.findFirstByUsername(requesterUsername)
                .orElseThrow(() -> new UserNotFoundException("User is not logged"));
        if (!(requester instanceof Admin) && !id.equals(requester.getId())) {
            throw new com.weg.quicktransfer.exception.UserNotAllowdException(
                    "User is not allowed to update this user");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        if (StringUtils.hasText(updateRequestDTO.name())) {
            user.setName(updateRequestDTO.name());
        }

        if (StringUtils.hasText(updateRequestDTO.password())) {
            PasswordPolicy.validate(updateRequestDTO.password());
            user.setPassword(passwordEncoder.encode(updateRequestDTO.password()));
            user.setTokenVersion(user.getTokenVersion() + 1);
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

    @Transactional
    public void revokeSessions(String username) {
        User user = userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    private List<UserResponseDTO> mapUsersToResponseDTOs(List<User> users) {
        return users.stream()
                .map(this::mapUserToResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapUserToResponseDTO(User user) {
        if (user instanceof Admin admin) {
            return adminMapper.toResponse(admin);
        }
        if (user instanceof Coordinator coordinator) {
            return coordinatorMapper.toResponse(coordinator);
        }
        if (user instanceof Manager manager) {
            return managerMapper.toResponse(manager);
        }
        throw new UserNotFoundException("Role not recognized for user ID: " + user.getId());
    }

}
