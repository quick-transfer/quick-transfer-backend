package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.settings.SystemSettingsResponseDTO;
import com.weg.quicktransfer.dto.settings.SystemSettingsUpdateRequestDTO;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.SystemSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SystemSettingsController {
    private final SystemSettingsService service;

    @GetMapping
    public ResponseEntity<SystemSettingsResponseDTO> find() {
        return ResponseEntity.ok(service.find());
    }

    @PatchMapping
    public ResponseEntity<SystemSettingsResponseDTO> update(
            @RequestBody @Valid SystemSettingsUpdateRequestDTO input,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.update(input, principal));
    }
}
