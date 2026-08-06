package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.application.VacancyApplicationRequestDTO;
import com.weg.quicktransfer.dto.application.VacancyApplicationResponseDTO;
import com.weg.quicktransfer.dto.application.VacancyApplicationUpdateRequestDTO;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.VacancyApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class VacancyApplicationController {
    private final VacancyApplicationService applicationService;

    @PreAuthorize("hasRole('COORDINATOR')")
    @PostMapping("/create")
    public ResponseEntity<VacancyApplicationResponseDTO> create(
            @RequestBody @Valid VacancyApplicationRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.create(request, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<VacancyApplicationResponseDTO>> findAll(
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(applicationService.findAll(pageable, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<VacancyApplicationResponseDTO> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(applicationService.findById(id, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<VacancyApplicationResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid VacancyApplicationUpdateRequestDTO request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(applicationService.update(id, request, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        applicationService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
}
