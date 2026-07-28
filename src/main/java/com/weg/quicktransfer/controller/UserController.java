package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.user.UserFilter;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/find/id/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<UserResponseDTO>> findUserByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchCourses(UserFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.searchUsers(filter));
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
