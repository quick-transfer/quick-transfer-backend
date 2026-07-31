package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.place.PlaceFilter;
import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.dto.place.PlaceUpdateRequestDTO;
import com.weg.quicktransfer.service.PlaceService;
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
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<PlaceResponseDTO> createPlace(@RequestBody @Valid PlaceRequestDTO placeRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.create(placeRequestDTO));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<PlaceResponseDTO> findPlaceById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findById(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("find/name/{name}")
    public ResponseEntity<List<PlaceResponseDTO>> findByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findByName(name));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<PlaceResponseDTO>> searchPlaces(PlaceFilter filter, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.searchPlaces(filter, pageable));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<PlaceResponseDTO>> findAllPlaces(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findAll(pageable));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<PlaceResponseDTO> updatePlace(
            @PathVariable UUID id,
            @RequestBody @Valid PlaceUpdateRequestDTO placeUpdateRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.update(id, placeUpdateRequestDTO));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable UUID id){
        placeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
