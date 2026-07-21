package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create/admin")
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody AdminRequestDTO adminRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.saveAdmin(adminRequestDTO));
    }

    @GetMapping("/find/admin/id/{id}")
    public ResponseEntity<AdminResponseDTO> findAdminById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminById(id));
    }

    @GetMapping("/find/admin/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findAdminByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminByName(name));
    }

    @GetMapping("/find/admin/all")
    public ResponseEntity<List<AdminResponseDTO>> findAllAdmins() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllAdmin());
    }

    @PutMapping("/update/admin/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, username, email));
    }

    @DeleteMapping("/delete/admin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
