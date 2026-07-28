package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
import com.weg.quicktransfer.service.AdminService;
import com.weg.quicktransfer.service.CoordinatorService;
import com.weg.quicktransfer.service.ManagerService;
import com.weg.quicktransfer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create/admin")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody @Valid AdminRequestDTO adminRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.saveAdmin(adminRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<AdminResponseDTO> findAdminById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findAdminByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<AdminResponseDTO>> findAllAdmins() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllAdmin());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable UUID id,
            @RequestBody @Valid AdminUpdateRequestDTO updatedRequest
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, updatedRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
