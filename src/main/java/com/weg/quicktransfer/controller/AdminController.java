package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/admin")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody @Valid AdminRequestDTO adminRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.saveAdmin(adminRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<AdminResponseDTO> findAdminById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findAdminByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdminResponseDTO>> searchAdmins(AdminFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.searchAdmins(filter));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<AdminResponseDTO>> findAllAdmins() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllAdmin());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody @Valid AdminUpdateRequestDTO updatedRequest
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, updatedRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
