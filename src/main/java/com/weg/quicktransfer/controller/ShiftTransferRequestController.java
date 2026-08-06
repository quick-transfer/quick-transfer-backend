package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.transfer.TransferRequestCreateDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResolveDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResponseDTO;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.ShiftTransferRequestService;
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
@RequestMapping("/transfer-request")
@RequiredArgsConstructor
public class ShiftTransferRequestController {
    private final ShiftTransferRequestService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @PostMapping("/create")
    public ResponseEntity<TransferRequestResponseDTO> create(
            @RequestBody @Valid TransferRequestCreateDTO input,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(input, principal));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<TransferRequestResponseDTO>> findAll(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(service.findAll(status, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @PatchMapping("/resolve/{id}")
    public ResponseEntity<TransferRequestResponseDTO> resolve(
            @PathVariable UUID id,
            @RequestBody @Valid TransferRequestResolveDTO input,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.resolve(id, input, principal));
    }
}
