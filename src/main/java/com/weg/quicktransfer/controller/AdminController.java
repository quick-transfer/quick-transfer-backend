package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.admin.AdminUpdateRequestDTO;
import com.weg.quicktransfer.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<AdminResponseDTO> findAdminById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findAdminByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminByName(name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Page<AdminResponseDTO>> searchAdmins(AdminFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.searchAdmins(filter, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<AdminResponseDTO>> findAllAdmins(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllAdmin(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable UUID id,
            @RequestBody @Valid AdminUpdateRequestDTO updatedRequest
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, updatedRequest));
    }
}
