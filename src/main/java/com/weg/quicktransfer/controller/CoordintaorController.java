package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorResponseDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.service.CoordinatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coordinator")
@RequiredArgsConstructor
public class CoordintaorController {

    private final CoordinatorService coordinatorService;

    @PostMapping("/create/coordinator")
    public ResponseEntity<CoordinatorResponseDTO> createCoordinator(@RequestBody CoordinatorRequestDTO coordinatorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordinatorService.create(coordinatorRequestDTO));
    }

    @GetMapping("/find/coordinator/id/{id}")
    public ResponseEntity<CoordinatorResponseDTO> findCoordinatorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findById(id));
    }

    @GetMapping("/find/coordinator/name/{name}")
    public ResponseEntity<List<CoordinatorResponseDTO>> findCoordinatorByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findByName(name));
    }

    @GetMapping("/find/coordinator/all")
    public ResponseEntity<List<CoordinatorResponseDTO>> findAllCoordinators() {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.findAll());
    }

    @PutMapping("/update/coordinator/{id}")
    public ResponseEntity<CoordinatorResponseDTO> updateCoordinator(
            @PathVariable Long id,
            @RequestBody CoordinatorUpdateRequestDTO coordinatorUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(coordinatorService.update(id, coordinatorUpdateRequestDTO));
    }

    @DeleteMapping("/delete/coordinator/{id}")
    public ResponseEntity<Void> deleteCoordinator(@PathVariable Long id) {
        coordinatorService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
