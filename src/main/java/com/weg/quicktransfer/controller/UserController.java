package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.user.UserFilter;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<UserResponseDTO>> findUserByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(name));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(UserFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.searchUsers(filter, pageable));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO,
            Authentication authentication
            ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(id, userUpdateRequestDTO, authentication.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
