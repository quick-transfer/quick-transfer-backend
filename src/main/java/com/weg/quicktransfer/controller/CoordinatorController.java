package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.coordinator.CoordinatorFilter;
import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.service.CoordinatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coordinator")
@RequiredArgsConstructor
public class CoordinatorController {

    private final CoordinatorService coordinatorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CoordinatorResponseDTO> createCoordinator(@RequestBody @Valid CoordinatorRequestDTO coordinatorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordinatorService.create(coordinatorRequestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<CoordinatorResponseDTO> findCoordinatorById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<CoordinatorResponseDTO>> findCoordinatorByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findByName(name));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/search")
    public ResponseEntity<Page<CoordinatorResponseDTO>> searchCoordinators(CoordinatorFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.searchCoordinators(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<CoordinatorResponseDTO>> findAllCoordinators(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findAll(pageable));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('COORDINATOR') and #id == authentication.principal.id)")
    @PatchMapping("/update/{id}")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinator(
            @PathVariable UUID id,
            @RequestBody @Valid CoordinatorUpdateRequestDTO coordinatorUpdateRequestDTO,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(coordinatorService.update(id, coordinatorUpdateRequestDTO, authentication.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCoordinator(@PathVariable UUID id) {
        coordinatorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
