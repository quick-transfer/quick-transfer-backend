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
            @RequestBody AdminUpdateRequestDTO updatedRequest
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, updatedRequest));
    }

    @DeleteMapping("/delete/admin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/create/manager")
    public ResponseEntity<ManagerResponseDTO> createManager(@RequestBody ManagerRequestDTO managerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managerService.create(managerRequestDTO));
    }

    @GetMapping("/find/manager/id/{id}")
    public ResponseEntity<AdminResponseDTO> findManagerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findById(id));
    }

    @GetMapping("/find/manager/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findCManagerByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findByName(name));
    }

    @GetMapping("/find/manager/all")
    public ResponseEntity<List<AdminResponseDTO>> findAllManagerss() {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findAll());
    }

    @PutMapping("/update/manager/{id}")
    public ResponseEntity<AdminResponseDTO> updateManager(
            @PathVariable Long id,
            @RequestBody ManagerUpdateRequestDTO updateRequestDTO
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.update(id, updateRequestDTO));
    }

    @DeleteMapping("/delete/manager/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/create/coordinator")
    public ResponseEntity<AdminResponseDTO> createCoordinator(@RequestBody AdminRequestDTO adminRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.saveAdmin(adminRequestDTO));
    }

    @GetMapping("/find/coordinator/id/{id}")
    public ResponseEntity<AdminResponseDTO> findCoordinatorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminById(id));
    }

    @GetMapping("/find/coordinator/name/{name}")
    public ResponseEntity<List<AdminResponseDTO>> findCoordintatorByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAdminByName(name));
    }

    @GetMapping("/find/coordinator/all")
    public ResponseEntity<List<AdminResponseDTO>> findAllCoordinators() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findAllAdmin());
    }

    @PutMapping("/update/coordinator/{id}")
    public ResponseEntity<AdminResponseDTO> updateCoordinator(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminById(id, name, email));
    }

    @DeleteMapping("/delete/coordinator/{id}")
    public ResponseEntity<Void> deleteCoordinator(@PathVariable Long id) {
        adminService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
