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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coordinator")
@RequiredArgsConstructor
public class CoordinatorController {

    private final CoordinatorService coordinatorService;

    @PostMapping("/create")
    public ResponseEntity<CoordinatorResponseDTO> createCoordinator(@RequestBody @Valid CoordinatorRequestDTO coordinatorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordinatorService.create(coordinatorRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<CoordinatorResponseDTO> findCoordinatorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<CoordinatorResponseDTO> findCoordinatorByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CoordinatorResponseDTO>> searchCoordinators(CoordinatorFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.searchCoordinators(filter));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<CoordinatorResponseDTO>> findAllCoordinators() {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinator(
            @PathVariable Long id,
            @RequestBody @Valid CoordinatorUpdateRequestDTO coordinatorUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.update(id, coordinatorUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCoordinator(@PathVariable Long id) {
        coordinatorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
