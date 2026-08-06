package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.shift.OperationalShiftResponseDTO;
import com.weg.quicktransfer.dto.shift.OperationalShiftUpdateRequestDTO;
import com.weg.quicktransfer.dto.shift.ShiftAssignmentRequestDTO;
import com.weg.quicktransfer.dto.transfer.TransferRequestResponseDTO;
import com.weg.quicktransfer.security.UserPrincipal;
import com.weg.quicktransfer.service.OperationalShiftService;
import com.weg.quicktransfer.service.ShiftTransferRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
public class OperationalShiftController {
    private final OperationalShiftService shiftService;
    private final ShiftTransferRequestService transferService;

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/find/all")
    public ResponseEntity<List<OperationalShiftResponseDTO>> findAll() {
        return ResponseEntity.ok(shiftService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<OperationalShiftResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid OperationalShiftUpdateRequestDTO input) {
        return ResponseEntity.ok(shiftService.update(id, input));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @PatchMapping("/student/{studentId}")
    public ResponseEntity<TransferRequestResponseDTO> assignStudent(
            @PathVariable UUID studentId,
            @RequestBody @Valid ShiftAssignmentRequestDTO input,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(transferService.assignManually(
                studentId, input.targetShiftId(), input.reason(), principal));
    }
}
