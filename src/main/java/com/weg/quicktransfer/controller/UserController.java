package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.service.UserService;
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

    @PostMapping("/create/")
    public ResponseEntity<UserResponseDTO> createCoordinator(@RequestBody AdminRequestDTO adminRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(adminRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<UserResponseDTO> findCoordinatorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<UserResponseDTO>> findCoordintatorByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<UserResponseDTO>> findAllCoordinators() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateCoordinator(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDTO userUpdateRequestDTO
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCoordinator(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
