package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.place.PlaceRequestDTO;
import com.weg.quicktransfer.dto.place.PlaceResponseDTO;
import com.weg.quicktransfer.dto.place.PlaceUpdateRequestDTO;
import com.weg.quicktransfer.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping("/create")
    public ResponseEntity<PlaceResponseDTO> createPlace(@RequestBody @Valid PlaceRequestDTO placeRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.create(placeRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<PlaceResponseDTO> findPlaceById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findById(id));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<PlaceResponseDTO>> findAllPlaces(){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<PlaceResponseDTO> updatePlace(
            @PathVariable UUID id,
            @RequestBody @Valid PlaceUpdateRequestDTO placeUpdateRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(placeService.update(id, placeUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlace(UUID id){
        placeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
